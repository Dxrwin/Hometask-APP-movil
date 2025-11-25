package com.iub.hometask.features.tasks.add

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.iub.hometask.data.mock.MockMembersRepository
import com.iub.hometask.data.mock.MockTasksRepository
import com.iub.hometask.data.mock.TaskCategory
import com.iub.hometask.data.mock.TaskStatus
import com.iub.hometask.navigation.Routes
import com.iub.hometask.ui.components.*
import com.iub.hometask.ui.theme.BackgroundDark
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskScreen(
    onBackClick: () -> Unit,
    onNavigateBottom: (String) -> Unit
) {
    val currentRoute = Routes.TASKS

    val titleState = remember { mutableStateOf("") }
    val descriptionState = remember { mutableStateOf("") }
    var dueDate by remember { mutableStateOf<LocalDate?>(null) }

    val members = MockMembersRepository.members
    val selectedMemberIds = remember { mutableStateListOf<Int>() }

    val scope = rememberCoroutineScope()

    // DatePicker
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale("es", "ES"))
    val formattedDate: String? = dueDate?.format(formatter)

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        dueDate = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.systemDefault())
                            .toLocalDate()
                    }
                    showDatePicker = false
                }) {
                    Text("Aceptar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        containerColor = BackgroundDark,
        topBar = {
            AddTaskTopBar(onBackClick = onBackClick)
        },
        bottomBar = {
            HomeBottomNavigationBar(
                currentRoute = currentRoute,
                onItemSelected = onNavigateBottom
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundDark)
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(8.dp))

            TaskTitleField(
                value = titleState.value,
                onValueChange = { titleState.value = it }
            )

            TaskDescriptionField(
                value = descriptionState.value,
                onValueChange = { descriptionState.value = it }
            )

            TaskDueDateField(
                formattedDate = formattedDate,
                onClick = { showDatePicker = true }
            )

            AssignMembersSection(
                members = members,
                selectedMemberIds = selectedMemberIds,
                onToggleMember = { id ->
                    if (selectedMemberIds.contains(id)) {
                        selectedMemberIds.remove(id)
                    } else {
                        selectedMemberIds.add(id)
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            TaskFormButtons(
                onCancel = { onBackClick() },
                onCreate = {
                    val title = titleState.value.trim()
                    if (title.isBlank()) {
                        // por ahora, validación mínima
                        scope.launch {
                            // podrías mostrar un snackbar si tienes ScaffoldState
                        }
                    } else {
                        val nextId =
                            (MockTasksRepository.tasks.maxOfOrNull { it.id } ?: 0) + 1
                        val label = dueDate?.let {
                            "Vence: ${it.format(formatter)}"
                        } ?: "Vence: Sin fecha"

                        MockTasksRepository.tasks.add(
                            com.iub.hometask.data.mock.HomeTask(
                                id = nextId,
                                title = title,
                                description = descriptionState.value,
                                creationDate = LocalDate.now().toString(),
                                category = TaskCategory.COCINA, // por ahora fijo, luego puedes dejarlo editable
                                status = TaskStatus.PENDING,
                                dueLabel = dueDate?.let {
                                    "Vence: ${it.format(formatter)}"
                                } ?: "Vence: Sin fecha",
                                dueTime = null,
                                assignedMemberIds = if (selectedMemberIds.isEmpty())
                                    members.take(1).map { m -> m.id }
                                else selectedMemberIds.toList()
                            )
                        )
                        onBackClick()
                    }
                }
            )

            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
