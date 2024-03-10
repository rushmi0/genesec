package win.notoshi.genesec

import org.junit.Test
import qrcode.QRCode
import qrcode.color.Colors

class QRcodeTest {

    @Test
    fun `create QR code`() {
        val helloWorld = QRCode.ofSquares()
            .withColor(Colors.DEEP_SKY_BLUE) // Default is Colors.BLACK
            .withSize(10) // Default is 25
            .build("Hello world!")

        val pngBytes = helloWorld.render()

        println(helloWorld)
        println(pngBytes)
    }

}