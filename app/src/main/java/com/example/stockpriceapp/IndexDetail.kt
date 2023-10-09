package com.example.stockpriceapp

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndexDetail(navController: NavController, companyName: String){
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
            fontSize = 20.sp
        )
        Button(
            onClick = { /*TODO*/ },
            colors = ButtonDefaults.buttonColors(Color.Gray)
        ) {
            Text(
                text = "ウォッチリストに登録",
            )
        }
    }
}