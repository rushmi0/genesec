package win.notoshi.genesec.model

import android.content.Context
import androidx.lifecycle.ViewModel
import win.notoshi.genesec.securekey.ECKeyFactory
import win.notoshi.genesec.securekey.ECKeyProvider
import win.notoshi.genesec.securekey.Secp256K1
import javax.inject.Inject

class NostrnsecModel @Inject constructor(val context: Context) : ViewModel() {


    init {
        val ecKeyFactory = ECKeyFactory(Secp256K1)
        ECKeyProvider.initialize(ecKeyFactory)
    }





}