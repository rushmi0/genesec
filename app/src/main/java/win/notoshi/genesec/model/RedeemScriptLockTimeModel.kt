package win.notoshi.genesec.model

import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.journeyapps.barcodescanner.BarcodeEncoder
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import win.notoshi.genesec.model.record.RedeemScriptRecord
import win.notoshi.genesec.service.nimiscript.ScriptBuilder
import win.notoshi.genesec.service.transaction.Address.getP2SH
import win.notoshi.genesec.service.utils.ShiftTo.HexToByteArray

class RedeemScriptLockTimeModel @Inject constructor(val context: Context) : ViewModel() {


    private lateinit var pubKey: String
    private lateinit var blockNumber: String
    private lateinit var redeemScript: String
    private lateinit var address: String

    private val _CONTRACT = MutableStateFlow(
        RedeemScriptRecord(
            "",
            ""
        )
    )

    private fun notifyRedeemScriptChanged() {
        _CONTRACT.value = RedeemScriptRecord(
            buildContract(),
            buildLockingScript()
        )
    }

    val CONTRACT: StateFlow<RedeemScriptRecord> = _CONTRACT

    fun processBuildContract() = notifyRedeemScriptChanged()

    private fun buildLockingScript(): String {
        this.address = redeemScript.getP2SH("main")
        return this.address
    }

    private fun buildContract(): String {
        val script = ScriptBuilder()
        this.redeemScript = script.TimeLock(
            this.blockNumber.toInt(),
            this.pubKey.HexToByteArray()
        )
        return this.redeemScript
    }

    fun getValue(pubKey: String, blocknum: String) {
        this.pubKey = pubKey
        this.blockNumber = blocknum
    }


    fun generateQRCode(content: String): Bitmap? {
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix: BitMatrix =
                multiFormatWriter.encode(content, BarcodeFormat.QR_CODE, 500, 500)
            val barcodeEncoder = BarcodeEncoder()
            return barcodeEncoder.createBitmap(bitMatrix)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

}
