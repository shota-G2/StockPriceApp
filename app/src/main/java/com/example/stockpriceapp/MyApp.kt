package com.example.stockpriceapp

import android.app.Application

class MyApp: Application() {
    //refreshToken
    lateinit var refreshToken: String

    //idToken
    lateinit var idToken: String

    //直近営業日
    lateinit var referenceDate: String

    //前営業日
    lateinit var previousBusinessDay: String

    //全企業データ
    var companyData: MutableList<CompanyData> = mutableListOf()

    //ウォッチリストデータ
    var watchListData: MutableList<WatchCompanyData> = mutableListOf()

    companion object {
        private var instance: MyApp? = null

        fun getInstance(): MyApp {
            if (instance == null) {
                instance = MyApp()
            }
            return instance!!
        }
    }
}
