package win.notoshi.genesec.service.transaction

import win.notoshi.genesec.model.utils.Bech32
import win.notoshi.genesec.service.utils.ShiftTo.ByteArrayToHex
import win.notoshi.genesec.service.utils.ShiftTo.HexToByteArray
import win.notoshi.genesec.service.utils.ShiftTo.RIPEMD160
import win.notoshi.genesec.service.utils.ShiftTo.SHA256
import win.notoshi.genesec.service.utils.ShiftTo.decodeBase58
import win.notoshi.genesec.service.utils.ShiftTo.encodeBase58
import win.notoshi.genesec.service.transaction.Address.verify.getChecksum

object Address {


    private fun P2SH(network: String, data: String): String {

        // * เมื่อตรวจสอบค่า network และสร้าง Locking Script ตาม network นั้น
        return when (network) {

            // * ในกรณีที่ค่า network เป็น "main" หรือ "test"
            "main",
            "test" -> {

                // * คำนวณค่าแฮช SHA256 ของข้อมูล
                val dataHash256: String = data.SHA256().ByteArrayToHex()

                // * คำนวณค่าแฮช RIPEMD160 ของแฮชข้อมูล
                val scriptHash: String = dataHash256.RIPEMD160()

                val pointer = when (network) {

                    // * ดึงค่าสคริปต์สำหรับเครือข่าย "main" -> 0x05
                    "main" -> Network.MAIN["p2sh"]

                    // * ดึงค่าสคริปต์สำหรับเครือข่าย "test" -> 0xC4
                    "test" -> Network.TEST["p2sh"]

                    // ! แจ้งเตือนข้อผิดพลาดในกรณีที่ network ไม่ถูกต้อง
                    else -> throw IllegalArgumentException("Invalid network")
                }.toString()

                // * รวมสคริปต์และแฮชสคริปต์เข้าด้วยกัน
                val components = pointer + scriptHash

                // * คำนวณเช็คซัม
                val checksum: ByteArray = components.HexToByteArray().getChecksum()

                // * ประกอบสคริปต์ pointer และ dataHash256 เข้าด้วยกัน
                val combine = components + checksum.ByteArrayToHex()

                // * เข้ารหัสและคืนค่าเป็น Base58
                combine.encodeBase58()
            }

            // ! แจ้งเตือนข้อผิดพลาดในกรณีที่ network ไม่ถูกต้อง
            else -> throw IllegalArgumentException("Invalid network")
        }
    }


    // ──────────────────────────────────────────────────────────────────────────────────────── \\


    // Pay-to-Script-Hash
    fun String.getP2SH(network: String): String {
        return P2SH(network, this)
    }

    // ──────────────────────────────────────────────────────────────────────────────────────── \\

    /*
    * ในส่วนนี้ ใช้สำหรับการตรวจสอบความถูกต้องของ Locking Script ต่าง ๆ
    */
    object verify {

        fun findeChecksum(data: ByteArray): ByteArray {
            val hash = data.SHA256().SHA256()
            return hash.sliceArray(0 .. 4)
        }

        private fun P2PKH(address: String): Boolean {
            val decodedAddress = address.decodeBase58().HexToByteArray()
            if (decodedAddress.size != 25) {
                return false
            }

            val checksum = findeChecksum(decodedAddress.sliceArray(0 .. 21))
            if (!decodedAddress.sliceArray(21 until 25).contentEquals(checksum)) {
                return false
            }

            val networkPrefix = decodedAddress[0]
            return networkPrefix == 0x00.toByte()
        }


        // ──────────────────────────────────────────────────────────────────────────────────────── \\

        fun ByteArray.getChecksum(): ByteArray {
            return findeChecksum(this)
        }

        fun String.isP2PKH(): Boolean {
            return P2PKH(this)
        }


    }
}

