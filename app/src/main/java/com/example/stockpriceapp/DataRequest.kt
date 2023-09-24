package com.example.stockpriceapp
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponse
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@RequiresApi(Build.VERSION_CODES.O)
class RequestIndexData(idToken: String) {
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    val header = mapOf("Authorization" to idToken)

    fun TradingCalender(): String{
        val today = LocalDateTime.now()
        val to = today.minusDays(84)
        val from = today.minusDays(89)
        val dtFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
        val toDay = to.format(dtFormat)
        val fromDay = from.format(dtFormat)

        var referenceDay = ""
        runBlocking {
            val (_, response, result) =
                "https://api.jquants.com/v1/markets/trading_calendar?from=$fromDay&to=$toDay"
                    .httpGet()
                    .header(header)
                    .awaitStringResponseResult()
            result.fold(

                {
                    val holidayDivisionAdapter = moshi.adapter(HolidayDivision::class.java)
                    val res = String(response.body().toByteArray())
                    val data = holidayDivisionAdapter.fromJson(res)
                    val tradingCalendar = data?.trading_calendar
                    if(tradingCalendar != null){
                        for(i in 5 downTo 0){
                            if(tradingCalendar[i].HolidayDivision != "0"){
                                referenceDay = tradingCalendar[i].Date
                                break
                            }
                        }
                    }
                },
                {

                }
            )
        }
        return referenceDay
    }

    fun RequestData(): List<String> {
        val indexClose = mutableListOf("")

        val TradingCalender = TradingCalender()
        val referenceDate = TradingCalender.replace("-", "")

        runBlocking {
            val (_, response, result) = "https://api.jquants.com/v1/prices/daily_quotes?date=$referenceDate"
                .httpGet()
                .header(header)
                .awaitStringResponseResult()
            result.fold(
                {   indexClose.remove(indexClose[0])
                    val dailyQuotesAdapter = moshi.adapter(DailyQuotes::class.java)
                    val res = String(response.body().toByteArray())
                    val data = dailyQuotesAdapter.fromJson(res)
                    val dataQuotes = data?.daily_quotes

                    if (dataQuotes != null) {
                        for (i in dataQuotes.indices) {
                            indexClose.add(dataQuotes[i].Close.toString())
                        }
                    }
                },
                {

                }
            )
        }
        return indexClose
    }

    fun RequestCompanyName(): List<String> {
        val companyName = mutableListOf("")

        val TradingCalender = TradingCalender()
        val referenceDate = TradingCalender.replace("-", "")

        runBlocking {
            val (_, response, result) = "https://api.jquants.com/v1/listed/info?date=$referenceDate"
                .httpGet()
                .header(header)
                .awaitStringResponseResult()
            result.fold(
                {   companyName.remove(companyName[0])
                    val infoAdapter = moshi.adapter(Info::class.java)
                    val res = String(response.body().toByteArray())
                    val data = infoAdapter.fromJson(res)
                    val info = data?.info

                    if (info != null) {
                        for (i in info.indices) {
                            companyName.add(info[i].CompanyName)
                        }
                    }
                },
                {

                }
            )
        }
        return companyName
    }
}