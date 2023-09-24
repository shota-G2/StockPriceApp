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

data class Info (
    val info: List<InfoData>
)

data class InfoData(
    val Date: String,
    val Code: String,
    val CompanyName: String,
    val CompanyNameEnglish:String,
    val Sector17Code: String,
    val Sector17CodeName: String,
    val Sector33Code: String,
    val Sector33CodeName: String,
    val ScaleCategory: String,
    val MarketCode: String,
    val MarketCodeName: String,
)

data class HolidayDivision(
    val trading_calendar: List<Date>
)

data class Date(
    val Date: String,
    val HolidayDivision: String
)