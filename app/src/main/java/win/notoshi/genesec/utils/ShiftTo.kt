package win.notoshi.genesec.model.utils


import win.notoshi.genesec.model.utils.ShiftTo.bech32Encode
import java.math.BigInteger
import java.security.MessageDigest
import kotlin.random.Random

object ShiftTo {

    private val hexDigits: String = "0123456789abcdef"

    fun randomValue(): String {
        val byteArray = Random.nextBytes(128)
        val hexString = byteArray.joinToString("") { "%02x".format(it) }
        return hexString.substring(0, 64)
    }

    fun String.BinaryToByteArray(): ByteArray {
        return this.chunked(8).map { it.toInt(2).toByte() }.toByteArray()
    }

    fun ByteArray.ByteArrayToBinary(): String {
        return joinToString("") { byte -> byte.toUByte().toString(2).padStart(8, '0') }
    }

    fun ByteArray.ByteArrayToBigInteger(): BigInteger {
        return BigInteger(1, this)
    }

    fun ByteArray.ByteArrayToHex(): String {
        return joinToString("") { byte -> byte.toUByte().toString(16).padStart(2, '0') }
    }

    fun String.HexToByteArray(): ByteArray = ByteArray(this.length / 2) { this.substring(it * 2, it * 2 + 2).toInt(16).toByte() }


    fun String.HexToBinary(): String {
        val stringBuilder = StringBuilder(length * 4)

        for (hexChar in this) {
            val decimalValue = hexDigits.indexOf(hexChar)
            val binaryChunk = decimalValue.toString(2).padStart(4, '0')
            stringBuilder.append(binaryChunk)
        }

        return stringBuilder.toString()
    }

    fun ByteArray.SHA256(): ByteArray {
        return MessageDigest.getInstance("SHA-256").digest(this)
    }

    fun String.SHA256(): ByteArray {
        return toByteArray().SHA256()
    }

    fun String.bech32Encode(hrp: String): String {
        val pubKeyBytes = this.HexToByteArray()
        val bech32Data = pubKeyBytes.toBech32Data(hrp)
        return bech32Data.address
    }

}


fun main() {

    val pubKeyA = "7f7ff03d123792d6ac594bfa67bf6d0c0ab55b6b1fdb6249303fe861f1ccba9a"

    val nsec = pubKeyA.bech32Encode("nsec")
    println(nsec)

}