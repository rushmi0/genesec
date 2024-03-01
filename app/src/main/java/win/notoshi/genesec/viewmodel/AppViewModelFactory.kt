package win.notoshi.genesec.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import android.content.Context
import win.notoshi.genesec.test.CountModelTest

class AppViewModelFactory(private val context: Context) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {

        if (modelClass.isAssignableFrom(CountModelTest::class.java)) {
            return CountModelTest(context) as T
        }

        throw IllegalArgumentException("UnknownViewModel")
    }
}
