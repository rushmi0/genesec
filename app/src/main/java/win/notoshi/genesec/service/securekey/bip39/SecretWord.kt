package win.notoshi.genesec.service.securekey.bip39

import android.util.Log
import win.notoshi.genesec.service.utils.ShiftTo.BinaryToByteArray
import win.notoshi.genesec.service.utils.ShiftTo.ByteArrayToBinary
import win.notoshi.genesec.service.utils.ShiftTo.ByteArrayToHex
import win.notoshi.genesec.service.utils.ShiftTo.HexToBinary
import win.notoshi.genesec.service.utils.ShiftTo.SHA256
import win.notoshi.genesec.service.utils.ShiftTo.toBinaryString
import java.math.BigInteger
import java.security.SecureRandom
import javax.inject.Inject
import kotlin.random.Random

class SecretWord @Inject constructor(private val Strength: Int) {


    private val WORD = BIP39.WORDLIST

    private fun generateEntropy(): ByteArray {
        val entropy = Random.nextBytes(Strength).toBinaryString()
        //val entropy = "11101111011001000001010101110100011100100111101111101011100110100000100110111101111101010100010001101011011110010100010001000110000110111011010011110101010000011011101111101010011001001000001011000101110011101001101000100111010101101111110111110"
        //println("Entropy ${entropy.length}: $entropy")
        //val entropy = "10101010111110011011000111010000011111001000100010100100111100100110110001011001010100101011011011000111101010010101101111101101"
        val byteArray: ByteArray = entropy.BinaryToByteArray()

        //println("Generated Binary ${byteArray.ByteArrayToBinary().length}: ${byteArray.ByteArrayToBinary()}")
        //println("Entropy Bytes [${byteArray.size}]: ${byteArray.contentToString()}")

        Log.d("Entropy ${entropy.length} bits", entropy)
        Log.d("Entropy ${byteArray.size} Bytes", "${byteArray.contentToString()}")
        return byteArray
    }


    // * https://www.mathsisfun.com/binary-decimal-hexadecimal-converter.html
    private fun checksum(data: ByteArray): String {
        val size = data.size * 8
        val entropyHash = data.SHA256().ByteArrayToHex()
        return entropyHash.HexToBinary().substring(0, size / 32)
    }

    fun generateMnemonic(): String {
        val entropyBytes = generateEntropy()
        val checksum = checksum(entropyBytes)
        val entropy = entropyBytes.ByteArrayToBinary() + checksum

        val pieces = (entropy.indices step 11).map { i -> entropy.substring(i, i + 11) }
        Log.d("Binary ${pieces.size} Pieces", "$pieces")

        //val mnemonic = pieces.map { piece -> wordlist[piece.toInt(2)] }.joinToString(" ")
        return pieces.joinToString(" ") { piece -> WORD[piece.toInt(2)] }
    }

}
