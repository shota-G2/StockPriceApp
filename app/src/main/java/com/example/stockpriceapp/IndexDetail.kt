package com.example.stockpriceapp

import android.graphics.Canvas
import android.graphics.drawable.Icon
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.vector.Path
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.realm.Realm
import io.realm.kotlin.delete
import io.realm.kotlin.where

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexDetail(navController: NavController, companyName: String, indexClose: String, difference: String){
    val myApp = MyApp.getInstance()

    //リスト登録ボタン表示
    val watchList = myApp.watchList
    val buttonText = if (!watchList.contains(companyName)) "登録"  else  "解除"

    //ダイアログ表示フラグ
    var showDialog by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf("Result") }

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
            fontSize = 25.sp,
            modifier = Modifier
                .padding(start = 15.dp)
        )
        Text(
            text = referenceDate,
            color = Color.White,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(top = 5.dp, start = 15.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp, start = 15.dp)
        ) {
            Text(
                text = indexClose,
                color = Color.White,
                fontSize = 30.sp
            )
            Text(
                text = difference,
                color = if (difference == "-") {
                    Color.White
                } else if (difference.toFloat() >= 0) {
                    Color.Green
                } else {
                    Color.Red
                },
                fontSize = 20.sp,
                modifier = Modifier
                    .align(Alignment.Bottom)
                    .padding(start = 40.dp)
            )
        }
        Box(
            modifier = Modifier
                .weight(1f)
        ){
            Image(
                painter = painterResource(id = R.drawable.wine),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
            )

            Canvas(
                modifier = Modifier
                    .width(400.dp)
                    .height(400.dp)
                    .padding(16.dp)
            ) {
                val path = androidx.compose.ui.graphics.Path()
                path.moveTo(0F, 0F)
                path.lineTo(size.width, 0F)
                path.lineTo(size.width, size.height)
                path.lineTo(0F, size.height)

                clipPath(
                    path = path,
                    clipOp = ClipOp.Intersect
                ) {
                    drawPath(
                        path = path,
                        brush = SolidColor(Color.White)
                    )
                }

                drawLine(
                    start = Offset(x = (0.1 * size.width).toFloat(), y = 10f),
                    end = Offset(x = (0.9 * size.width).toFloat(), y = 500f),
                    color = Color.Black,
                    strokeWidth = 10f
                )
            }

            Button(
                onClick = {
                    showDialog = true
                },
                colors = if(buttonText == "登録") {
                    ButtonDefaults.buttonColors(Color.Green)
                } else {
                    ButtonDefaults.buttonColors(Color.Gray)
                },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 10.dp, bottom = 10.dp)
            ) {
                Text(
                    text = buttonText
                )
            }
        }
    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = {
                result = "Dismiss"
                showDialog = false
            },
            confirmButton = {
                TextButton(
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
                            myApp.watchListIndexClose.add(indexClose)
                            myApp.watchListDifference.add(difference)
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
                            myApp.watchListIndexClose.remove(indexClose)
                            myApp.watchListDifference.remove(difference)
                            navController.navigate("watchListScreen")
                        }
                        showDialog = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        result = "Cancel"
                        showDialog = false
                    }
                ) {
                    Text("Cancel")
                }
            },
            text = {
                Text(
                    text = if (buttonText == "登録") { "ウォッチリストに登録しますか？" } else { "ウォッチリスト登録を解除しますか？" }
                )
            },
        )
    }
}