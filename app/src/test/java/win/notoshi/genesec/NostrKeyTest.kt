package win.notoshi.genesec

import org.junit.Test
import win.notoshi.genesec.model.utils.ShiftTo.bech32Encode

class NostrKeyTest {



    @Test
    fun `test Nostr private key (nsec)`() {
        val pubKeyA = "7f7ff03d123792d6ac594bfa67bf6d0c0ab55b6b1fdb6249303fe861f1ccba9a"

        val nsec = pubKeyA.bech32Encode("nsec")
        println(nsec)
    }

    @Test
    fun `test Nostr Public key (npub)`() {




    }


}