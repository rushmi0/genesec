package win.notoshi.genesec.model

import android.content.Context
import androidx.lifecycle.ViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import win.notoshi.genesec.model.record.NostrKeyRecord
import win.notoshi.genesec.securekey.ECKeyFactory
import win.notoshi.genesec.securekey.ECKeyProvider
import win.notoshi.genesec.securekey.ECKeyProvider.toXPoint
import win.notoshi.genesec.securekey.Secp256K1
import win.notoshi.genesec.utils.ShiftTo.bech32Encode
import win.notoshi.genesec.utils.ShiftTo.npub
import win.notoshi.genesec.utils.ShiftTo.nsec
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
        NostrKeyRecord(
            "",
            "",
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

    fun privateKey(): String {
        priv = randomBytes()
        return priv
    }

    fun publicKey(): String {
        pub = priv.toXPoint()
        return pub
    }


}