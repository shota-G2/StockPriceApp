package com.example.stockpriceapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.stockpriceapp.ui.theme.StockPriceAppTheme

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
                SerchList()
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
            leadingIcon = { Icon(Icons.Filled.Search, null)})
    }
}

@Composable
fun SerchList(){
    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .size(600.dp)
    ) {

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