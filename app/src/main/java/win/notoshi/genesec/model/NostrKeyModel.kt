package win.notoshi.genesec.model

import android.content.Context
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import win.notoshi.genesec.model.record.ShortKeyRecord
import win.notoshi.genesec.securekey.ECKeyFactory
import win.notoshi.genesec.securekey.ECKeyProvider
import win.notoshi.genesec.securekey.ECKeyProvider.toXPoint
import win.notoshi.genesec.securekey.Secp256K1
import win.notoshi.genesec.utils.ShiftTo.bech32Encode
import javax.inject.Inject
import kotlin.random.Random

class NostrKeyModel @Inject constructor(val context: Context) : ViewModel() {

    init {
        val ecKeyFactory = ECKeyFactory(Secp256K1)
        ECKeyProvider.initialize(ecKeyFactory)
    }

    private lateinit var priv: String
    private lateinit var pub: String

    private lateinit var nsec: String
    private lateinit var npub: String

    private val _NOSTR_KEY = MutableStateFlow(
        ShortKeyRecord("", "", "", "")
    )


    private val _SHOW_NOSTR_KEY = MutableStateFlow(
        ShortKeyRecord("", "", "", "")
    )


    val NOSTR_KEY: StateFlow<ShortKeyRecord> = _NOSTR_KEY

    val SHOW_NOSTR_KEY: StateFlow<ShortKeyRecord> = _SHOW_NOSTR_KEY

    private fun notifyNostrKeyChanged() {
        _NOSTR_KEY.value = ShortKeyRecord(
            nsec(),
            npub(),
            privateKey(),
            publicKey()
        )
    }

    private fun showNotifyNostrKeyChanged() {
        _SHOW_NOSTR_KEY.value = ShortKeyRecord(
            nsec().shortenString(),
            npub().shortenString(),
            privateKey().shortenString(),
            publicKey().shortenString()
        )
    }


    fun nostrKeyPair() = notifyNostrKeyChanged()

    fun rawKeyPair() = showNotifyNostrKeyChanged()


    private fun String.shortenString(): String {
        val prefixLength = 13
        val suffixLength = 13

        // ตรวจสอบว่าข้อมูลไม่ว่างเปล่าหรือไม่
        if (this.isEmpty()) {
            return this
        }

        // ดึงข้อมูลที่ต้องการเก็บไว้ด้านหน้า
        val prefix = this.substring(0, minOf(prefixLength, this.length))

        // ดึงข้อมูลที่ต้องการเก็บไว้ด้านหลัง
        val suffix = this.substring(maxOf(0, this.length - suffixLength))

        return "$prefix....$suffix"
    }

    private fun randomBytes(): String {
        return Random.nextBytes(32).joinToString("") { "%02x".format(it) }
    }

    fun nsec(): String {
        nsec = privateKey().wrapKey("nsec")
        return nsec
    }

    fun npub(): String {
        npub = publicKey().wrapKey("npub")
        return npub
    }

    fun privateKey(): String {
        priv = randomBytes()
        return priv
    }

    fun publicKey(): String {
        pub = priv.toXPoint()
        return pub
    }

    private fun String.wrapKey(hrp: String): String {
        return this.bech32Encode(hrp)
    }

}