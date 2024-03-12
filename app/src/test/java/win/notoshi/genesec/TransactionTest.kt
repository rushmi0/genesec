package win.notoshi.genesec

import androidx.compose.ui.text.toLowerCase
import fr.acinq.bitcoin.Base58
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
import fr.acinq.bitcoin.Script
import win.notoshi.genesec.utils.ShiftTo.toByteArray
import fr.acinq.bitcoin.ScriptFlags
import fr.acinq.bitcoin.SigHash.SIGHASH_ALL
import fr.acinq.bitcoin.Transaction
import fr.acinq.bitcoin.TxIn
import fr.acinq.bitcoin.TxOut
import fr.acinq.bitcoin.toSatoshi
import org.junit.Test
import win.notoshi.genesec.utils.ShiftTo.ByteArrayToHex

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


}