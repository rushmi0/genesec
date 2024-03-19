package win.notoshi.genesec

import org.junit.Test
import win.notoshi.genesec.service.securekey.bip39.SecretWord

class BIP39Test {


    @Test
    fun `generate mnemonic`() {

        for (i in 1..3000) {

            println("\n> #$i")
            val generator = SecretWord(16)
            val mnemonic = generator.generateMnemonic()
            //println("Mnemonic Word [${mnemonic.split(" ").size}]")
            //println("> $mnemonic")
        }

    }

}