package win.notoshi.genesec.securekey

import java.math.BigInteger

interface CurveParamsProvider {
    val A: BigInteger
    val B: BigInteger
    val N: BigInteger
    val P: BigInteger
    val G: PointField
}
