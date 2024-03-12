package win.notoshi.genesec

import fr.acinq.bitcoin.ByteVector
import fr.acinq.bitcoin.DeterministicWallet
import fr.acinq.bitcoin.OutPoint
import fr.acinq.bitcoin.Transaction
import fr.acinq.bitcoin.TxHash
import fr.acinq.bitcoin.TxId
import fr.acinq.bitcoin.TxIn
import fr.acinq.bitcoin.TxOut
import fr.acinq.bitcoin.psbt.Psbt
import fr.acinq.bitcoin.toSatoshi


fun main() {

//    val masterPrivKey =
//        DeterministicWallet.ExtendedPrivateKey.decode("tprv8ZgxMBicQKsPd9TeAdPADNnSyH9SSUUbTVeFszDE23Ki6TBB5nCefAdHkK8Fm3qMQR6sHwA56zqRmKmxnHk37JkiFzvncDqoKmPWubu7hDF").second
//    println(masterPrivKey.publicKey)

    val inputTx1 = Transaction(
        version = 2,
        txIn = listOf(
            TxIn(
                OutPoint(
                    TxHash("75ddabb27b8845f5247975c8a5ba7c6f336c4570708ebe230caf6db5217ae858"),
                    1
                ),
                ByteVector("00208c2353173743b595dfb4a07b72ba8e42e3797da74e87fe7d9d7497e3b2028903"),
                0
            )
        ),
        txOut = listOf(
            TxOut(500.toSatoshi(), ByteVector("0014d85c2b71d0060b09c9886aeb815e50991dda124d")),
            TxOut(750.toSatoshi(), ByteVector("0014d85c2b71d0060b09c9886aeb815e50991dda124d"))
        ),
        lockTime = 0
    )

    println(inputTx1)

    val unsignedTx = Transaction(
        version = 2,
        txIn = listOf(
            TxIn(
                OutPoint(
                    TxId("75ddabb27b8845f5247975c8a5ba7c6f336c4570708ebe230caf6db5217ae858"),
                    0
                ),
                signatureScript = listOf(),
                sequence = TxIn.SEQUENCE_FINAL
            )
        ),
        txOut = listOf(
            TxOut(
                12_000.toSatoshi(),
                ByteVector("0014d85c2b71d0060b09c9886aeb815e50991dda124d")
            )
        ),
        lockTime = 0
    )
    val psbt = Psbt(unsignedTx)

    println("-------------------------------------------")
    println("Unsigned Tx \n$unsignedTx")
    println("-------------------------------------------")
    println("PSBT")
    println("Tx: ${psbt.global.tx}")
    println("Version: ${psbt.global.version}")
    println("PublicKey: ${psbt.global.extendedPublicKeys}")
    println(" ${psbt.global.unknown}")

//    val updated = psbt.updateNonWitnessInput(
//        unsignedTx,
//
//    )


}