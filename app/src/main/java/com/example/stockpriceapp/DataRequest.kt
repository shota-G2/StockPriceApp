package com.example.stockpriceapp
import android.content.Context
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.navigation.NavController
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.coroutines.awaitStringResponseResult
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class apiRequest {
    val myApp = MyApp.getInstance()
    val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    fun getRefreshToken(context: Context, watchList: MutableList<String>, navController: NavController) {
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
                        val refreshTokenResultAdapter =
                            moshi.adapter(resultRefreshToken::class.java)
                        val data = String(response.body().toByteArray())

                        //変数refreshTokenにAPIから返ってきたrefreshTokenを格納する
                        myApp.refreshToken =
                            refreshTokenResultAdapter.fromJson(data)?.refreshToken.toString()

                        //idToken取得
                        val apiRequest = apiRequest()
                        apiRequest.getIdToken(context, watchList, navController)
                    }
                    is Result.Failure -> {
                        Toast.makeText(context, "通信エラーが発生しました", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    fun getIdToken(context: Context, watchList: MutableList<String>, navController: NavController) {
        val getIdTokenUrl = context.getString(R.string.getIdTokenUrl) + myApp.refreshToken
        Fuel.post(getIdTokenUrl)
            .response { _, response, result ->
                when (result) {
                    is Result.Success -> {
                        val idTokenResultAdapter = moshi.adapter(resultIdToken::class.java)
                        val res = String(response.body().toByteArray())
                        myApp.idToken = idTokenResultAdapter.fromJson(res)?.idToken.toString()

                        //営業日判定
                        val apiRequest = apiRequest()
                        apiRequest.TradingCalender(context)

                        val activeCompanyName = myApp.activeCompanyName
                        val theDayBeforeActiveCompanyName = myApp.theDayBeforeActiveCompanyName
                        val onTheDayIndexClose = myApp.onTheDayIndexClose
                        val theDayBeforeIndexClose = myApp.theDayBeforeIndexClose
                        val theDayBeforeActiveStockCode = myApp.theDayBeforeActiveStockCode

                        apiRequest.RequestCompanyName(context)
                        apiRequest.RequestData(
                            myApp.referenceDate,
                            myApp.onTheDayIndexClose,
                            myApp.activeCompanyName,
                            myApp.activeStockCode,
                            context
                        )
                        apiRequest.RequestData(
                            myApp.previousBusinessDay,
                            myApp.theDayBeforeIndexClose,
                            myApp.theDayBeforeActiveCompanyName,
                            myApp.theDayBeforeActiveStockCode,
                            context
                        )

                        val difference = myApp.difference
                        val companyName = myApp.companyName
                        val stockCodeList = myApp.stockCode

                        difference.clear()
                        for (i in 0 until onTheDayIndexClose.size) {
                            if (theDayBeforeActiveCompanyName.contains(activeCompanyName[i])) {
                                val companyCodeIndexNum = companyName.indexOf(activeCompanyName[i])
                                val stockCode = stockCodeList[companyCodeIndexNum]
                                val theDayBeforeActiveStockCodeIndexNum =
                                    theDayBeforeActiveStockCode.indexOf(stockCode)
                                if (onTheDayIndexClose[i] != "null" && theDayBeforeIndexClose[theDayBeforeActiveStockCodeIndexNum] != "null") {
                                    val differenceNum =
                                        onTheDayIndexClose[i].toFloat() - theDayBeforeIndexClose[theDayBeforeActiveStockCodeIndexNum].toFloat()

                                    difference.add(differenceNum.toString())
                                } else {
                                    difference.add("-")
                                }
                            } else {
                                difference.add("-")
                            }
                        }

                        val watchListIndexClose = myApp.watchListIndexClose
                        val watchListDifference = myApp.watchListDifference
                        watchListIndexClose.clear()
                        watchListDifference.clear()
                        for (i in 0 until watchList.size) {
                            val watchListIndexNum = activeCompanyName.indexOf(watchList[i])
                            if (watchListIndexNum != -1) {
                                watchListIndexClose.add(onTheDayIndexClose[watchListIndexNum])
                                watchListDifference.add(difference[watchListIndexNum])
                            }
                        }
                        navController.navigate("watchListScreen")
                    }
                    is Result.Failure -> {
                        Toast.makeText(context, "通信エラーが発生しました", Toast.LENGTH_LONG).show()
                    }
                }
            }
    }

    fun TradingCalender(context: Context){
        //休日に対応するため5日間分のカレンダーを取得
        val today = LocalDateTime.now()
        val dtFormat = DateTimeFormatter.ofPattern("yyyyMMdd")
        val toDay = today.minusDays(84).format(dtFormat)
        val fromDay = today.minusDays(89).format((dtFormat))

        //header設定
        val idToken = myApp.idToken
        val header = mapOf("Authorization" to idToken)

        runBlocking {
            val (_, response, result) =
                "https://api.jquants.com/v1/markets/trading_calendar?from=$fromDay&to=$toDay"
                    .httpGet()
                    .header(header)
                    .awaitStringResponseResult()
            result.fold(
                {
                    //取得データを格納
                    val holidayDivisionAdapter = moshi.adapter(HolidayDivision::class.java)
                    val res = String(response.body().toByteArray())
                    val data = holidayDivisionAdapter.fromJson(res)
                    val tradingCalendar = data?.trading_calendar

                    //営業日の登録
                    val referenceDate = mutableListOf<String>()
                    if(tradingCalendar != null){
                        for(i in 5 downTo 0){
                            if(tradingCalendar[i].HolidayDivision == "1"){
                                referenceDate.add(tradingCalendar[i].Date)
                            }
                            //2営業日リスト追加できたらループからはずれる
                            if (referenceDate.size == 2){
                                break
                            }
                        }
                        myApp.referenceDate = referenceDate[0]
                        myApp.previousBusinessDay = referenceDate[1]
                    }
                },
                {
                    Toast.makeText(context, "通信エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    fun RequestData(day: String, indexCloseList: MutableList<String>, activeCompanyName: MutableList<String>, codeList: MutableList<String>, context: Context) {
        val stockCode = myApp.stockCode
        val companyName = myApp.companyName
        val idToken = myApp.idToken
        val header = mapOf("Authorization" to idToken)

        runBlocking {
            val (_, response, result) = "https://api.jquants.com/v1/prices/daily_quotes?date=$day"
                .httpGet()
                .header(header)
                .awaitStringResponseResult()
            result.fold(
                {
                    val dailyQuotesAdapter = moshi.adapter(DailyQuotes::class.java)
                    val res = String(response.body().toByteArray())
                    val data = dailyQuotesAdapter.fromJson(res)
                    val dataQuotes = data?.daily_quotes

                    indexCloseList.clear()
                    activeCompanyName.clear()
                    codeList.clear()
                    if (dataQuotes != null) {
                        for (i in dataQuotes.indices) {
                            codeList.add(dataQuotes[i].Code)
                            indexCloseList.add(dataQuotes[i].Close.toString())
                        }
                        for(i in 0 until codeList.size) {
                            if (stockCode.contains(codeList[i])){
                                val indexNum = stockCode.indexOf(codeList[i])
                                activeCompanyName.add(companyName[indexNum])
                            } else {
                                activeCompanyName.add("-")
                            }
                        }
                    }
                },
                {
                    Toast.makeText(context, "通信エラーが発生しました", Toast.LENGTH_LONG).show()
                }
            )
        }
    }

    fun RequestCompanyName(context: Context) {
        val idToken = myApp.idToken
        val header = mapOf("Authorization" to idToken)
        val companyName = myApp.companyName
        val stockCode = myApp.stockCode
        val TradingCalender = myApp.referenceDate
        val referenceDate = TradingCalender.replace("-", "")

        runBlocking {
            val (_, response, result) = "https://api.jquants.com/v1/listed/info?date=$referenceDate"
                .httpGet()
                .header(header)
                .awaitStringResponseResult()
            result.fold(
                {
                    val infoAdapter = moshi.adapter(Info::class.java)
                    val res = String(response.body().toByteArray())
                    val data = infoAdapter.fromJson(res)
                    val info = data?.info

                    companyName.clear()
                    stockCode.clear()
                    if (info != null) {
                        for (i in info.indices) {
                            companyName.add(info[i].CompanyName)
                            stockCode.add(info[i].Code)
                        }
                    }
                },
                {
                    Toast.makeText(context, "通信エラーが発生しました", Toast.LENGTH_LONG).show()

                }
            )
        }
    }
}