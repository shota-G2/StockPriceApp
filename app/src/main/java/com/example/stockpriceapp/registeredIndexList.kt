package com.example.stockpriceapp

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class RegisteredIndexList: RealmObject(){
    var id: String? = null
    @PrimaryKey
    var registeredIndexList: String? = null

}