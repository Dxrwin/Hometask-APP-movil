package com.iub.hometask.features.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.components.HomeBottomNavigationBar
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.TextPrimary

@Composable
fun SimplePlaceholderScreen(
    title: String,
    currentRoute: String,
    onNavigateBottom: (String) -> Unit
) {
    Scaffold(
        containerColor = BackgroundDark,
        bottomBar = {
            HomeBottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = onNavigateBottom
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 22.sp
            )
        }
    }
}
