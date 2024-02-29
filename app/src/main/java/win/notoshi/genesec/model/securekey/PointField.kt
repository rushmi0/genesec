package win.notoshi.genesec.model.securekey

import java.math.BigInteger

// * จุดบนเส้นโค้งวงรี มีพิกัด x และ y
data class PointField(
    val x: BigInteger,
    val y: BigInteger
)
