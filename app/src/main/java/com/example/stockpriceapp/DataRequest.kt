package com.example.stockpriceapp

import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.google.gson.Gson
import com.squareup.moshi.JsonReader
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class RequestIndexData {

    fun RequestData(): List<String> {

        val indexData = listOf("日経平均","TOPIX","NYダウ","S＆P","ドル円","千葉銀行","AGC","大日本印刷","三菱UFJ銀行","SMBC","みずほ銀行")

        return indexData
    }
}