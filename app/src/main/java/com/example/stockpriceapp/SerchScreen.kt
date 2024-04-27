@file:Suppress("IMPLICIT_CAST_TO_ANY")

package com.example.stockpriceapp

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.stockpriceapp.ui.theme.StockPriceAppTheme
import kotlin.math.round

@SuppressLint("MutableCollectionMutableState")
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
                val displayList by remember { mutableStateOf(companyData) }
                Column {
                    TopBar("検索")
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                ){
                    SerchList(navController, displayList)
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

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SerchList(
    navController: NavController,
    companyData: MutableList<CompanyData>
){
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    var changedListFlg by remember { mutableStateOf(0) }
    val changedList: MutableList<CompanyData> = mutableListOf()
    Box(modifier = Modifier
        .fillMaxWidth()
        .height(48.dp)
    ) {
        var text by remember { mutableStateOf("" ) }
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
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                if (!text.isEmpty()){
                    changedList.clear()
                    for (item in companyData){
                        if (item.companyName.contains(text)){
                            changedList.add(item)
                        }
                    }
                    changedListFlg += 1
                } else {
                    changedListFlg = 0
                }
                keyboardController?.hide()
            })
        )
    }

    Column(modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.End
    ){
        Text(
            text = "(基準日:$referenceDate)",
            fontSize = 10.sp,
            color = Color.White
        )
    }

    LazyColumn(modifier = Modifier
        .fillMaxWidth()
        .size(617.dp)
    ) {
        if(changedListFlg == 0) {
            itemsIndexed(companyData) { _, list ->
                val (indexClose, difference) = setDisplayData(list.onTheDayIndexClose, list.theDayBeforeIndexClose)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("indexDetail/${list.companyName}/$indexClose/$difference") }
                ) {
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

                        Column {
                            Text(
                                text = list.companyName,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = 15.sp,
                                color = Color.White,
                                modifier = Modifier
                                    .padding(start = 15.dp, top = 15.dp),
                            )

                            Row {
                                Text(
                                    context.getString(R.string.indexClose),
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(start = 15.dp)
                                )

                                Text(
                                    indexClose,
                                    fontSize = 20.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(start = 10.dp)
                                )
                                Text(
                                    context.getString(R.string.difference),
                                    fontSize = 15.sp,
                                    color = Color.White,
                                    modifier = Modifier
                                        .padding(start = 30.dp)
                                )

                                Text(
                                    difference,
                                    fontSize = 20.sp,
                                    color = if (difference != "-") {
                                        if (difference.toFloat() >= 0) {
                                            Color.Green
                                        } else {
                                            Color.Red
                                        }
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
        } else {
            itemsIndexed(changedList){ _, list ->
                val (indexClose, difference) = setDisplayData(list.onTheDayIndexClose, list.theDayBeforeIndexClose)

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { navController.navigate("indexDetail/${list.companyName}/$indexClose/$difference") }
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

                        Column {
                            Text(
                                text = list.companyName,
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
                                    indexClose,
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
                                    difference,
                                    fontSize = 20.sp,
                                    color = if (difference != "-"){
                                        if (difference.toFloat() >= 0) {Color.Green } else {Color.Red}
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