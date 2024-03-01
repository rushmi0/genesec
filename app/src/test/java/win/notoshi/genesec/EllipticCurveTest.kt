package win.notoshi.genesec

import org.junit.Assert
import org.junit.Test
import win.notoshi.genesec.securekey.ECKeyFactory
import win.notoshi.genesec.securekey.ECKeyProvider
import win.notoshi.genesec.securekey.ECKeyProvider.compressed
import win.notoshi.genesec.securekey.ECKeyProvider.toPublicKey
import win.notoshi.genesec.securekey.Secp256K1
import java.math.BigInteger

class EllipticCurveTest {

    private val priv = BigInteger("3f92871eacfc4d34e37441d3f5c662dd5ae45e7744b201a6367da19dc51a3fb4", 16)

    @Test
    fun testPublicKey() {
        val ecKeyFactory = ECKeyFactory(Secp256K1)
        ECKeyProvider.setKeyProvider(ecKeyFactory)

        val pubKey = priv.toPublicKey()

        Assert.assertEquals(
            pubKey,
            "04d0a951954dc6a0167f5857dea80da2d3ee5d88d51a6a701ce75437c6923b37b792fbb2353798bc7446b7eecdd4fbb76870fae75d024c3976ddbe5b61e434dfe6"
        )

        Assert.assertEquals(
            pubKey.compressed(),
            "02d0a951954dc6a0167f5857dea80da2d3ee5d88d51a6a701ce75437c6923b37b7"
        )
    }

}
