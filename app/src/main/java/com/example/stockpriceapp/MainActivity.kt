package com.example.stockpriceapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.result.Result
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            LoginScreen(navController = navController)
            NavHost(navController = navController, startDestination = "loginScreen" ){
                composable("loginScreen"){ LoginScreen(navController = navController)}
                composable("watchListScreen"){ WatchListScreen(navController)}
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

                Column(){
                    var text by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = text,
                        onValueChange = { text = it },
                        label = {
                            Text(text = "mailaddress",
                                color = Color.White
                            )
                        }
                    )

                    OutlinedTextField(value = text,
                        onValueChange = { text = it },
                        label = {
                            Text(text = "password",
                                color = Color.White
                            )
                        },
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
                                        val data = result.get()

                                        //変数refreshTokenにAPIから返ってきたrefreshTokenを格納する
//                                        val refreshToken = refreshTokenResultAdapter.fromJson(data)
                                        val refreshToken = "eyJjdHkiOiJKV1QiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiUlNBLU9BRVAifQ.N01N7OTIQMHVhx62tU0p_ba82_p8-ss0OHFyI8jmy9maOUgCMedlB6VQfRn7G5nzdLIcSlf7AlCbL1oPb9nRr2phZ6sPRYNXXa81wRWTUpQtp3_qZBfD4rH7Ggdk5MU9tQ3NUHYFsrqIthTkTPBDdjbfNxmB_C78q8c_jWa8zuwADvQxljhVFzcqjzCKdUboe7oKFae7of_gr80HD94B4VDTpne8wyUSGPEbr40pceAf8uUAKsYu15l5gLImeZl_WXqBLEBSc6apDP4cCW5dcN8ganmKNcVY75bRRSF9gzn36_PoHxa68pJEbysjLoH8Km3EXfHsgYZRWFxiJy2vLQ.OJA3u6VXgtBN_sMY.L-Fwteuguty1fjFY46MfDOYiVSJnEaPcnLYRhBBg1PZnO0dVGnh4DJaPlfiCOnHNEIo-N5KGZgAUXEIKAVVrlO5UEqCOE9IhmXqOHmFji6INe42PCcnihVps5huW2RNRvVGL1FDu8GDpVkpnCtgdbOGAWXxz7NhVqzSNudoAg-q3xvz1dq3qKPwfO2IQowSY4owZOmTPKiQBGUp10-Y_8i--nnuZhvNQZ5qG5yJy36pDvnaPkEziGYEw9n5itG1bX7eCIDa7uweplykVyNwialUBCtn65S78OH63KbXw4zw1yZ5yQ9QfDTO3-81PjFP--qpzW2jbKm8F5hFXe0Qnq2fwrDUaAHJ-jgh8MEQDx9t1qwqCHkoSQXWE7Xh8APadJjvyb8cQDpsqZbxkthkn5oDxjHVwgDTRliHpJjluv-UjuLBjiUgrb3ngfQo2BALzbK62rtrhGld_spemRJW7wHGU4f1DFyBZ7VfSELaR0lIySuYnBW2F0BqhCnWLMtNXePSJ74fj-90b97A0pzpkZFk55TYN2kIJ2I0pFoTsRyYLan1jtsxNo72DH6N-eEU-wzlzrkyBVg_Bh2UAdGNLTt2dJux4upISePdfKZmgdbhW7NQMUr6K_6DwBPOCI75Pdbc1nqVOePRPGPWjnJk3RyohRQ0cXUgQfeJvFHcU7T0tvCS5my60P6Z2Svmlr85tCKRBAVgyDJZqJzsimS8-I7gh_CEQPJdKlxOTIgZof_nmXc9eQ-nlcxbKxGMsky1LcHZnIVI7ylaEIVsTJRVGHjx5fSZMNM7-q2u60r3sdrfeU8Pi6qoSAevIsYL4N0o-xM99bIkHX1kzviakqy8riX4kqCwWdhzLwG9FL8v6LxUGIqWvatXZdjjVVSmy1IK1d3uKEAcmGVyCyuVrDfUOHGK4AQ4SbAromm6iAxsbjMq_dFL6YEeqHpoDTzKtewFo6rUi8__gQjd1_bR9_iRfOOuN_hIU8c9_EBgtxUxFHdrYt8y_SyYZA8qDTaUJ0EldkK-qKixprG0ED0XXZDKw8neJcQEYrY_hn9IpahkrAnBK7AamhU_2cLTfDT8wTjV_n_Tq7k4mf1AMsS53B8ORq6JUkXiM0-c7KVKz42WDp-rJb3M9jNJVL-MVx_4zFnTkCHdxlvXY4zwM_70mshsZzwqTtyV5M9cq00-LvntfDM_DO8zJQX-H4FSthkitCnpkkZ78Y_I2zDF28DgH3nr1u_csSP88uKUm9lhvWQ2o_zpofwrp3YuuowNF8iX-2Q7XZajfvy4v0rV13xuQthv6xO6jQr2hnDi0ZWu9QILDqZZSITNj1X7W3BrZG4iD2GDAGjV7_-Py6bpgeQ.prGnEYOBBX9pq8TQ6yZymA"

                                        //idToken取得
                                        Fuel.post("https://api.jquants.com/v1/token/auth_refresh?refreshtoken=$refreshToken")
                                            .response { _, _, result ->
                                                when (result) {
                                                    is Result.Success -> {

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
                        colors = ButtonDefaults.buttonColors(Color.Gray)
                                            ) {
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
