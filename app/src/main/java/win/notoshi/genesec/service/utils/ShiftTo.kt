package win.notoshi.genesec.service.utils

import win.notoshi.genesec.model.utils.toBech32Data
import win.notoshi.genesec.service.utils.ShiftTo.ByteArrayToHex
import java.math.BigInteger
import java.security.MessageDigest

object ShiftTo {

    private val hexDigits: String = "0123456789abcdef"


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
        //return joinToString("") { byte -> byte.toUByte().toString(16).padStart(2, '0') }
        return this.joinToString("") { byte -> "%02x".format(byte) }
    }


    fun ByteArray.toBinaryString(): String {
        val binaryString = StringBuilder()
        for (byte in this) {
            for (i in 7 downTo 0) {
                binaryString.append((byte.toInt() shr i) and 1)
            }
        }
        return binaryString.toString()
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

    fun String.RIPEMD160(): String {
        return Ripemd160.getHash(this.HexToByteArray()).ByteArrayToHex()
    }

    fun ByteArray.RIPEMD160(): String {
        return Ripemd160.getHash(this).ByteArrayToHex()
    }

    fun String.bech32Encode(hrp: String) : String {
        val keyBytes = this.HexToByteArray()
        val bech32Data = keyBytes.toBech32Data(hrp)
        return bech32Data.address
    }


    fun String.nsec() : String {
        return this.wrapKey("nsec")
    }

    fun String.npub() : String {
        return this.wrapKey("npub")
    }

    private fun String.wrapKey(hrp: String): String {
        return this.bech32Encode(hrp)
    }



    fun String.shortenString(): String {
        val prefixLength = 8
        val suffixLength = 8

        return when {
            this.isNotEmpty() -> {
                val prefix = this.substring(0, minOf(prefixLength, length))
                val suffix = this.substring(maxOf(0, length - suffixLength))
                "$prefix....$suffix"
            }
            else -> this
        }
    }


    fun String.decodeBase58(): String {
        return Base58.decode(this).ByteArrayToHex()
    }

    fun String.encodeBase58(): String {
        return Base58.encode(this.HexToByteArray())
    }


    fun Int.toByteArray(): ByteArray {
        return byteArrayOf(this.toByte())
    }

}