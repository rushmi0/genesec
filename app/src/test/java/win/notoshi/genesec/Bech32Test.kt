package win.notoshi.genesec

import junit.framework.TestCase
import org.junit.Test
import win.notoshi.genesec.model.utils.Bech32
import win.notoshi.genesec.model.utils.ShiftTo.HexToByteArray
import win.notoshi.genesec.model.utils.toBech32Data

class Bech32Test {

    private val priv = "3f92871eacfc4d34e37441d3f5c662dd5ae45e7744b201a6367da19dc51a3fb4"

    @Test
    fun testBech32() {

        val pubKeyBytes = priv.HexToByteArray()

        val hrp = "nsec"
        val bech32Data = pubKeyBytes.toBech32Data(hrp)
        val nsec = bech32Data.address

        val privOrigi = Bech32.decode(nsec).hexData

        TestCase.assertEquals(
            "nsec187fgw84vl3xnfcm5g8flt3nzm4dwghnhgjeqrf3k0ksem3g6876q9ryerx",
            nsec
        )

        TestCase.assertEquals(
            priv,
            privOrigi
        )


    }

}