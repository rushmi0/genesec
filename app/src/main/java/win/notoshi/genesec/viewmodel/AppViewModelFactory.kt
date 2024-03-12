package win.notoshi.genesec.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import win.notoshi.genesec.model.ContractModel
import win.notoshi.genesec.model.MnemonicModel
import win.notoshi.genesec.model.NostrKeyModel
import win.notoshi.genesec.model.CountModelTest

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(NostrKeyModel::class.java) -> NostrKeyModel(context) as T
            modelClass.isAssignableFrom(MnemonicModel::class.java) -> MnemonicModel(context) as T
            modelClass.isAssignableFrom(ContractModel::class.java) -> ContractModel(context) as T
            modelClass.isAssignableFrom(CountModelTest::class.java) -> CountModelTest(context) as T
            else -> throw IllegalArgumentException("UnknownViewModel")
        }
    }
}
