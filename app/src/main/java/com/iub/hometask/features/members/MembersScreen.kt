package com.iub.hometask.features.members

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // <--- ¡ESTA ES LA IMPORTACIÓN QUE FALTABA!
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.iub.hometask.ui.components.HomeBottomNavigationBar // Asegúrate de importar esto
import com.iub.hometask.navigation.Routes
import coil.request.ImageRequest
import com.iub.hometask.data.repository.MemberUiModel
import android.util.Log
import coil.compose.SubcomposeAsyncImage


// Colores consistentes con tu tema oscuro
private val DarkBackground = Color(0xFF121212)
private val CardBackground = Color(0xFF1E1E1E)
private val TextWhite = Color(0xFFEEEEEE)
private val TextGray = Color(0xFFAAAAAA)

@Composable
fun MembersScreen(
    onBackClick: () -> Unit,
    onNavigateBottom: (String) -> Unit,
    onAddMemberClick: () -> Unit,
    onMemberClick: (Int) -> Unit,
    onChatClick: (Int) -> Unit,
    viewModel: MembersViewModel = viewModel(factory = MembersViewModel.Factory)
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = DarkBackground,
        bottomBar = {
            HomeBottomNavigationBar(
                currentRoute = Routes.MEMBERS, // Marcamos "Miembros" como activo
                onItemSelected = { route -> onNavigateBottom(route) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddMemberClick,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Agregar Miembro")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when (val state = uiState) {
                is MembersUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                is MembersUiState.Error -> {
                    Text(
                        text = state.message,
                        color = Color.Red,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(16.dp)
                    )
                }
                is MembersUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        item {
                            Text(
                                "Miembros del Hogar",
                                color = TextWhite,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }

                        // Gracias al import 'androidx.compose.foundation.lazy.items', esto funciona:
                        items(state.members) { member ->
                            MemberCard(
                                member = member,
                                onClick = { onMemberClick(member.id) },
                                onChatClick = { onChatClick(member.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MemberCard(
    member: MemberUiModel,
    onClick: () -> Unit,
    onChatClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // IMAGEN CON COIL + LOGS DE ERROR VISUALES
            if (member.imageUrl != null) {
                SubcomposeAsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(member.imageUrl)
                        .crossfade(true)
                        .build(),
                    contentDescription = member.name,
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    // Indicador de carga
                    loading = {
                        CircularProgressIndicator(
                            modifier = Modifier.padding(10.dp),
                            strokeWidth = 2.dp
                        )
                    },
                    // Manejador de Error (Con Log y UI Roja)
                    error = {
                        val e = it.result.throwable
                        // Este log aparecerá en Logcat al buscar "COIL"
                        Log.e("COIL_LOG", " Error cargando: ${member.imageUrl}")
                        Log.e("COIL_LOG", "   Causa: ${e.message}")

                        // Icono rojo en pantalla
                        Box(
                            modifier = Modifier.fillMaxSize().background(Color.Red.copy(0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Person, contentDescription = "Error", tint = Color.Red)
                        }
                    }
                )
            } else {
                // Placeholder si no hay URL
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            //Información del usuario
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.name,
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = member.roleName,
                    color = TextGray,
                    fontSize = 14.sp
                )
                Text(
                    text = member.email, // Agregué el email para verificar datos
                    color = TextGray.copy(alpha = 0.7f),
                    fontSize = 12.sp
                )
            }

            // Botón Chat
            IconButton(onClick = onChatClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Chat",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}