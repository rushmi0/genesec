package win.notoshi.genesec

import org.junit.Test
import win.notoshi.genesec.securekey.EllipticCurve
import win.notoshi.genesec.securekey.Secp256K1

class EllipticCurveTest(
    private val ecc: EllipticCurve = EllipticCurve(Secp256K1)
) {

    @Test
    fun testPublicKey() {
       TODO("Not yet implemented")
    }

}
