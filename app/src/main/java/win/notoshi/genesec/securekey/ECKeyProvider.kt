package win.notoshi.genesec.securekey

import android.os.Build
import androidx.annotation.RequiresApi
import io.micronaut.core.annotation.Introspected
import java.math.BigInteger

@Introspected
object ECKeyProvider {

    private lateinit var keyProvider: ECKeyFactory

    fun initialize(ecKeyFactory: ECKeyFactory) {
        keyProvider = ecKeyFactory
    }

    fun BigInteger.toPublicKey(): String {
        checkInitialized()
        return keyProvider.pubKeyPoint(this)
    }

    fun String.compressed(): String {
        checkInitialized()
        return keyProvider.groupSelection(this)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun String.extractKey(): PointField? {
        checkInitialized()
        return keyProvider.recoverFullKey(this)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun String.toPointField(): PointField {
        checkInitialized()
        return keyProvider.pointRecovery(this)
    }

    fun PointField.verifyPoint(): Boolean {
        checkInitialized()
        return keyProvider.isPointOnCurve(this)
    }

    private fun checkInitialized() {
        if (!::keyProvider.isInitialized) {
            throw IllegalStateException("ECKeyProvider not initialized. Call initialize() first.")
        }
    }
}
