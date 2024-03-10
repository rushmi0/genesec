package win.notoshi.genesec.model

import android.content.Context
import androidx.lifecycle.ViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import win.notoshi.genesec.model.record.BIP32Record
import win.notoshi.genesec.securekey.bip39.SecretWord

class MnemonicModel @Inject constructor(val context: Context) : ViewModel() {

    private val _SEED = MutableStateFlow(
        BIP32Record(
            ""
        )
    )

    val SEED: StateFlow<BIP32Record> = _SEED

    fun mnemonicPhrase() : String {
        return SecretWord(128).generateMnemonic()
    }

}