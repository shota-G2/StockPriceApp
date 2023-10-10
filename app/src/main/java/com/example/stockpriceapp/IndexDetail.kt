package com.example.stockpriceapp

import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import io.realm.Realm
import io.realm.kotlin.delete
import io.realm.kotlin.where

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexDetail(navController: NavController, companyName: String){
    val myApp = MyApp.getInstance()
    val watchList = myApp.watchList

    val buttonDisplayCheck: Boolean = watchList.contains(companyName)

    val buttonText = if (!buttonDisplayCheck){ "登録" } else { "削除" }

    Column(modifier = Modifier
        .background(Color.Black)
        .fillMaxSize()
    ) {
        TopAppBar(
            colors = TopAppBarDefaults.smallTopAppBarColors(Color.Black),
            title = { Text(text = "")},
            navigationIcon = {
                IconButton(onClick = { navController.navigateUp() },
                    ) {
                    Icon(
                        painter = rememberVectorPainter(image = Icons.Default.ArrowBack),
                        contentDescription = "",
                        tint = Color.White
                    )
                }
            }
        )
        Text(
            text = companyName,
            color = Color.White,
            fontSize = 20.sp
        )
        Button(
            onClick = {
                if (buttonText == "登録"){
                    val realm = Realm.getDefaultInstance()
                    val registeredIndexList = RegisteredIndexList()
                    registeredIndexList.id = "marimocag2@gmail.com"
                    registeredIndexList.registeredIndexList = companyName
                    realm.use { realm ->
                        realm.executeTransaction {
                            it.insertOrUpdate(registeredIndexList)
                        }
                    }
                    myApp.watchList.add(companyName)
                    navController.navigate("watchListScreen")
                } else {
                    val realm = Realm.getDefaultInstance()
                    realm.use { reslm ->
                        realm.executeTransaction {
                            realm.where(RegisteredIndexList::class.java)
                                .equalTo("registeredIndexList", companyName)
                                .findAll().deleteAllFromRealm()
                        }
                    }
                    myApp.watchList.remove(companyName)
                    navController.navigate("watchListScreen")
                }

            },
            colors = ButtonDefaults.buttonColors(Color.Gray)
        ) {
            Text(
                text = buttonText
            )
        }

    }
}