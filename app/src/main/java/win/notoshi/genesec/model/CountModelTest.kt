package win.notoshi.genesec.model


import kotlinx.coroutines.flow.MutableStateFlow
import android.content.Context
import androidx.lifecycle.ViewModel
import jakarta.inject.Inject

class CountModelTest @Inject constructor(val context: Context): ViewModel() {

    val count = MutableStateFlow<Int>(0)

    fun add() {
        count.value = count.value + 1
    }

}