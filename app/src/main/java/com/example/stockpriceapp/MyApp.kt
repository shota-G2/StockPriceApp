package com.example.stockpriceapp

import android.app.Application

class MyApp: Application() {

    lateinit var idToken: String
    //直近営業日
    lateinit var referenceDate: String
    //前営業日
    lateinit var previousBusinessDay: String
    //全企業名・銘柄コード・基準日取引企業
    var companyName = mutableListOf("")
    var stockCode = mutableListOf("")
    var activeCompanyName = mutableListOf("")
    var theDayBeforeActiveCompanyName = mutableListOf("")
    //直近営業日終値
    var onTheDayIndexClose = mutableListOf("")
    //前営業日終値
    var theDayBeforeIndexClose = mutableListOf("")
    //ウォッチリスト
    var watchList = mutableListOf("")

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