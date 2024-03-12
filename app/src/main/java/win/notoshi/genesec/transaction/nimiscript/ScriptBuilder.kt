package win.notoshi.genesec.transaction.nimiscript

import fr.acinq.bitcoin.OP_CHECKLOCKTIMEVERIFY
import fr.acinq.bitcoin.OP_CHECKSIG
import fr.acinq.bitcoin.OP_DROP
import fr.acinq.bitcoin.OP_PUSHDATA
import win.notoshi.genesec.utils.ShiftTo.ByteArrayToHex
import win.notoshi.genesec.utils.ShiftTo.HexToByteArray
import win.notoshi.genesec.utils.ShiftTo.toByteArray
import java.nio.ByteBuffer
import java.nio.ByteOrder

class ScriptBuilder {

    // * https://github.com/bitcoin/bips/blob/master/bip-0065.mediawiki
    fun TimeLock(blockNumber: Int, publicKey: ByteArray): String {
        if (blockNumber < 0) {
            throw IllegalArgumentException("Block number must be non-negative")
        }

        /*
         * Block Number: 766910
         * PUSHDATA: คำนวณจากค่าที่เป็นค่า DEC นำไปแปลงเป็น HEX จากนั้นแปลงต่อเป็น LITTLE ENDIAN
         *
         *
         * https://www.save-editor.com/tools/wse_hex.html
         * Block -> [HEX] -> BB3BE -> [LITTLE ENDIAN] -> BEB30B
         *   └── PUSHDATA มาจากขนาด 3 bytes จาก [BE, B3, 0B]
         *          └── PUSHDATA = [03]
         *
         * 697022 -> [HEX] -> AA2BE -> [LITTLE ENDIAN] -> BEA20A
         *   └── PUSHDATA มาจากขนาด 3 bytes จาก [BE, A2, 0A] = 03 หรือ 3 byte
         *          └── PUSHDATA = [03]
         *
         * 7669100 -> [HEX] -> 75056C -> [LITTLE ENDIAN] -> 6C0575
         *   └── PUSHDATA มาจากขนาด 3 bytes จาก [6C, 05, 75] = 03 หรือ 3 byte
         *          └── PUSHDATA = [03]
         *
         * 96385217 -> [HEX] -> 5BEB8C1 -> [LITTLE ENDIAN] -> C1B8BE05
         *   └── PUSHDATA มาจากขนาด 4 bytes จาก [C1, B8, BE, 05] = 04 หรือ 4 byte
         *          └── PUSHDATA = [04]
         * */

        // กำหนดค่าเริ่มต้นสำหรับ blockNumber ในรูปแบบ LITTLE ENDIAN 8 Bytes
        val LITTLE_ENDIAN: ByteBuffer =
            ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putInt(blockNumber)

        // ตรวจสอบและกำหนดค่าให้กับ nLockTime โดยตัด byte 0x00 ที่อยู่ด้านท้ายออก
        var nLockTime: ByteArray = LITTLE_ENDIAN.array()
        while (nLockTime.isNotEmpty() && nLockTime.last() == 0x00.toByte()) {
            nLockTime = nLockTime.dropLast(1).toByteArray()
        }

        /*
        * องค์ประกอบสคริปต์
        *
        *   [ < ขนาดหมายเลข Block, หมายเลข Block รูปแบบ LITTLE ENDIAN > ]
        * OP_CHECKLOCKTIMEVERIFY
        * OP_DROP
        *   [ < ขนาดของ Public key >, < Public key > ]
        * OP_CHECKSIG
        */
        val stack = listOf(
            OP_PUSHDATA(nLockTime).opCode.toByteArray(),
            nLockTime,
            OP_CHECKLOCKTIMEVERIFY.code.toByteArray(),
            OP_DROP.code.toByteArray(),
            OP_PUSHDATA(publicKey).opCode.toByteArray(),
            publicKey,
            OP_CHECKSIG.code.toByteArray()
        )

        return buildString {
            stack.forEach { element ->
                append(element.ByteArrayToHex())
            }
        }
    }


}

fun main() {
    val script = ScriptBuilder()
    val publicKey = "02a1d6c523ea4baa26127a55eee14adcee1b1419407e317654682c0759f431ecaf"
    val redeemScript = script.TimeLock(1423787, publicKey.HexToByteArray())
    println(redeemScript)
    // 03abb915b1752102a1d6c523ea4baa26127a55eee14adcee1b1419407e317654682c0759f431ecafac
    // 3JFVsdsib5mrvpZmid1jdTPa2KR1fXXgpz
}