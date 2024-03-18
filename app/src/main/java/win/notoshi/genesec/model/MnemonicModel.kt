package win.notoshi.genesec.model

import android.content.Context
import androidx.lifecycle.ViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import win.notoshi.genesec.model.record.BIP39Record
import win.notoshi.genesec.service.securekey.bip39.SecretWord
import kotlin.properties.Delegates

class MnemonicModel @Inject constructor(val context: Context) : ViewModel() {

    private val _SEED = MutableStateFlow(BIP39Record(null, 0))
    val SEED: StateFlow<BIP39Record> = _SEED

    private var strength by Delegates.notNull<Int>()
    private var length by Delegates.notNull<Int>()


    private fun notifyMnemonicPhraseChanged() {
        _SEED.value = BIP39Record(newMnemonic(), length)
    }

    fun mnemonicPhrase() = notifyMnemonicPhraseChanged()

    private fun newMnemonic(): String {
        val data = SecretWord(strength).generateMnemonic()
        length = data.split(" ").size
        return data
    }

    fun getValue(strength: Int, wordLength: Int) {
        this.strength = strength
        this.length = wordLength
    }

}
