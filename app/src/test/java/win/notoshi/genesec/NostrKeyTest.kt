//package win.notoshi.genesec
//
//import org.junit.Test
//import win.notoshi.genesec.securekey.ECKeyFactory
//import win.notoshi.genesec.securekey.ECKeyProvider
//import win.notoshi.genesec.securekey.NostrKey.privateKey
//import win.notoshi.genesec.securekey.NostrKey.publicKey
//import win.notoshi.genesec.securekey.NostrKey.wrapKey
//import win.notoshi.genesec.securekey.Secp256K1
//
//class NostrKeyTest {
//
//    init {
//        val ecKeyFactory = ECKeyFactory(Secp256K1)
//        ECKeyProvider.initialize(ecKeyFactory)
//    }
//
//    val priv = privateKey()
//    val pub = priv.publicKey()
//
//    @Test
//    fun `Generate nostr key`() {
//        val privateKey = priv.wrapKey("nsec")
//        println(privateKey)
//
//        val publicKey = pub.wrapKey("npub")
//        println(publicKey)
//    }
//
//
//
//
//}