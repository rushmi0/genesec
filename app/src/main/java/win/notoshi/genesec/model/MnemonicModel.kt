package win.notoshi.genesec.model

import android.content.Context
import androidx.lifecycle.ViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import win.notoshi.genesec.model.record.BIP39Record
import win.notoshi.genesec.securekey.bip39.SecretWord

class MnemonicModel @Inject constructor(val context: Context) : ViewModel() {

    private val _SEED = MutableStateFlow(
        BIP39Record(
            null
        )
    )

    val SEED: StateFlow<BIP39Record> = _SEED

    private fun notifyMnemonicPhraseChanged() {
        _SEED.value = BIP39Record(
            newMnemonic()
        )
    }

    fun mnemonicPhrase() = notifyMnemonicPhraseChanged()

    private fun newMnemonic(): String {
        return SecretWord(128).generateMnemonic()
    }

}