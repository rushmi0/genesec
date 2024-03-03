package win.notoshi.genesec.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import win.notoshi.genesec.model.MnemonicModel
import win.notoshi.genesec.model.NostrnsecModel
import win.notoshi.genesec.test.CountModelTest

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(NostrnsecModel::class.java)) {
            return NostrnsecModel(context) as T
        } else if (modelClass.isAssignableFrom(MnemonicModel::class.java)) {
            return MnemonicModel(context) as T
        } else if (modelClass.isAssignableFrom(CountModelTest::class.java)) {
            return CountModelTest(context) as T
        }

        throw IllegalArgumentException("UnknownViewModel")
    }
}
