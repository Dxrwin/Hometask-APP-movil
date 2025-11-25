package com.iub.hometask.features.members

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.data.mock.MockMembersRepository
import com.iub.hometask.navigation.Routes
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.TextPrimary

@Composable
fun MembersScreen(
    onBackClick: () -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    val currentRoute = Routes.MEMBERS
    val members = MockMembersRepository.members

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            MembersTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            HomeBottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = onNavigateBottom
            )
        },
        floatingActionButton = {
            PrimaryFab(onClick = { /* TODO: añadir miembro */ })
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Miembros Actuales",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            members.forEach { member ->
                MemberListItem(
                    member = member,
                    onEditClick = { /* TODO: editar miembro */ }
                )
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
