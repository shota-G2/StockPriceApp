package com.example.stockpriceapp


import android.os.Build
import android.os.Bundle
import android.transition.CircularPropagation
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.stockpriceapp.ui.theme.StockPriceAppTheme
import com.github.kittinunf.fuel.Fuel
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.where
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            //Realm設定
            Realm.init(this)
            val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .allowWritesOnUiThread(true)
                .build()
            Realm.setDefaultConfiguration(config)

            //画面遷移設定
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "loginScreen" ){
                composable("loginScreen"){LoginScreen(navController)}
                composable("watchListScreen"){ WatchListScreen(navController)}
                composable("serchScreen"){ SerchScreen(navController)}
                composable("indexDetail/{companyName}"){ backStackEntry ->
                    val companyName = backStackEntry.arguments?.getString("companyName") ?: ""
                    IndexDetail(navController, companyName)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
//ログイン画面
fun LoginScreen(navController: NavController){
    StockPriceAppTheme {
        //A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            var editable by remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxSize()){
                Image(
                    painter = painterResource(id = R.drawable.wine),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )
                Column{
                    var text by remember { mutableStateOf("") }
                    //アドレス入力欄
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = {
                            Text(
                                text = "mailaddress",
                                color = Color.DarkGray
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(),
                        modifier = Modifier.padding(top = 150.dp, start = 55.dp)
                    )

                    //パスワード入力欄
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = {
                            Text(
                                text = "password",
                                color = Color.DarkGray
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(),
                        modifier = Modifier.padding(top = 10.dp, start = 55.dp)
                    )

                    //ログインボタン
                    Button(
                        onClick = {
                            //インジケータ表示フラグ
                            editable = !editable

                            //グローバル変数格納クラスインスタンス化
                            val myApp = MyApp.getInstance()

                            //ウォッチリストをグローバル変数に格納
                            val realm = Realm.getDefaultInstance()
                            val watchList = myApp.watchList
                            watchList.remove("")
                            realm.use { realm ->
                                val result = realm.where<RegisteredIndexList>().findAll()
                                val copyResult = realm.copyFromRealm(result)
                                for (i in 0 until result.size){
                                    watchList.add(copyResult[i]?.registeredIndexList.toString())
                                }
                            }

                            //moshi,adapter設定
                            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                            val refreshTokenRequestAdapter = moshi.adapter(Parameter::class.java)
                            val parameter = Parameter(
                                mailaddress = "marimocag2@gmail.com",
                                password = "princeG21021"
                            )

                            //refreshToken取得
                            Fuel.post("https://api.jquants.com/v1/token/auth_user")
                                .body(refreshTokenRequestAdapter.toJson(parameter))
                                .response { _, response, result ->
                                    when (result) {
                                        is Result.Success -> {
                                            val refreshTokenResultAdapter =
                                                moshi.adapter(resultRefreshToken::class.java)
                                            val data = String(response.body().toByteArray())

                                            //変数refreshTokenにAPIから返ってきたrefreshTokenを格納する
                                            val refreshToken =
                                                refreshTokenResultAdapter.fromJson(data)?.refreshToken

                                            //idToken取得
                                            Fuel.post("https://api.jquants.com/v1/token/auth_refresh?refreshtoken=$refreshToken")
                                                .response { _, idResponse, idResult ->
                                                    when (idResult) {
                                                        is Result.Success -> {
                                                            val idTokenResultAdapter = moshi.adapter(resultIdToken::class.java)
                                                            val res = String(idResponse.body().toByteArray())
                                                            myApp.idToken = idTokenResultAdapter.fromJson(res)?.idToken.toString()
                                                            RequestIndexData().TradingCalender()
                                                            RequestIndexData().RequestCompanyName()
                                                            RequestIndexData().RequestData()

                                                            navController.navigate("watchListScreen")
                                                        }

                                                        is Result.Failure -> {

                                                        }
                                                    }
                                                }
                                        }

                                        is Result.Failure -> {

                                        }
                                    }
                                }
                        },
                        colors = ButtonDefaults.buttonColors(Color.Gray),
                        modifier = Modifier.padding(top = 10.dp, start = 230.dp)
                    ){
                        Text(text = "ログイン")
                    }

                    AnimatedVisibility(visible = editable) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            SimpleProgress()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SimpleProgress() {
    CircularProgressIndicator(color = Color.White)
}