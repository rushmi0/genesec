package win.notoshi.genesec.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import win.notoshi.genesec.model.LockTimeContractModel
import win.notoshi.genesec.model.MnemonicModel
import win.notoshi.genesec.model.NostrKeyModel
import win.notoshi.genesec.model.CountModelTest
import win.notoshi.genesec.model.KeyTypeModel
import win.notoshi.genesec.model.RedeemScriptLockTimeModel
import win.notoshi.genesec.model.WIFKeyModel

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MnemonicModel::class.java) -> MnemonicModel(context) as T

            modelClass.isAssignableFrom(NostrKeyModel::class.java) -> NostrKeyModel(context) as T
            modelClass.isAssignableFrom(WIFKeyModel::class.java) -> WIFKeyModel(context) as T
            modelClass.isAssignableFrom(KeyTypeModel::class.java) -> KeyTypeModel(context) as T

            modelClass.isAssignableFrom(LockTimeContractModel::class.java) -> LockTimeContractModel(context) as T
            modelClass.isAssignableFrom(RedeemScriptLockTimeModel::class.java) -> RedeemScriptLockTimeModel(context) as T

            modelClass.isAssignableFrom(CountModelTest::class.java) -> CountModelTest(context) as T
            else -> throw IllegalArgumentException("UnknownViewModel")
        }
    }

}
