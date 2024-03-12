package win.notoshi.genesec

import junit.framework.TestCase
import org.junit.Test
import win.notoshi.genesec.service.securekey.ECKeyFactory
import win.notoshi.genesec.service.securekey.ECKeyProvider
import win.notoshi.genesec.service.securekey.ECKeyProvider.compressed
import win.notoshi.genesec.service.securekey.ECKeyProvider.toPointField
import win.notoshi.genesec.service.securekey.ECKeyProvider.toPublicKey
import win.notoshi.genesec.service.securekey.Secp256K1
import java.math.BigInteger

class EllipticCurveTest {

    private val priv = BigInteger("3f92871eacfc4d34e37441d3f5c662dd5ae45e7744b201a6367da19dc51a3fb4", 16)

    init {
        val ecKeyFactory = ECKeyFactory(Secp256K1)
        ECKeyProvider.initialize(ecKeyFactory)
    }

    // https://learnmeabitcoin.com/technical/public-key
    @Test
    fun testPublicKey() {
        val pubKey = priv.toPublicKey()

        TestCase.assertEquals(
            pubKey,
            "04d0a951954dc6a0167f5857dea80da2d3ee5d88d51a6a701ce75437c6923b37b792fbb2353798bc7446b7eecdd4fbb76870fae75d024c3976ddbe5b61e434dfe6"
        )

        TestCase.assertEquals(
            pubKey.compressed(),
            "02d0a951954dc6a0167f5857dea80da2d3ee5d88d51a6a701ce75437c6923b37b7"
        )
    }

    @Test
    fun testConvertCompressedKey2Point() {
        val pubKeyCompressed = "02d0a951954dc6a0167f5857dea80da2d3ee5d88d51a6a701ce75437c6923b37b7"
        val pointField = pubKeyCompressed.toPointField()

        // Assert
        TestCase.assertEquals(
            pointField.x.toString(16),
            "d0a951954dc6a0167f5857dea80da2d3ee5d88d51a6a701ce75437c6923b37b7"
        )

        TestCase.assertEquals(
            pointField.y.toString(16),
            "92fbb2353798bc7446b7eecdd4fbb76870fae75d024c3976ddbe5b61e434dfe6"
        )
    }

    @Test
    fun testConvertCompressedKey2UncompressedKey() {

    }
}
