package win.notoshi.genesec.securekey.bip39

import win.notoshi.genesec.utils.ShiftTo.BinaryToByteArray
import win.notoshi.genesec.utils.ShiftTo.ByteArrayToBinary
import win.notoshi.genesec.utils.ShiftTo.ByteArrayToHex
import win.notoshi.genesec.utils.ShiftTo.HexToBinary
import win.notoshi.genesec.utils.ShiftTo.SHA256
import java.math.BigInteger
import java.security.SecureRandom
import javax.inject.Inject

class SecretWord @Inject constructor(private val Strength: Int) {


    private val WORD = BIP39.WORDLIST

    private fun generateEntropy(): ByteArray {
        val entropy = BigInteger(Strength, SecureRandom()).toString(2)
        //val entropy = "10101010111110011011000111010000011111001000100010100100111100100110110001011001010100101011011011000111101010010101101111101101"
        val byteArray: ByteArray = entropy.BinaryToByteArray()

        println("Generated Binary: ${byteArray.ByteArrayToBinary()}")
        println("Entropy Bytes [${byteArray.size}]: ${byteArray.contentToString()}")
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
        val entropy = entropyBytes.ByteArrayToBinary() + checksum(entropyBytes)

        val pieces = (entropy.indices step 11).map { i -> entropy.substring(i, i + 11) }
        //val mnemonic = pieces.map { piece -> wordlist[piece.toInt(2)] }.joinToString(" ")
        return pieces.joinToString(" ") { piece -> WORD[piece.toInt(2)] }
    }

}


fun main() {
    val generator = SecretWord(128)
    val mnemonic = generator.generateMnemonic()
    println("\nMnemonic Word [${mnemonic.split(" ").size}]")
    println("> $mnemonic")
}