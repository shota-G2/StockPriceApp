package com.example.stockpriceapp


import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
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
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "loginScreen" ){
                composable("loginScreen"){LoginScreen(navController)}
                composable("watchListScreen/{idToken}",
                    arguments = listOf(
                        navArgument("idToken"){ type = NavType.StringType}
                    )
                ){ backStackEntry ->
                    val idToken = backStackEntry.arguments?.getString("idToken") ?: ""
                    WatchListScreen(navController, idToken)
                }
                composable("serchScreen"){ SerchScreen(navController)}
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController){
    StockPriceAppTheme {
        //A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.fillMaxSize()){
                Image(painter = painterResource(id = R.drawable.wine),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                )

                Column{
                    var text by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = {
                            Text(text = "mailaddress",
                                color = Color.DarkGray
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(),
                        modifier = Modifier.padding(top = 150.dp, start = 55.dp)
                    )

                    OutlinedTextField(value = text,
                        onValueChange = { text = it },
                        label = {
                            Text(text = "password",
                                color = Color.DarkGray
                            )
                        },
                        colors = TextFieldDefaults.textFieldColors(),
                        modifier = Modifier.padding(top = 10.dp, start = 55.dp)
                    )

                    Button(onClick = {
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
                                        val refreshTokenResultAdapter = moshi.adapter(resultRefreshToken::class.java)
                                        val data = String(response.body().toByteArray())

                                        //変数refreshTokenにAPIから返ってきたrefreshTokenを格納する
                                        val refreshToken = refreshTokenResultAdapter.fromJson(data)?.refreshToken

                                        //idToken取得
                                        Fuel.post("https://api.jquants.com/v1/token/auth_refresh?refreshtoken=$refreshToken")
                                            .response { _, idResponse, idResult ->
                                                when (idResult) {
                                                    is Result.Success -> {
                                                        val idTokenResultAdapter = moshi.adapter(resultIdToken::class.java)
                                                        val res = String(idResponse.body().toByteArray())
                                                        val idToken = idTokenResultAdapter.fromJson(res)?.idToken

                                                        navController.navigate("watchListScreen/$idToken")

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
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    StockPriceAppTheme {
        LoginScreen(navController)
    }
}
