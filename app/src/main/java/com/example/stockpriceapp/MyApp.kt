package com.example.stockpriceapp

import android.app.Application

class MyApp: Application() {
    lateinit var idToken: String
    lateinit var referenceDate: String
    var companyName = mutableListOf("")
    var indexClose = mutableListOf("")

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