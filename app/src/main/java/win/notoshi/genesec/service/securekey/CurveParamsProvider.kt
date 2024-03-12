package win.notoshi.genesec.service.securekey

import java.math.BigInteger

interface CurveParamsProvider {
    val A: BigInteger
    val B: BigInteger
    val N: BigInteger
    val P: BigInteger
    val G: PointField
}
