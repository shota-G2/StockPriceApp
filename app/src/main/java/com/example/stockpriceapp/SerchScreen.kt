package com.example.stockpriceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stockpriceapp.ui.theme.StockPriceAppTheme
import java.util.Collections

class SerchScreen : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

        }
    }
}

@Composable
fun SerchScreen(navController: NavController){
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
                TopBar("検索")
                SerchBar()
                SerchList(navController)
                MainMenu(navController)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SerchBar() {
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(60.dp)
    ) {
        var text by remember { mutableStateOf("") }
        TextField(
            value = text,
            onValueChange = { text = it },
            colors = TextFieldDefaults.textFieldColors(),
            label = {
                Text(text = "ファンド名を入力",
                    color = Color.DarkGray
                )
            },
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = { Icon(Icons.Filled.Search, null)},
            singleLine = true
        )
    }
}

val myApp = MyApp.getInstance()
val activeCompanyName = myApp.activeCompanyName
val referenceDate = myApp.referenceDate.replace("-", "/")
val onTheDayIndexClose = myApp.onTheDayIndexClose
val difference = myApp.difference

@Composable
fun SerchList(navController: NavController){
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
        .size(617.dp)
    ) {
        itemsIndexed(activeCompanyName){ indexNum, activeCompanyName ->
            val indexClose = onTheDayIndexClose[indexNum]
            val onTheDaydifference = difference[indexNum]
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { navController.navigate("indexDetail/$activeCompanyName/$indexClose/$onTheDaydifference") }
            ){
                Box {
                    Image(
                        painter = painterResource(id = R.drawable.wine),
                        contentDescription = null,
                        contentScale = ContentScale.FillBounds,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, start = 10.dp, end = 10.dp,)
                            .size(65.dp)
                    )

                    Collections.replaceAll(onTheDayIndexClose, "null", "-")
                    Column {
                        Text(
                            text = activeCompanyName,
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
                                onTheDayIndexClose[indexNum],
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
                                difference[indexNum],
                                fontSize = 20.sp,
                                color = if (difference[indexNum] != "-"){
                                    if (difference[indexNum].toFloat() >= 0) {Color.Green } else {Color.Red}
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


@Preview(showBackground = true)
@Composable
fun SerchScreenPreview() {
    val navController = rememberNavController()
    StockPriceAppTheme {
        SerchScreen(navController)
    }
}