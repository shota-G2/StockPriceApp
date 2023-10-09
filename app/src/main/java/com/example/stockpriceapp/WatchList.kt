package com.example.stockpriceapp

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stockpriceapp.ui.theme.StockPriceAppTheme
import com.github.kittinunf.fuel.Fuel
import com.google.android.gms.cast.RequestData
import kotlinx.coroutines.runBlocking

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WatchListScreen(navController: NavController) {
    StockPriceAppTheme {
        //A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(modifier = Modifier.background(Color.Black)
            ) {
                TopBar("ウォッチリスト")
                WatchList(navController)
                MainMenu(navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(text: String){
    TopAppBar(
        colors = TopAppBarDefaults.smallTopAppBarColors(Color.Black),
        title = { Text(text = text,
            fontSize = 20.sp,
            color = Color.White,
        )},
    )
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WatchList(navController: NavController) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Gray)
            .horizontalScroll(rememberScrollState())
    ) {
        for (i in 0..5){
            Button(
                onClick = { navController.navigate("serchScreen") },
                colors = ButtonDefaults.buttonColors(Color.Gray)
            ) {
                Text("ウォッチリスト$i")
            }
        }
    }

    val myApp = MyApp.getInstance()
    val companyName = myApp.companyName
    val referenceDate = myApp.referenceDate.replace("-", "/")
    val indexClose = myApp.indexClose

    Column(modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ){
        Text(text = "(基準日:$referenceDate)",
            fontSize = 10.sp,
            color = Color.White
        )
    }

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .size(630.dp)
    ) {
        itemsIndexed(companyName){ indexNum, companyName ->
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("indexDetail/$companyName") }
            ){
                Box {
                    Image(painter = painterResource(id = R.drawable.wine),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp,)
                            .size(65.dp)
                    )

                    Column {

                        Text(text = companyName,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            fontSize = 15.sp,
                            color = Color.White,
                            modifier = Modifier
                                .padding(start = 15.dp, top = 15.dp),
                        )

                        Row {
                            Text("前日終値",
                                fontSize = 15.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(start = 15.dp)
                            )

                            Text(indexClose[indexNum],
                                fontSize = 20.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                            )
                            Text("前日比",
                                fontSize = 15.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(start = 30.dp)
                            )
                            Text("-200.00",
                                fontSize = 20.sp,
                                color = Color.Red,
                                modifier = Modifier
                                    .padding(start = 10.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainMenu(navController: NavController){
    val fontSize = 15.sp
    Row(modifier = Modifier
        .fillMaxWidth()
        .background(Color.Black)
        .size(65.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        val textColor = Color.White

        TextButton(onClick = { navController.navigate("watchListScreen") },
        ) {
            Text("ウォッチリスト",
                color = textColor,
                fontSize = fontSize
            )
        }
        TextButton(onClick = { navController.navigate("serchScreen") },
        ) {
            Text("検索",
                color = textColor,
                fontSize = fontSize
            )
        }
        TextButton(onClick = { navController.navigate("loginScreen") },
        ) {
            Text("サインアウト",
                color = textColor,
                fontSize = fontSize
            )
        }
        TextButton(onClick = { navController.navigateUp() },
        ) {
            Text("まなぶ",
                color = textColor,
                fontSize = fontSize
            )
        }
    }
}