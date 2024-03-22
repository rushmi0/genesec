package win.notoshi.genesec

import androidx.compose.ui.text.toLowerCase
import fr.acinq.bitcoin.Base58
import fr.acinq.bitcoin.Base58Check
import fr.acinq.bitcoin.ByteVector
import fr.acinq.bitcoin.Crypto
import fr.acinq.bitcoin.OP_0
import fr.acinq.bitcoin.OP_CHECKSIG
import fr.acinq.bitcoin.OP_DUP
import fr.acinq.bitcoin.OP_EQUAL
import fr.acinq.bitcoin.OP_EQUALVERIFY
import fr.acinq.bitcoin.OP_HASH160
import fr.acinq.bitcoin.OP_PUSHDATA
import fr.acinq.bitcoin.OutPoint
import fr.acinq.bitcoin.PrivateKey
import fr.acinq.bitcoin.Protocol
import fr.acinq.bitcoin.Script
import fr.acinq.bitcoin.ScriptElt
import win.notoshi.genesec.service.utils.ShiftTo.toByteArray
import fr.acinq.bitcoin.ScriptFlags
import fr.acinq.bitcoin.ScriptWitness
import fr.acinq.bitcoin.SigHash.SIGHASH_ALL
import fr.acinq.bitcoin.SigVersion
import fr.acinq.bitcoin.Transaction
import fr.acinq.bitcoin.TxId
import fr.acinq.bitcoin.TxIn
import fr.acinq.bitcoin.TxOut
import fr.acinq.bitcoin.byteVector
import fr.acinq.bitcoin.toSatoshi
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import win.notoshi.genesec.service.utils.ShiftTo.ByteArrayToHex

class TransactionTest {


    private fun createUnsignedTransaction(
        previousTx: Transaction,
        multisigAddress: ByteArray
    ): Transaction {
        return Transaction(
            version = 1L,
            txIn = listOf(
                TxIn(
                    OutPoint(previousTx.hash, 0),
                    signatureScript = listOf(),
                    sequence = 0xFFFFFFFFL
                )
            ),
            txOut = listOf(
                TxOut(
                    amount = 900000L.toSatoshi(),
                    publicKeyScript = listOf(OP_HASH160, OP_PUSHDATA(multisigAddress), OP_EQUAL)
                )
            ),
            lockTime = 0L
        )
    }

    private fun signTransaction(
        tx: Transaction,
        previousTx: Transaction,
        privateKey: PrivateKey
    ): Transaction {
        val sig = Transaction.signInput(
            tx,
            0,
            previousTx.txOut[0].publicKeyScript,
            SIGHASH_ALL,
            privateKey
        )

        return tx.updateSigScript(
            0,
            listOf(OP_PUSHDATA(sig), OP_PUSHDATA(privateKey.publicKey().toUncompressedBin()))
        )
    }


//    @Test
//    fun `create and sign p2sh transactions`() {
//        val key1 = PrivateKey.fromHex("C0B91A94A26DC9BE07374C2280E43B1DE54BE568B2509EF3CE1ADE5C9CF9E8AA01")
//        val key2 = PrivateKey.fromHex("5C3D081615591ABCE914D231BA009D8AE0174759E4A9AE821D97E28F122E2F8C01")
//        val key3 = PrivateKey.fromHex("29322B8277C344606BA1830D223D5ED09B9E1385ED26BE4AD14075F054283D8C01")
//
//        val pubKeys = listOf(key1, key2, key3).map { it.publicKey() }
//        val redeemScript: ByteArray = Script.createMultiSigMofN(2, pubKeys)
//
//        val privateKey = PrivateKey.fromBase58(
//            "92TgRLMLLdwJjT1JrrmTTWEpZ8uG7zpHEgSVPTbwfAs27RpdeWM",
//            Base58.Prefix.SecretKeyTestnet
//        ).first
//
//        val previousTx = "41e573704b8fba07c261a31c89ca10c3cb202c7e4063f185c997a8a87cf21dea" // Replace with the actual serialized transaction
//
//        val multisigAddress = Crypto.hash160(redeemScript)
//
//        val tx = createUnsignedTransaction(previousTx, multisigAddress)
//
//        val signedTx = signTransaction(tx, previousTx, privateKey)
//
//        assert(Transaction.correctlySpends(signedTx, listOf(previousTx), ScriptFlags.STANDARD_SCRIPT_VERIFY_FLAGS))
//    }
//


    @Test
    fun `create and sign p2sh transactions `() {
        val key1 =
            PrivateKey.fromHex("C0B91A94A26DC9BE07374C2280E43B1DE54BE568B2509EF3CE1ADE5C9CF9E8AA01")
        val pub1 = key1.publicKey()
        val key2 =
            PrivateKey.fromHex("5C3D081615591ABCE914D231BA009D8AE0174759E4A9AE821D97E28F122E2F8C01")
        val pub2 = key2.publicKey()
        val key3 =
            PrivateKey.fromHex("29322B8277C344606BA1830D223D5ED09B9E1385ED26BE4AD14075F054283D8C01")
        val pub3 = key3.publicKey()

        // we want to spend the first output of this tx
        val previousTx = Transaction.read(
            "01000000014100d6a4d20ff14dfffd772aa3610881d66332ed160fc1094a338490513b0cf800000000fc0047304402201182201b586c6bfe6fd0346382900834149674d3cbb4081c304965440b1c0af20220023b62a997f4385e9279dc1078590556c6c6a85c3ec20fda407e95eb270e4de90147304402200c75f91f8bd741a8e71d11ff6a3e931838e32ceead34ccccfe3f73f01a81e45f02201795881473644b5f5ee6a8d8a90fe16e60eacace40e88900c375af2e0c51e26d014c69522103bd95bfc136869e2e5e3b0491e45c32634b0201a03903e210b01be248e04df8702103e04f714a4010ca5bb1423ef97012cb1008fb0dfd2f02acbcd3650771c46e4a8f2102913bd21425454688bdc2df2f0e518c5f3109b1c1be56e6e783a41c394c95dc0953aeffffffff0140420f00000000001976a914298e5c1e2d2cf22deffd2885394376c7712f9c6088ac00000000"
        )
        val privateKey = PrivateKey.fromBase58(
            "92TgRLMLLdwJjT1JrrmTTWEpZ8uG7zpHEgSVPTbwfAs27RpdeWM",
            Base58.Prefix.SecretKeyTestnet
        ).first

        val publicKey = privateKey.publicKey()

        // create and serialize a "2 out of 3" multisig script
        val redeemScript: ByteArray =
            Script.write(Script.createMultiSigMofN(2, listOf(pub1, pub2, pub3)))

        // แปลง ByteArray เป็น String และแสดงผล
        val redeemScriptString: String = redeemScript.ByteArrayToHex()
        println(redeemScriptString)

        // the multisig adress is just that hash of this script
        val multisigAddress: ByteArray = Crypto.hash160(redeemScript)

        val multisigAddressBase58: String = Base58.encode(multisigAddress)
        println("Multisig Address (Base58): $multisigAddressBase58")


        // we want to send money to our multisig adress by redeeming the first output
        // of 41e573704b8fba07c261a31c89ca10c3cb202c7e4063f185c997a8a87cf21dea
        // using our private key 92TgRLMLLdwJjT1JrrmTTWEpZ8uG7zpHEgSVPTbwfAs27RpdeWM

        // create a tx with empty input signature scripts
        val tx = Transaction(
            version = 1L,
            txIn = listOf(
                TxIn(
                    OutPoint(
                        previousTx.hash,
                        0
                    ),
                    signatureScript = listOf(

                    ),
                    sequence = 0xFFFFFFFFL
                )
            ),
            txOut = listOf(
                TxOut(
                    amount = 900000L.toSatoshi(), // 0.009 BTC in satoshi, meaning the fee will be 0.01-0.009 = 0.001
                    publicKeyScript = listOf(OP_HASH160, OP_PUSHDATA(multisigAddress), OP_EQUAL)
                )
            ),
            lockTime = 0L
        )

        // and sign it
        val sig = Transaction.signInput(
            tx,
            0,
            previousTx.txOut[0].publicKeyScript,
            SIGHASH_ALL,
            privateKey
        )

        val signedTx = tx.updateSigScript(
            0,
            listOf(OP_PUSHDATA(sig), OP_PUSHDATA(privateKey.publicKey().toUncompressedBin()))
        )

        Transaction.correctlySpends(
            signedTx,
            listOf(previousTx),
            ScriptFlags.STANDARD_SCRIPT_VERIFY_FLAGS
        )

        // --------------------------------------------------------------------------------------

        // how to spend our tx ? let's try to sent its output to our public key
        val spendingTx = Transaction(
            version = 1L,
            txIn = listOf(
                TxIn(OutPoint(signedTx.hash, 0), signatureScript = listOf(), sequence = 0xFFFFFFFFL)
            ),
            txOut = listOf(
                TxOut(
                    amount = 900000L.toSatoshi(),
                    publicKeyScript = listOf(
                        OP_DUP,
                        OP_HASH160,
                        OP_PUSHDATA(publicKey.hash160()),
                        OP_EQUALVERIFY,
                        OP_CHECKSIG
                    )
                )
            ),
            lockTime = 0L
        )

        // we need at least 2 signatures
        val sig1 = Transaction.signInput(spendingTx, 0, redeemScript, SIGHASH_ALL, key1)
        val sig2 = Transaction.signInput(spendingTx, 0, redeemScript, SIGHASH_ALL, key2)

        // update our tx with the correct sig script
        val sigScript = listOf(
            OP_0, OP_PUSHDATA(sig1), OP_PUSHDATA(sig2), OP_PUSHDATA(redeemScript)
        )

        val signedSpendingTx = spendingTx.updateSigScript(0, sigScript)
        Transaction.correctlySpends(
            signedSpendingTx,
            listOf(signedTx),
            ScriptFlags.STANDARD_SCRIPT_VERIFY_FLAGS
        )
    }

    // --------------------------------------------------------

    val pversion = Protocol.PROTOCOL_VERSION

    @Test
    fun `create p2wsh tx`() {
        val (priv1, _) = PrivateKey.fromBase58(
            "cV5oyXUgySSMcUvKNdKtuYg4t4NTaxkwYrrocgsJZuYac2ogEdZX",
            Base58.Prefix.SecretKeyTestnet
        )
        val pub1 = priv1.publicKey()
        val address1 = Base58Check.encode(Base58.Prefix.PubkeyAddressTestnet, pub1.hash160())

        assertEquals("mkNdbutRYE3me7wvbwvvJ8XQwbzi56sneZ", address1)

        val (priv2, _) = PrivateKey.fromBase58(
            "cV7LGVeY2VPuCyCSarqEqFCUNig2NzwiAEBTTA89vNRQ4Vqjfurs",
            Base58.Prefix.SecretKeyTestnet
        )
        val pub2 = priv2.publicKey()

        val (priv3, _) = PrivateKey.fromBase58(
            "cRp4uUnreGMZN8vB7nQFX6XWMHU5Lc73HMAhmcDEwHfbgRS66Cqp",
            Base58.Prefix.SecretKeyTestnet
        )
        val pub3 = priv3.publicKey()

        // this is a standard tx that sends 0.05 BTC to mkNdbutRYE3me7wvbwvvJ8XQwbzi56sneZ
        val tx1 = Transaction.read(
            "020000000001016ecc08b535a0c774234419dee508867ace1535a0d256d6b2aa19942441777336000000002322002073bb471aa121fbdd95942eabb5e665d66e71542e6e075c8392cd0df72a075b72fdffffff02803823030000000017a914d3c15be7951c9de644bdf9e22dcbcb77550c4ae487404b4c00000000001976a9143545b2a6659dbe5bdf841d1158135be184d81d3688ac0400473044022041cac92405e4e3215c2f9c27a67ff0792c8fb76e4182023fed081f541f4563e002203bd04d4d810ef8074aeb26a19e01e1ee1a40ad83e4d0ac2c614b8cb22825d2ae0147304402204c947b46ea480419c04098a56a5219bb1f491b07e12926fb6f304132a1f1e29e022078cc9f004c74d6c3c2b2dfcca6385d2fabe44d4eadb027a0d764e1ab9d7f09190147522102be608bf8904326b4d0ec9346aa348773fe51ee70338849acd2dd710b73bf611a2103627c19e40f67c5ee8b44df85ee911b7e978869fa5a3de1d972a461f47ea349e452ae90bb2300",
            pversion
        )

        // now let's create a simple tx that spends tx1 and sends 0.049 BTC to a P2WSH output
        val tx2 = run {
            // our script is a 2-of-2 multisig script
            val redeemScript: List<ScriptElt> = Script.createMultiSigMofN(2, listOf(pub2, pub3))
            println(redeemScript)
            println(redeemScript[0])
            val script = Script.pay2wsh(redeemScript)
            println(script)
            val tmp = Transaction(
                version = 1,
                txIn = listOf(TxIn(OutPoint(tx1.hash, 1), sequence = 0xffffffffL)),
                txOut = listOf(TxOut(4900000L.toSatoshi(), script)),
                lockTime = 0
            )
            val sig = Transaction.signInput(
                tmp,
                0,
                tx1.txOut[1].publicKeyScript,
                SIGHASH_ALL,
                tx1.txOut[1].amount,
                SigVersion.SIGVERSION_BASE,
                priv1
            )
            tmp.updateSigScript(0, listOf(OP_PUSHDATA(sig), OP_PUSHDATA(pub1)))
        }
        println(tx2)

        Transaction.correctlySpends(tx2, listOf(tx1), ScriptFlags.STANDARD_SCRIPT_VERIFY_FLAGS)
        assertEquals(
            tx2.txid,
            TxId("2f8360a06a31ca642d717b1857aa86b3306fc554fa9c437d88b4bc61b7f2b3e9")
        )
        // this tx was published on testnet as 2f8360a06a31ca642d717b1857aa86b3306fc554fa9c437d88b4bc61b7f2b3e9

        // and now we create a segwit tx that spends the P2WSH output
        val tx3 = run {
            val tmp = Transaction(
                version = 1,
                txIn = listOf(TxIn(OutPoint(tx2.hash, 0), sequence = 0xffffffffL)),
                txOut = listOf(TxOut(4800000L.toSatoshi(), Script.pay2wpkh(pub1))),
                lockTime = 0
            )
            val pubKeyScript = Script.write(Script.createMultiSigMofN(2, listOf(pub2, pub3)))
            val sig2 = Transaction.signInput(
                tmp,
                0,
                pubKeyScript,
                SIGHASH_ALL,
                tx2.txOut[0].amount,
                SigVersion.SIGVERSION_WITNESS_V0,
                priv2
            )
            val sig3 = Transaction.signInput(
                tmp,
                0,
                pubKeyScript,
                SIGHASH_ALL,
                tx2.txOut[0].amount,
                SigVersion.SIGVERSION_WITNESS_V0,
                priv3
            )
            val witness = ScriptWitness(
                listOf(
                    ByteVector.empty,
                    sig2.byteVector(),
                    sig3.byteVector(),
                    pubKeyScript.byteVector()
                )
            )
            tmp.updateWitness(0, witness)
        }

        Transaction.correctlySpends(tx3, listOf(tx2), ScriptFlags.STANDARD_SCRIPT_VERIFY_FLAGS)
        assertEquals(
            tx3.txid,
            TxId("4817f79def9d9f559ddaa636f0c196e79f31bc959feead77b4151733114c652a")
        )
    }

    // ---------------------------------------------------------------------

    @Test
    fun `create p2wpkh tx`() {
        val (priv1, _) = PrivateKey.fromBase58(
            "cV7LGVeY2VPuCyCSarqEqFCUNig2NzwiAEBTTA89vNRQ4Vqjfurs",
            Base58.Prefix.SecretKeyTestnet
        )
        val pub1 = priv1.publicKey()
        val address1 = Base58Check.encode(Base58.Prefix.PubkeyAddressTestnet, pub1.hash160())

        assertTrue(address1 == "mp4eLFx7CpifAJxnvCZ3FmqKsh9dQmi5dA")

        // this is a standard tx that sends 0.04 BTC to mp4eLFx7CpifAJxnvCZ3FmqKsh9dQmi5dA
        val tx1 = Transaction.read(
            "02000000000101516508384a3e006340f1ea700eb3635330beed5d94c7b460b6b495eb1593d55c0100000023220020a5fdf5b5f2c592362b78a50997821964b39dd90476c6e1f3e97e79acb134ca3bfdffffff0200093d00000000001976a9145dbf52b8d7af4fb5f9b75b808f0a8284493531b388aca005071d0000000017a914d77e5f7ca4d9f05dc4f25dc0aa1391f0e901bdfc87040047304402207bfb18327be173512f38bd4120b8f02545321ecc6105a852cbc25b1de687ba570220705a1225d8a8e0fbd4b35f3bc38a2840706f8524e8dc6f0151746aeff14033ce014730440220486925fb0495442e4ccb1b711692af7057d4db24f8775b5dfa3f8c74992081f102203beae7d96423e0c66b7b5f8919a5f3ad89a42dc4303f37201e4e596909478357014752210245119449d07c16992c148e3b33f1395ee05c936fc510d9fae83417f8e1901f922103eb03f67b56c88bccff90b76182c08556eac9ebc5a0efee8669bef69ae6d4ea5752ae75bb2300",
            pversion
        )

        // now let's create a simple tx that spends tx1 and send 0.039 BTC to P2WPK output
        val tx2 = run {
            val tmp = Transaction(
                version = 1,
                txIn = listOf(
                    TxIn(
                        OutPoint(tx1.hash, 0),
                        sequence = 0xffffffffL
                    )
                ),
                txOut = listOf(
                    TxOut(
                        3900000L.toSatoshi(),
                        Script.pay2wpkh(pub1)
                    )
                ),
                lockTime = 0
            )
            val sig = Transaction.signInput(
                tmp,
                0,
                tx1.txOut[0].publicKeyScript,
                SIGHASH_ALL,
                tx1.txOut[0].amount,
                SigVersion.SIGVERSION_BASE,
                priv1
            )
            tmp.updateSigScript(0, listOf(OP_PUSHDATA(sig), OP_PUSHDATA(pub1)))
        }

        Transaction.correctlySpends(tx2, listOf(tx1), ScriptFlags.STANDARD_SCRIPT_VERIFY_FLAGS)
        assertEquals(tx2.txid, TxId("f25b3fecc9652466926237d96e4bc7ee2c984051fe48e61417aba218af5570c3"))
        // this tx was published on testnet as f25b3fecc9652466926237d96e4bc7ee2c984051fe48e61417aba218af5570c3

        // and now we create a testnet tx that spends the P2WPK output
        val tx3 = run {
            val tmp = Transaction(
                version = 1,
                txIn = listOf(
                    TxIn(
                        OutPoint(
                            tx2.hash, 0
                        ),
                        sequence = 0xffffffffL
                    )
                ),
                txOut = listOf(
                    TxOut(
                        3800000L.toSatoshi(),
                        Script.pay2wpkh(pub1)
                    )
                ), // we reuse the same output script but if could be anything else
                lockTime = 0
            )
            // mind this: the pubkey script used for signing is not the prevout pubscript (which is just a push
            // of the pubkey hash), but the actual script that is evaluated by the script engine, in this case a PAY2PKH script
            val pubKeyScript = Script.pay2pkh(pub1)
            val c: ScriptElt = pubKeyScript[2]
            println(pubKeyScript)

            val sig = Transaction.signInput(
                tmp,
                0,
                pubKeyScript,
                SIGHASH_ALL,
                tx2.txOut[0].amount,
                SigVersion.SIGVERSION_WITNESS_V0,
                priv1
            )
            val witness = ScriptWitness(listOf(sig.byteVector(), pub1.value))
            tmp.updateWitness(0, witness)
        }

        Transaction.correctlySpends(tx3, listOf(tx2), ScriptFlags.STANDARD_SCRIPT_VERIFY_FLAGS)
        assertEquals(tx3.txid, TxId("739e7cba97af259d2c089690adea00aa78b1c8d7995aa9377be58fe5332378aa"))
        // this tx was published on testnet as 739e7cba97af259d2c089690adea00aa78b1c8d7995aa9377be58fe5332378aa
    }

}