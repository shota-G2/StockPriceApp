package com.example.stockpriceapp

import android.app.Application

class MyApp: Application() {

    //idToken
    lateinit var idToken: String
    //直近営業日
    lateinit var referenceDate: String
    //前営業日
    lateinit var previousBusinessDay: String
    //全企業名・銘柄コード
    var companyName: MutableList<String> = mutableListOf()
    var stockCode: MutableList<String> = mutableListOf()

    //基準日取引企業・銘柄コード・終値
    var activeCompanyName: MutableList<String> = mutableListOf()
    var activeStockCode: MutableList<String> = mutableListOf()
    var onTheDayIndexClose: MutableList<String> = mutableListOf()

    //前日取引企業・銘柄コード・終値
    var theDayBeforeActiveCompanyName: MutableList<String> = mutableListOf()
    var theDayBeforeActiveStockCode: MutableList<String> = mutableListOf()
    var theDayBeforeIndexClose: MutableList<String> = mutableListOf()

    //検索画面前日比
    var difference: MutableList<String> = mutableListOf()

    //ウォッチリスト
    var watchList: MutableList<String> = mutableListOf()
    var watchListIndexClose: MutableList<String> = mutableListOf()
    var watchListDifference : MutableList<String> = mutableListOf()

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