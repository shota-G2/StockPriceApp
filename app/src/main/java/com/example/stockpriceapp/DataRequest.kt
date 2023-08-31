package com.example.stockpriceapp

import android.os.Parcel
import android.os.Parcelable
import android.widget.Toast
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
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class RequestIndexData {

    fun RequestData(idToken: String): List<String> {

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        var indexdata = listOf("")
        val header = mapOf("Authorization" to idToken)

        runBlocking {
            val (_, response, result) = "https://api.jquants.com/v1/prices/daily_quotes?date=20230324"
                .httpGet()
                .header(header)
                .awaitStringResponseResult()
            result.fold(
                { data ->

                    val res = String(response.body().toByteArray())

                    indexdata = listOf(
                        "日経平均",
                        "TOPIX",
                        "NYダウ",
                        "S＆P",
                        "ドル円",
                        "千葉銀行",
                        "AGC",
                        "大日本印刷",
                        "三菱UFJ銀行",
                        "SMBC",
                        "みずほ銀行"
                    )
                },
                { error ->

                }
            )
        }

        return indexdata
        }
    }
