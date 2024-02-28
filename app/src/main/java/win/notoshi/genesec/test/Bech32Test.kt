package win.notoshi.genesec.test

import junit.framework.TestCase.assertEquals

import org.junit.Test
import win.notoshi.genesec.utils.ShiftTo.HexToByteArray
import win.notoshi.genesec.utils.toBech32Data

class Bech32Test {

    private val priv: String = "3f92871eacfc4d34e37441d3f5c662dd5ae45e7744b201a6367da19dc51a3fb4"

    @Test
    fun testBech32() {

        val pubKeyBytes = priv.HexToByteArray()

        val hrp = "nsec"

        val bech32Data = pubKeyBytes.toBech32Data(hrp)

        val nsec = bech32Data.address

        assertEquals(
            "nsec187fgw84vl3xnfcm5g8flt3nzm4dwghnhgjeqrf3k0ksem3g6876q9ryerx",
            nsec
        )
    }

}