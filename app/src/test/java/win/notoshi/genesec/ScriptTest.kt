package win.notoshi.genesec


import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

import fr.acinq.bitcoin.Bitcoin
import fr.acinq.bitcoin.Block
import fr.acinq.bitcoin.PrivateKey
import fr.acinq.bitcoin.Script
import fr.acinq.bitcoin.ScriptElt
import fr.acinq.secp256k1.Hex


class ScriptTest {

    private val priv = PrivateKey.fromHex("0101010101010101010101010101010101010101010101010101010101010101")
    private val pub = priv.publicKey()


    @Test
    fun `WIF key`() {
        val privateKey = PrivateKey.fromHex("58badd9b455145b487ad24c7f259cc437cc4ffd0784716845249321fb5961cfc")

        //  Mainnet
        val wifMainnet = privateKey.toBase58(0x80.toByte())

        // Testnet
        val wifTestnet = privateKey.toBase58(0xEF.toByte())

        println(wifMainnet) // KzCBzQzuMFSfPX98wKAstJZrsM6dSW88YofEmBtJTSz85qvJEnEd
        println(wifTestnet) // cQZBTKzknK8vYxcQKiz1Fd4vVaQ36xDpcqohscLoxZe8Lb18d6U4
    }

    @Test
    fun `decode script hex`() {
        val redeemScript = "03beb30bb17521027de11310f7c996a2d1021276c11759ebb6f26d229dfd0bbc93b7f72fd36e3b8cac"
        val bytes = Hex.decode(redeemScript)
        val result: List<ScriptElt> = Script.parse(bytes)
        println(result)
        val script = Script.pay2sh(result)
        println(script)

        val addr = Bitcoin.addressFromPublicKeyScript(Block.LivenetGenesisBlock.hash, script).right
        println(addr)
    }

    @Test
    fun p2pkh() {
        val script: List<ScriptElt> = Script.pay2pkh(pub)
        println(script)

        val scriptHex = Hex.encode(Script.write(script))
        println(scriptHex)

        assertEquals("76a91479b000887626b294a914501a4cd226b58b23598388ac", Hex.encode(Script.write(script)))
        assertTrue(Script.isPay2pkh(script))
        assertNull(Script.getWitnessVersion(script))
        assertFalse(Script.isPay2sh(script) || Script.isPay2wpkh(script) || Script.isPay2wsh(script))
        assertEquals("1C6Rc3w25VHud3dLDamutaqfKWqhrLRTaD", Bitcoin.addressFromPublicKeyScript(Block.LivenetGenesisBlock.hash, script).right)
    }

    @Test
    fun p2sh() {
        val script = Script.pay2sh(Script.pay2pkh(pub))
        assertEquals("a914832e012d4cd5f23df82efd34e473345a2f8aa4fb87", Hex.encode(Script.write(script)))
        assertTrue(Script.isPay2sh(script))
        assertNull(Script.getWitnessVersion(script))
        assertFalse(Script.isPay2pkh(script) || Script.isPay2wpkh(script) || Script.isPay2wsh(script))
        assertEquals("3DedZ8SErqfunkjqnv8Pta1MKgEuHi22W5", Bitcoin.addressFromPublicKeyScript(Block.LivenetGenesisBlock.hash, script).right)
    }

    @Test
    fun p2wpkh() {
        val script = Script.pay2wpkh(pub)
        assertEquals("001479b000887626b294a914501a4cd226b58b235983", Hex.encode(Script.write(script)))
        assertTrue(Script.isPay2wpkh(script))
        assertEquals(0, Script.getWitnessVersion(script))
        assertFalse(Script.isPay2sh(script) || Script.isPay2pkh(script) || Script.isPay2wsh(script))
        assertEquals("bc1q0xcqpzrky6eff2g52qdye53xkk9jxkvrh6yhyw", Bitcoin.addressFromPublicKeyScript(Block.LivenetGenesisBlock.hash, script).right)
    }

    @Test
    fun p2wsh() {
        val script = Script.pay2wsh(Script.pay2pkh(pub))
        assertEquals("00206f1b349d7fed5240ad719948529e8b06abf038438f9b523820489375af513a3f", Hex.encode(Script.write(script)))
        assertTrue(Script.isPay2wsh(script))
        assertEquals(0, Script.getWitnessVersion(script))
        assertFalse(Script.isPay2sh(script) || Script.isPay2wpkh(script) || Script.isPay2pkh(script))
        assertEquals("bc1qdudnf8tla4fyptt3n9y9985tq64lqwzr37d4ywpqfzfhtt638glsqaednx", Bitcoin.addressFromPublicKeyScript(Block.LivenetGenesisBlock.hash, script).right)
    }

}