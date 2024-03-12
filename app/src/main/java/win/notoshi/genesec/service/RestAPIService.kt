package win.notoshi.genesec.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import com.google.gson.GsonBuilder


interface RestAPIService {

    @GET("tx/{txid}/hex")
    suspend fun getTransactionHex(@Path("txid") txid: String): String

    @GET("address/{address}/txs/chain")
    suspend fun getAddressTransactions(@Path("address") address: String): List<TransactionResponse>

}

data class TransactionResponse(
    val txid: String,
    val version: Int,
    val locktime: Long,
    val vin: List<Any>, // แก้ไขตามโครงสร้างจริง
    val vout: List<Any>, // แก้ไขตามโครงสร้างจริง
    val size: Int,
    val weight: Int,
    val fee: Int,
    val status: TransactionStatusResponse
)

data class TransactionStatusResponse(
    val confirmed: Boolean,
    val block_height: Long,
    val block_hash: String,
    val block_time: Long
)


suspend fun main() {
    // Base URL ของ API
    val baseUrl = "https://mempool.space/api/"

    // สร้าง Gson instance และใช้ setLenient(true) เพื่อยอมรับ JSON ที่ไม่สมบูรณ์
    val gson = GsonBuilder().setLenient().create()

    // สร้าง Retrofit instance และใช้ GsonConverter
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    // สร้าง instance ของ ApiService
    val apiService = retrofit.create(RestAPIService::class.java)

    // TXID สำหรับเรียก API
    val txid = "15e10745f15593a899cef391191bdd3d7c12412cc4696b7bcb669d0feadc8521"

    // เรียกใช้งาน API ด้วย Retrofit
    try {
        val responseBody = apiService.getTransactionHex(txid)
        println("Transaction Hex Response: $responseBody")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }

    // ทดสอบเรียก API สำหรับข้อมูลที่สอง
    val address = "1wiz18xYmhRX6xStj2b9t1rwWX4GKUgpv"
    try {
        val addressTransactions = apiService.getAddressTransactions(address)
        println("Address Transactions Response: $addressTransactions")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }

    try {
        val addressTransactions = apiService.getAddressTransactions(address)
        // กรองข้อมูลเฉพาะ txid
        val txidList = addressTransactions.map { it.txid }[0]
        println("Address Transactions Response - TXIDs only: $txidList")
    } catch (e: Exception) {
        println("Error: ${e.message}")
    }
}
