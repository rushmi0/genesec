package win.notoshi.genesec.test


import kotlinx.coroutines.flow.MutableStateFlow
import android.content.Context
import androidx.lifecycle.ViewModel

class CountModelTest constructor(val context: Context): ViewModel() {

    val count = MutableStateFlow<Int>(0)

    fun add(){
        count.value = count.value + 1
    }

}