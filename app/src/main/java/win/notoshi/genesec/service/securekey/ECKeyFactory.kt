package win.notoshi.genesec.service.securekey

import android.os.Build
import androidx.annotation.RequiresApi
import io.micronaut.core.annotation.Introspected
import win.notoshi.genesec.service.utils.ShiftTo.ByteArrayToBigInteger
import win.notoshi.genesec.service.utils.ShiftTo.ByteArrayToHex
import win.notoshi.genesec.service.utils.ShiftTo.HexToByteArray
import java.math.BigInteger

/*
* ปรับแต่ง Public key
* */
@Introspected
open class ECKeyFactory(curve: CurveParamsProvider) : EllipticCurve(curve) {


    /*
    * ใช้เพื่อตรวจสอบว่าจุดที่รับเข้ามานั้นอยู่บนเส้นโค้งวงรีหรือไม่
    * โดยการรับค่า point เพื่อนำไปคำนวณตามสมการเส้นโค้งวงรี และตรวจสอบว่าสมการที่ได้มีค่าเท่ากันหรือไม่ และจะคืนค่าเป็น true หากสมการมีค่าเท่ากัน
    * */
    fun isPointOnCurve(point: PointField?): Boolean {
        val (x, y) = point
        // ! ถ้าค่า point ที่รับเข้ามาเป็น null ให้ส่งค่า Exception กลับไป
            ?: throw IllegalArgumentException("`isPointOnCurve` Method Point is null")

        // * ตรวจสอบว่าจุดนั้นเป็นไปตามสมการเส้นโค้งวงรี หรือไม่: y^2 = x^3 + Ax + B (mod P)
        val leftSide = (y * y) % P // leftSide เป็นค่า y^2 และรนำไป mod P
        val rightSide = (x.pow(3) + A * x + B) % P // rightSide เป็นค่า x^3 + Ax + B และรนำไป mod P

        return leftSide == rightSide
    }


    fun pubKeyPoint(k: BigInteger): String {
        try {
            val point: PointField = multiplyPoint(k)
            val xHex: String = point.x.toString(16)
            val yHex: String = point.y.toString(16)

            val xSize: Int = xHex.HexToByteArray().size
            val ySize: Int = yHex.HexToByteArray().size

            val max = maxOf(xSize, ySize)

            when {
                xSize != max -> {
                    val padding: String = "0".repeat(max - xSize)
                    return "04$padding$xHex$yHex"
                }
                ySize != max -> {
                    val padding: String = "0".repeat(max - ySize)
                    return "04$xHex$padding$yHex"
                }
            }
            return "04$xHex$yHex"
        } catch (e: IllegalArgumentException) {
            println("Invalid private key: ${e.message}")
            return null.toString()
        } catch (e: Exception) {
            println("Failed to generate the public key: ${e.message}")
            return null.toString()
        }
    }


    fun groupSelection(publicKey: String): String {


        val keyByteArray = publicKey.HexToByteArray().copyOfRange(1, publicKey.HexToByteArray().size)

        // วัดขนาด `keyByteArray` แลพหารด้วย 2 เพื่อแบ่งครึ่ง
        val middle = keyByteArray.size / 2

        // แบ่งครึ่งข้อมูล `keyByteArray` ออกเป็น 2 ส่วน
        val xOnly = keyByteArray.copyOfRange(0, middle).ByteArrayToHex()
        val yOnly = keyByteArray.copyOfRange(middle, keyByteArray.size).ByteArrayToHex()

        // ทำการแยกพิกัด x ออกมาจาก public key รูปแบบเต็ม
        val x = BigInteger(xOnly, 16)

        // ทำการแยกพิกัด y ออกมาจาก public key รูปแบบเต็ม
        val y = BigInteger(yOnly, 16)

        // ตรวจสอบว่า y เป็นเลขคู่หรือไม่ เพื่อเลือก group key ที่เหมาะสมเนื่องจากมี 2 กลุ่ม
        return if (y and BigInteger.ONE == BigInteger.ZERO) {
            "02" + x.toString(16).padStart(middle * 2, '0')
        } else {
            "03" + x.toString(16).padStart(middle * 2, '0')
        }
    }



    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun publicKeyGroup(xGroupOnly: String): PointField {

        val byteArray = xGroupOnly.HexToByteArray()
        val xCoord = byteArray.copyOfRange(1, byteArray.size).ByteArrayToBigInteger()
        val isYEven = byteArray[0] == 2.toByte()

        val xCubed = xCoord.modPow(BigInteger.valueOf(3), P)
        val Ax = xCoord.multiply(A).mod(P)
        val ySquared = xCubed.add(Ax).add(B).mod(P)

        val y = ySquared.modPow(P.add(BigInteger.ONE).divide(BigInteger.valueOf(4)), P)
        val isYSquareEven = y.mod(BigInteger.TWO) == BigInteger.ZERO
        val computedY = if (isYSquareEven != isYEven) P.subtract(y) else y

        return PointField(xCoord, computedY)
    }


    // ใช้สำหรับแปรง Public Key Hex ให้อยู่ในรูปแบบของ พิกัดบนเส้นโค้งวงรี (x, y)
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    fun pointRecovery(key: String): PointField {

        return when (key.HexToByteArray().size) {
            33 -> {
                publicKeyGroup(key)
            }
            else -> {
                // แจ้งข้อผิดพลาดเมื่อขนาดของ public key ไม่ถูกต้อง
                throw IllegalArgumentException("Invalid public key")
            }
        }

    }


    fun generatePoint(k: BigInteger): String {
        // คำนวณค่าพิกัดบนเส้นโค้งวงรีจาก private key
        val point = multiplyPoint(k)

        // ตรวจสอบว่าจุดที่ได้มานั้นอยู่บนเส้นโค้งวงรีหรือไม่
        if (!isPointOnCurve(point)) {
            throw IllegalArgumentException("Invalid private key")
        }

        // คืนค่าพิกัดบนเส้นโค้งวงรี
        return point.x.toString(16)
    }


}
