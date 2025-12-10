package com.iub.hometask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation.compose.rememberNavController
import com.iub.hometask.navigation.HomeTaskNavGraph
import com.iub.hometask.ui.theme.HomeTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        android.util.Log.e("PRUEBA_LOG", "¡HOLA! SI VES ESTO, EL LOGCAT FUNCIONA")
        super.onCreate(savedInstanceState)
        setContent {
            HomeTaskApp()
        }
    }
}

@Composable
fun HomeTaskApp() {
    HomeTaskTheme {
        val navController = rememberNavController()
        HomeTaskNavGraph(navController = navController)
    }
}
