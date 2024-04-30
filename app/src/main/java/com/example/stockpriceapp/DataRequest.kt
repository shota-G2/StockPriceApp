package com.example.stockpriceapp
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.result.Result
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class DataRequest {
    private val myApp = MyApp.getInstance()
    private val companyData = myApp.companyData
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun getRefreshToken(
        context: Context,
        navController: NavController
    ) {
        //moshi,adapter設定
        val refreshTokenRequestAdapter = moshi.adapter(Parameter::class.java)
        val parameter = Parameter(
            mailaddress = "marimocag2@gmail.com",
            password = "princeG21021"
        )

        val getRefreshTokenUrl = context.getString(R.string.getRefreshTokenUrl)

        Fuel.post(getRefreshTokenUrl)
            .body(refreshTokenRequestAdapter.toJson(parameter))
            .response { _, response, result ->
                when (result) {
                    is Result.Success -> {
                        val refreshTokenResultAdapter = moshi.adapter(resultRefreshToken::class.java)
                        val data = String(response.body().toByteArray())

                        //変数refreshTokenにAPIから返ってきたrefreshTokenを格納する
                        myApp.refreshToken = refreshTokenResultAdapter.fromJson(data)?.refreshToken.toString()

                        //idToken取得
                        val dataRequest = DataRequest()
                        dataRequest.getIdToken(context, navController)
                    }
                    is Result.Failure -> {
                        Toast.makeText(context, "通信エラーが発生しました", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun getIdToken(
        context: Context,
        navController: NavController
    ) {
        val getIdTokenUrl = context.getString(R.string.getIdTokenUrl) + myApp.refreshToken
        val dataRequest = DataRequest()

        Fuel.post(getIdTokenUrl)
            .response { _, response, result ->
                when (result) {
                    is Result.Success -> {
                        val idTokenResultAdapter = moshi.adapter(resultIdToken::class.java)
                        val res = String(response.body().toByteArray())
                        myApp.idToken = idTokenResultAdapter.fromJson(res)?.idToken.toString()
                        val header = mapOf("Authorization" to myApp.idToken)

                        //営業日判定
                        dataRequest.tradingCalender(header, context, navController)
                    }
                    is Result.Failure -> {
                        Toast.makeText(context, "通信エラーが発生しました", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    private fun tradingCalender(
        header: Map<String, String>,
        context: Context,
        navController: NavController
    ) {
        //休日に対応するため5日間分のカレンダーを取得
        val today = LocalDateTime.now()
        val dtFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
        val toDay = today.minusDays(84).format(dtFormat)
        val fromDay = today.minusDays(89).format((dtFormat))

        val dataRequest = DataRequest()

        Fuel.get("https://api.jquants.com/v1/markets/trading_calendar?from=$fromDay&to=$toDay")
            .header(header)
            .response { _, response, result ->
                when(result) {
                    is Result.Success -> {
                        //取得データを格納
                        val holidayDivisionAdapter = moshi.adapter(HolidayDivision::class.java)
                        val res = String(response.body().toByteArray())
                        val data = holidayDivisionAdapter.fromJson(res)
                        val tradingCalendar = data?.trading_calendar

                        //営業日の登録
                        val referenceDate = mutableListOf<String>()
                        if (tradingCalendar != null) {
                            for (i in 5 downTo 0) {
                                if (tradingCalendar[i].HolidayDivision == "1") {
                                    referenceDate.add(tradingCalendar[i].Date)
                                }
                                //2営業日リスト追加できたらループからはずれる
                                if (referenceDate.size == 2) {
                                    break
                                }
                            }
                            myApp.referenceDate = referenceDate[0]
                            myApp.previousBusinessDay = referenceDate[1]
                        }

                        // 全企業名取得
                        dataRequest.requestCompanyName(header, context, navController)
                    }
                    is Result.Failure -> {
                        Toast.makeText(context, "通信エラーが発生しました", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun requestCompanyName(
        header: Map<String, String>,
        context: Context,
        navController: NavController
    ) {
        val TradingCalender = myApp.referenceDate
        val referenceDate = TradingCalender.replace("-", "")

        Fuel.get("https://api.jquants.com/v1/listed/info?date=$referenceDate")
            .header(header)
            .response { _, response, result ->
                when(result) {
                    is Result.Success -> {
                        val infoAdapter = moshi.adapter(Info::class.java)
                        val res = String(response.body().toByteArray())
                        val data = infoAdapter.fromJson(res)
                        val info = data?.info

                        companyData.clear()
                        if (info != null) {
                            for (data in info) {
                                companyData.add(CompanyData(data.CompanyName, data.Code))
                            }
                        }

                        val dataRequest = DataRequest()

                        //対象日の終値取得（無料版の仕様により本日から84日前）
                        dataRequest.requestCompanyData(
                            myApp.referenceDate,
                            companyData,
                            header,
                            context
                        )

                        //対象日前日の終値取得
                        dataRequest.requestCompanyData(
                            myApp.previousBusinessDay,
                            companyData,
                            header,
                            context,
                            navController
                        )
                    }
                    is Result.Failure -> {
                        Toast.makeText(context, "通信エラーが発生しました", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun requestCompanyData(
        day: String,
        companyData: MutableList<CompanyData>,
        header: Map<String, String>,
        context: Context,
        navController: NavController? = null
    ) {
        Fuel.get("https://api.jquants.com/v1/prices/daily_quotes?date=$day")
            .header(header)
            .response { _, response, result ->
                when(result) {
                    is Result.Success -> {
                        val dailyQuotesAdapter = moshi.adapter(DailyQuotes::class.java)
                        val res = String(response.body().toByteArray())
                        val data = dailyQuotesAdapter.fromJson(res)
                        val dataQuotes = data?.daily_quotes

                        if (dataQuotes != null) {
                            for (data in companyData) {
                                for (responseData in dataQuotes) {
                                    if (data.stockCode == responseData.Code ) {
                                        if (day == myApp.referenceDate) {
                                            data.onTheDayIndexClose = responseData.Close
                                        } else {
                                            data.theDayBeforeIndexClose = responseData.Close
                                        }
                                    }
                                }
                            }
                        }
                        navController?.navigate("watchListScreen")
                    }
                    is Result.Failure -> {
                        Toast.makeText(context, "通信エラーが発生しました", Toast.LENGTH_LONG).show()
                    }
                }
        }
    }
}