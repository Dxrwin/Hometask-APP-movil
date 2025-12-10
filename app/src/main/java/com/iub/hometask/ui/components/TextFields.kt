package com.iub.hometask.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.iub.hometask.ui.theme.InputBackground
import com.iub.hometask.ui.theme.InputBorder
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

@Composable
fun HomeTaskTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String = "",
    modifier: Modifier = Modifier,
    isPassword: Boolean = false ,
    readOnly: Boolean = false, // <--- NUEVO: Para bloquear teclado
    trailingIcon: @Composable (() -> Unit)? = null // <--- ¡AQUÍ ESTABA EL ERROR!
) {

    val passwordVisible = remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text(text = placeholder, color = TextPrimary.copy(alpha = 0.5f)) },
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = TextPrimary,   // Color del texto al escribir
            unfocusedTextColor = TextPrimary, // Color del texto al salir
            cursorColor = MaterialTheme.colorScheme.primary, // Color de la barrita |
            disabledContainerColor = InputBackground,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = Color.Transparent
        ),
        visualTransformation = if (isPassword && !passwordVisible.value)
            PasswordVisualTransformation() else VisualTransformation.None,
        singleLine = true,
        readOnly = readOnly, // Pasamos el readOnly
        trailingIcon = {
            // Lógica combinada: Si es password, mostramos el ojo. Si no, mostramos el trailingIcon que nos pasen (la flechita del menú)
            if (isPassword) {
                IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                    Icon(
                        imageVector = if (passwordVisible.value) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle password visibility"
                    )
                }
            } else {
                // Si nos pasaron un ícono (como el del menú desplegable), lo pintamos aquí
                trailingIcon?.invoke()
            }
        }
    )
}
