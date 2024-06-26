package com.example.stockpriceapp

import android.os.Build
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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.stockpriceapp.ui.theme.StockPriceAppTheme
import java.util.Collections

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun WatchListScreen(navController: NavController) {
    StockPriceAppTheme {
        //A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .background(Color.Black)
                    .fillMaxSize()
            ) {
                Column {
                    TopBar("ウォッチリスト")
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ){
                    WatchList(navController)
                }
                Column(
                    verticalArrangement = Arrangement.Bottom
                ) {
                    MainMenu(navController)
                }
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
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .horizontalScroll(rememberScrollState())
        ) {
            for (i in 0..5){
                Button(
                    onClick = {  },
                    colors = ButtonDefaults.buttonColors(Color.Gray)
                ) {
                    Text("ウォッチリスト$i")
                }
            }
        }

        val myApp = MyApp.getInstance()
        val watchList = myApp.watchList
        val watchListIndexClose = myApp.watchListIndexClose
        val watchListDifference = myApp.watchListDifference
        val referenceDate = myApp.referenceDate.replace("-", "/")


        Column(modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.End
        ){
            Text(text = "(基準日:$referenceDate)",
                fontSize = 10.sp,
                color = Color.White
            )
        }

        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .size(630.dp)
        ) {
            itemsIndexed(watchList){ indexNum, watchList ->
                val indexClose = watchListIndexClose[indexNum]
                val difference = watchListDifference[indexNum]
                Row(verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("indexDetail/$watchList/$indexClose/$difference")
                        }
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

                        Collections.replaceAll(watchListIndexClose, "null", "-")
                        Column {
                            Text(text = watchList,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 15.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(start = 15.dp, top = 15.dp),
                            )
                            Row {
                                Text(
                                    "前日終値",
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(start = 15.dp)
                                )

                                Text(
                                    watchListIndexClose[indexNum],
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                )
                                Text(
                                    "前日比",
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(start = 30.dp)
                                )
                                Text(
                                    watchListDifference[indexNum],
                                    fontSize = 20.sp,
                                    color = if (watchListDifference[indexNum] != "-"){
                                        if (watchListDifference[indexNum].toFloat() >= 0) {Color.Green } else {Color.Red}
                                    } else {
                                        Color.White
                                    },
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
}

@Composable
fun MainMenu(navController: NavController) {
    val color = Color.White
    val iconSize = Modifier.size(30.dp)
    val textSize = 15.sp

    BottomAppBar(
        modifier = Modifier
            .height(80.dp)
            .clip(RoundedCornerShape(30.dp, 30.dp, 0.dp, 0.dp)),
        containerColor = Color.Gray,

        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(onClick = { navController.navigate("watchListScreen") }) {
                        Icon(
                            Icons.Filled.Favorite,
                            contentDescription = "watchList",
                            modifier = iconSize,
                            tint = color
                        )
                    }
                    IconButton(onClick = { navController.navigate("serchScreen") }) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "search",
                            modifier = iconSize,
                            tint = color
                        )
                    }
                    IconButton(onClick = { navController.navigate("loginScreen") }) {
                        Icon(
                            Icons.Filled.Home,
                            contentDescription = "logout",
                            modifier = iconSize,
                            tint = color
                        )
                    }
                }
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 20.dp, bottom = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,) {
                    Text(
                        "ウォッチリスト",
                        color = color,
                        fontSize = textSize
                        )
                    Text(
                        "検索",
                        color = color,
                        fontSize = textSize
                    )
                    Text(
                        "ログアウト",
                        color = color,
                        fontSize = textSize
                    )
                }
            }
        }
    )
}

//@RequiresApi(Build.VERSION_CODES.O)
//@Preview
//@Composable
//fun WatchScreenPreview(){
//    val navController = rememberNavController()
//    WatchListScreen(navController)
//}
//@Preview
//@Composable
//fun Preview(){
//    val navController = rememberNavController()
//    MainMenu(navController)
//}