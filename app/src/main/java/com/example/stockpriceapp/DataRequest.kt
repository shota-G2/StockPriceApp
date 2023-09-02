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

    fun RequestData(idToken: String): Pair<List<String>, List<String>> {

        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
        val indexName = mutableListOf("")
        val indexClose = mutableListOf("")
        val header = mapOf("Authorization" to idToken)

        runBlocking {
            val (_, response, result) = "https://api.jquants.com/v1/prices/daily_quotes?date=20230324"
                .httpGet()
                .header(header)
                .awaitStringResponseResult()
            result.fold(
                { data ->
                    indexName.remove(indexName[0])
                    indexClose.remove(indexClose[0])
                    val DailyQuotesAdapter = moshi.adapter(DailyQuotes::class.java)
                    val res = String(response.body().toByteArray())
                    val data = DailyQuotesAdapter.fromJson(res)
                    val dataQuotes = data?.daily_quotes

                    if (dataQuotes != null) {
                        for (i in dataQuotes.indices) {
                            indexName.add(dataQuotes[i].Code)
                            indexClose.add(dataQuotes[i].Close.toString())
                        }
                    }
                },
                { error ->

                }
            )
        }

        return indexName to indexClose
        }
    }