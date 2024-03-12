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
import win.notoshi.genesec.model.record.NostrKeyRecord
import win.notoshi.genesec.service.securekey.ECKeyFactory
import win.notoshi.genesec.service.securekey.ECKeyProvider
import win.notoshi.genesec.service.securekey.ECKeyProvider.toXPoint
import win.notoshi.genesec.service.securekey.Secp256K1
import win.notoshi.genesec.service.utils.ShiftTo.npub
import win.notoshi.genesec.service.utils.ShiftTo.nsec
import kotlin.random.Random

class NostrKeyModel @Inject constructor(val context: Context) : ViewModel() {

    init {
        val ecKeyFactory = ECKeyFactory(Secp256K1)
        ECKeyProvider.initialize(ecKeyFactory)
    }

    private lateinit var priv: String
    private lateinit var pub: String


    private val _NOSTR_KEY = MutableStateFlow(
        NostrKeyRecord(
            null,
            null,
        )
    )

    val NOSTR_KEY: StateFlow<NostrKeyRecord> = _NOSTR_KEY

    private fun notifyNostrKeyChanged() {
        _NOSTR_KEY.value = NostrKeyRecord(
            privateKey().nsec(),
            publicKey().npub(),
        )
    }

    fun nostrKeyPair() = notifyNostrKeyChanged()

    private fun randomBytes(): String {
        return Random.nextBytes(32).joinToString("") { "%02x".format(it) }
    }

    private fun privateKey(): String {
        priv = randomBytes()
        return priv
    }

    private fun publicKey(): String {
        pub = priv.toXPoint()
        return pub
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