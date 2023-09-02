package com.example.stockpriceapp

data class Parameter(
    val mailaddress: String,
    val password: String
)

data class resultRefreshToken(
    val refreshToken: String
)

data class resultIdToken(
    val idToken: String
)

data class DailyQuotes(
    val daily_quotes: List<DailyQuotesData>
)

data class DailyQuotesData(
    val Date: String,
    val Code: String,
    val Open: Float?,
    val High:Float?,
    val Low: Float?,
    val Close: Float?,
    val UpperLimit: String,
    val LowerLimit: String,
    val Volume: Float?,
    val TurnoverValue: Float?,
    val AdjustmentFactor: Float?,
    val AdjustmentOpen: Float?,
    val AdjustmentLow: Float?,
    val AdjustmentClose: Float?,
    val AdjustmentVolume: Float?
)

data class DataList (
    val indexName: MutableList<String>,
    val indexClose: MutableList<String>
)

