package com.example.stockpriceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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

class WatchList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}

//class IndexDataViewModel(idToken: String): ViewModel() {
//    val RepuestIndexData = RequestIndexData().RequestData(idToken)
//    val indexData = MutableLiveData(RepuestIndexData)
//    val indexList = indexData.value
//}

@Composable
fun WatchListScreen(navController: NavController, idToken: String = "idToken") {
    StockPriceAppTheme {
        //A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .background(Color.Black)
            ) {
                TopBar("ウォッチリスト")
                WatchList(navController, idToken)
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
        actions = {
            IconButton(onClick = { /* do something */ },
                colors = IconButtonDefaults.iconButtonColors(Color.Black),
            ) {
                Icon(Icons.Filled.Search, contentDescription = "Search text")
            }
        }
    )
}

@Composable
fun WatchList(navController: NavController, idToken: String) {

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

//    val viewModel: IndexDataViewModel = viewModel()
//    val indexList = viewModel.indexList!!


    val (indexName, indexClose) = RequestIndexData().RequestData(idToken)

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .size(650.dp)
    ) {
        itemsIndexed(indexName){ indexNum, indexName ->
            Row(verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
            ){
                Box {
                    Image(painter = painterResource(id = R.drawable.wine),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp,)
                            .size(70.dp)
                    )

                    Column {
                        Text(text = indexName,
                            fontSize = 20.sp,
                            color = Color.White,
                            modifier = Modifier
                                .padding(start = 15.dp, top = 15.dp)
                        )

                        Row {
                            Text("前日終値",
                                fontSize = 15.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(start = 15.dp)
                            )

                            Text(indexClose[indexNum],
                                fontSize = 25.sp,
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
                                fontSize = 25.sp,
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
        .background(Color.Black),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {

        val textColor = Color.White

        TextButton(onClick = { /*TODO*/ },
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
        TextButton(onClick = { /*TODO*/ },
        ) {
            Text("ポートフォリオ",
                color = textColor,
                fontSize = fontSize
            )
        }
        TextButton(onClick = { /*TODO*/ },
        ) {
            Text("まなぶ",
                color = textColor,
                fontSize = fontSize
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun WatchListScreenPreview() {
    val navController = rememberNavController()
    StockPriceAppTheme {
        WatchListScreen(navController)
    }
}