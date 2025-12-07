package com.iub.hometask.features.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.BackgroundDark
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.PrimaryBlue
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    onBackClick: () -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            SettingsTopBar(onBackClick = onBackClick)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // Tabs
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier
                    .fillMaxWidth()
                    .background(BackgroundDark),
                containerColor = BackgroundDark,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        height = 3.dp,
                        color = PrimaryBlue
                    )
                }
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Text(
                            "Principal",
                            color = if (selectedTab == 0) PrimaryBlue else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Text(
                            "Miembros",
                            color = if (selectedTab == 1) PrimaryBlue else TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = {
                        Text(
                            "Notificaciones y Otros",
                            color = if (selectedTab == 2) PrimaryBlue else TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                )
            }

            when (selectedTab) {
                0 -> PrincipalTab()
                1 -> MiembrosTab()
                2 -> NotificacionesTab()
            }

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsTopBar(onBackClick: () -> Unit) {
    TopAppBar(
        title = {
            Text(
                text = "Ajustes",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Text("←", fontSize = 20.sp, color = TextPrimary)
            }
        },
        actions = {
            IconButton(onClick = { /* TODO: Perfil usuario */ }) {
                Text("👤", fontSize = 18.sp)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = BackgroundDark)
    )
}

@Composable
private fun PrincipalTab() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tema
        SettingsSection(title = "Tema") {
            val darkModeEnabled = remember { mutableStateOf(true) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🌙", fontSize = 18.sp)
                    Text(
                        "Modo Oscuro",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
                Switch(
                    checked = darkModeEnabled.value,
                    onCheckedChange = { darkModeEnabled.value = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PrimaryBlue,
                        uncheckedThumbColor = TextSecondary
                    )
                )
            }
        }

        // Idiomas
        SettingsSection(title = "Idiomas") {
            SettingOptionRow(label = "Idioma", value = "Español", icon = "🌐")
        }

        // Configuración de Perfil
        SettingsSection(title = "Configuración de Perfil") {
            SettingOptionRow(label = "Editar perfil", icon = "✏️")
            Divider(
                color = CardDark,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            SettingOptionRow(label = "Cambiar contraseña", icon = "🔒")
        }
    }
}

@Composable
private fun MiembrosTab() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Gestiona los miembros de tu hogar",
            color = TextSecondary,
            fontSize = 13.sp,
            modifier = Modifier.padding(top = 8.dp)
        )

        SettingsSection(title = "") {
            SettingOptionRow(label = "Agregar miembro", icon = "➕")
            Divider(
                color = CardDark,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            SettingOptionRow(label = "Ver permisos", icon = "📋")
        }
    }
}

@Composable
private fun NotificacionesTab() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        SettingsSection(title = "Notificaciones") {
            val pushNotifications = remember { mutableStateOf(true) }
            val emailNotifications = remember { mutableStateOf(false) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("🔔", fontSize = 18.sp)
                    Text(
                        "Notificaciones Push",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
                Switch(
                    checked = pushNotifications.value,
                    onCheckedChange = { pushNotifications.value = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PrimaryBlue,
                        uncheckedThumbColor = TextSecondary
                    )
                )
            }

            Divider(
                color = CardDark,
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("📧", fontSize = 18.sp)
                    Text(
                        "Notificaciones por Email",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                }
                Switch(
                    checked = emailNotifications.value,
                    onCheckedChange = { emailNotifications.value = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = PrimaryBlue,
                        uncheckedThumbColor = TextSecondary
                    )
                )
            }
        }

        SettingsSection(title = "Privacidad") {
            SettingOptionRow(label = "Ver política de privacidad", icon = "🔐")
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        if (title.isNotEmpty()) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 12.dp)
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardDark, shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            content()
        }
    }
}

@Composable
private fun SettingOptionRow(
    label: String,
    value: String? = null,
    icon: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { /* TODO */ }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = icon, fontSize = 18.sp)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = TextPrimary,
                fontSize = 14.sp
            )
        }
        if (value != null) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = value,
                    color = TextSecondary,
                    fontSize = 13.sp
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        } else {
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}


