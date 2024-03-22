package win.notoshi.genesec.service.securekey

import win.notoshi.genesec.service.securekey.WIF.extractWIF
import win.notoshi.genesec.service.securekey.WIF.toWIF
import win.notoshi.genesec.service.transaction.Address
import win.notoshi.genesec.service.utils.ShiftTo.ByteArrayToHex
import win.notoshi.genesec.service.utils.ShiftTo.HexToByteArray
import win.notoshi.genesec.service.utils.ShiftTo.decodeBase58
import win.notoshi.genesec.service.utils.ShiftTo.encodeBase58
import win.notoshi.genesec.service.transaction.Address.verify.getChecksum
import win.notoshi.genesec.service.transaction.Network
import win.notoshi.genesec.service.utils.ShiftTo.SHA256


object WIF {

    private val CHAIN = Network

    private fun privateKeyToWIF_U(network: String, privateKeyHex: String): String {

        /*
         *
         *  ฟังก์ชั่น privateKeyToWIF_U
         *     ├──  รับค่า Hash sha256  ::  <- 9454a5235cf34e382d7e927eb5709dc4f4ed08eed177cb3f2d4ea359071962d7
         *          └──  ผลลัพธ์ WIF Key  ::  -> 5JwcVJQfQbzAfXnMYQXzLjzczGi22v8BvyyHkUBTmYwN7Z3Qswa
         *
         */

        // * แปลง Private Key จากเลขฐานสิบหกเป็นอาร์เรย์ไบต์
        val privateKeyBytes = privateKeyHex.HexToByteArray()

        // * เมื่อตรวจสอบค่า network และสร้าง WIF Key ตาม network นั้น
        val prefix: ByteArray = when (network) {
            "main" -> {

                // * ดึงคำนำหน้า WIF สำหรับเครือข่าย "main" -> 0x80
                CHAIN.MAIN["wif"].toString().HexToByteArray()
            }
            "test" -> {

                // * ดึงคำนำหน้า WIF สำหรับเครือข่าย "test" -> 0xEF
                CHAIN.TEST["wif"].toString().HexToByteArray()
            }
            else -> {

                // ! แจ้งเตือนข้อผิดพลาดในกรณีที่ network ไม่ถูกต้อง
                return throw IllegalArgumentException("Invalid network")
            }
        }

        // * รวมคำนำหน้าและไบต์ของคีย์ส่วนตัวเข้าด้วยกัน
        val extendedKey = prefix + privateKeyBytes

        // * คำนวณเช็คซัม
        val checksum = extendedKey.getChecksum()

        // * รวมคีย์ที่ถูกขยายและเช็คซัมเข้าด้วยกัน
        val wifBytes = extendedKey + checksum

        // * แปลงอาร์เรย์ของไบต์ที่รวมกันเป็นสตริงฐานสิบหกและเข้ารหัสใน Base58
        return wifBytes.ByteArrayToHex().encodeBase58()
    }


    private fun privateKeyToWIF_C(network: String, privateKeyHex: String): String {

        /*
         *
         *  ฟังก์ชั่น privateKeyToWIF_C
         *     ├──  รับค่า Hash sha256  ::  <- 9454a5235cf34e382d7e927eb5709dc4f4ed08eed177cb3f2d4ea359071962d7
         *          └──  ผลลัพธ์ WIF Key  ::  -> L2C3duqSXBRKf4sBfcsn68mKqnL3ZTUjFGTSvryB9dxxBche5CNY
         *
         */

        // * แปลง Private Key จากเลขฐานสิบหกเป็นอาร์เรย์ไบต์
        val privateKeyBytes = privateKeyHex.HexToByteArray()

        // * เมื่อตรวจสอบค่า network และสร้าง WIF Key ตาม network นั้น
        val prefix: ByteArray = when (network) {
            "main" -> {

                // * ดึงคำนำหน้า WIF สำหรับเครือข่าย "main" -> 0x80
                CHAIN.MAIN["wif"].toString().HexToByteArray()
            }
            "test" -> {

                // * ดึงคำนำหน้า WIF สำหรับเครือข่าย "test" -> 0xEF
                CHAIN.TEST["wif"].toString().HexToByteArray()
            }
            else -> {

                // ! แจ้งเตือนข้อผิดพลาดในกรณีที่ network ไม่ถูกต้อง
                return throw IllegalArgumentException("Invalid network")
            }
        }

        // * สร้างอาร์เรย์ของไบต์ที่แทนค่าเพิ่มเติมสำหรับการบีบอัด
        val compressed = byteArrayOf(0x01.toByte())

        // * รวมคำนำหน้า, ไบต์ของคีย์ส่วนตัว, และค่าเพิ่มเติมสำหรับการบีบอัดเข้าด้วยกัน
        val extendedKey = prefix + privateKeyBytes + compressed

        // * คำนวณเช็คซัม
        val checksum = extendedKey.getChecksum()

        // * รวมคีย์ที่ถูกขยายและเช็คซัมเข้าด้วยกัน
        val wifBytes = extendedKey + checksum

        // * แปลงอาร์เรย์ของไบต์ที่รวมกันเป็นสตริงฐานสิบหกและเข้ารหัสใน Base58
        return wifBytes.ByteArrayToHex().encodeBase58()
    }

    // ──────────────────────────────────────────────────────────────────────────────────────── \\

    // * แปลงเลขฐานสิบหก (Private Key) ให้อยู่ในรูป WIF
    fun String.toWIF(network: String, option: Boolean): String {
        return if (option == true) {
            privateKeyToWIF_C(network, this)
        } else {
            privateKeyToWIF_U(network, this)
        }
    }

    // * แกะ WIF Key และเก็บเอาเฉพาะค่าคีย์ (Private Key) ที่ค้องการ
    fun String.extractWIF(): String {
        val data = this.decodeBase58().HexToByteArray()
        return data.copyOfRange(1, 33).ByteArrayToHex()
    }

    fun ByteArray.getChecksum(): ByteArray {
        return checksum(this)
    }

    private fun checksum(data: ByteArray): ByteArray {
        val hash = data.SHA256().SHA256()
        return hash.sliceArray(0 .. 4)
    }

}


fun main() {

    val private_key = "c51a52e294165cfde3342e8c12c5f3370d29d12401c03803fe34de78c80b1804"
    println("Private Key: ${private_key.length} length\n\t└── $private_key\n")

    val WIF = private_key.toWIF("main", true)
    println("WIF Key: ${WIF.length} length\n\t└── $WIF\n")

    val wifC = private_key.toWIF("main", false)
    println("WIF Key [Compress]: ${wifC.length} length\n\t└── $wifC\n")

    val data = WIF.extractWIF()
    println("Original Key U: ${data.length} length\n\t└── ${data}")

}