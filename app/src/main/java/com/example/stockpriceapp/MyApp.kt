package com.example.stockpriceapp

import android.app.Application

class MyApp: Application() {

    lateinit var idToken: String
    //直近営業日
    lateinit var referenceDate: String
    //前営業日
    lateinit var previousBusinessDay: String
    //全企業名・銘柄コード
    var companyName = mutableListOf("")
    var stockCode = mutableListOf("")

    //基準日取引企業・銘柄コード・終値
    var activeCompanyName = mutableListOf("")
    var activeStockCode = mutableListOf("")
    var onTheDayIndexClose = mutableListOf("")

    //前日取引企業・銘柄コード・終値
    var theDayBeforeActiveCompanyName = mutableListOf("")
    var theDayBeforeActiveStockCode = mutableListOf("")
    var theDayBeforeIndexClose = mutableListOf("")

    //検索画面前日比
    var difference = mutableListOf("")

    //ウォッチリスト
    var watchList = mutableListOf("")
    var watchListIndexClose = mutableListOf("")
    var watchListDifference  = mutableListOf("")

    companion object {
        private var instance: MyApp? = null

        fun getInstance(): MyApp {
            if(instance == null) {
                instance = MyApp()
            }
            return instance!!
        }
    }
}