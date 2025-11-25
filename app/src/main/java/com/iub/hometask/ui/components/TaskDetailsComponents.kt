package com.iub.hometask.ui.components

import android.graphics.PathMeasure
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.PrimaryBlue
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

@Composable
fun SatisfyingCheckboxItem(
    checked: Boolean,
    // CORRECCIÓN: Faltaba "-> Unit" para indicar que es una función lambda
    onCheckedChange: (Boolean) -> Unit,
    label: String
) {
    val transition = updateTransition(checked, label = "checkbox")

    val bgColor by transition.animateColor(label = "bgColor") { isChecked ->
        if (isChecked) PrimaryBlue else Color.Transparent
    }

    val borderColor by transition.animateColor(label = "borderColor") { isChecked ->
        if (isChecked) PrimaryBlue else TextSecondary.copy(alpha = 0.5f)
    }

    val checkProgress by transition.animateFloat(
        transitionSpec = { tween(durationMillis = 400) },
        label = "checkProgress"
    ) { isChecked -> if (isChecked) 1f else 0f }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            // Al hacer click, invocamos la lambda pasando el valor contrario
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .background(bgColor, RoundedCornerShape(6.dp))
                .border(2.dp, borderColor, RoundedCornerShape(6.dp)),
            contentAlignment = Alignment.Center
        ) {
            if (checkProgress > 0f) {
                Canvas(modifier = Modifier.size(14.dp)) {
                    // CORRECCIÓN: Aquí se completó el bloque de código faltante
                    val path = Path().apply {
                        moveTo(size.width * 0.2f, size.height * 0.5f)
                        lineTo(size.width * 0.4f, size.height * 0.8f)
                        lineTo(size.width * 0.8f, size.height * 0.2f)
                    }

                    // Calculamos la longitud del path para animarlo
                    val pathMeasure = PathMeasure(path.asAndroidPath(), false)
                    val length = pathMeasure.length

                    drawPath(
                        path = path,
                        color = Color.White,
                        style = Stroke(
                            width = 2.5.dp.toPx(),
                            cap = StrokeCap.Round,
                            pathEffect = PathEffect.dashPathEffect(
                                floatArrayOf(length * checkProgress, length),
                                0f
                            )
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = label,
            color = if (checked) TextSecondary else TextPrimary,
            textDecoration = if (checked) TextDecoration.LineThrough else null,
            fontSize = 15.sp
        )
    }
}









//descomentar para usar la interfaz por chtgpt
/*import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.iub.hometask.ui.theme.CardDark
import com.iub.hometask.ui.theme.PrimaryBlue
import com.iub.hometask.ui.theme.TextPrimary
import com.iub.hometask.ui.theme.TextSecondary

// -------------------------------------------------------------
//  CARD PRINCIPAL DE INFORMACIÓN DE TAREA
// -------------------------------------------------------------

@Composable
fun TaskInfoCard(
    title: String,
    description: String,
    createdDate: String,
    dueLabel: String,
    dueTimeLabel: String
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(20.dp))
            .padding(16.dp)
    ) {
        Text(
            text = title,
            color = TextPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = description,
            color = TextSecondary,
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row {
            Column {
                Text(
                    text = "Creada",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = createdDate,
                    color = TextPrimary,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "Fecha Límite",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = "$dueLabel, $dueTimeLabel",
                    color = PrimaryBlue,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

// -------------------------------------------------------------
//  CARD DE USUARIO ASIGNADO
// -------------------------------------------------------------

@Composable
fun AssignedUserCard(
    name: String,
    username: String,
    onMessageClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(20.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(CardDark.copy(alpha = 0.8f)),
            contentAlignment = Alignment.Center
        ) {
            // TODO: aquí puedes cargar la imagen real del usuario
            Text(
                text = name.firstOrNull()?.uppercase() ?: "",
                color = TextPrimary,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = name,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "@$username",
                color = TextSecondary,
                fontSize = 12.sp
            )
        }

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(CardDark.copy(alpha = 0.9f))
                .clickable { onMessageClick() },
            contentAlignment = Alignment.Center
        ) {
            // TODO: sustituir por Icon real de mensaje
            Text(text = "💬", color = PrimaryBlue, fontSize = 18.sp)
        }
    }
}

// -------------------------------------------------------------
//  MODELO Y LISTA DE COMENTARIOS
// -------------------------------------------------------------

data class CommentUi(
    val id: Int,
    val authorName: String,
    val text: String,
    val timeLabel: String,
    val imageUri: Uri? = null
)

@Composable
fun CommentsList(
    comments: List<CommentUi>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 320.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        comments.forEach { comment ->
            CommentItem(comment = comment)
        }
    }
}

@Composable
private fun CommentItem(
    comment: CommentUi
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(CardDark.copy(alpha = 0.9f)),
                contentAlignment = Alignment.Center
            ) {
                // TODO: avatar real del usuario
                Text(
                    text = comment.authorName.firstOrNull()?.uppercase() ?: "",
                    color = TextPrimary,
                    fontSize = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(
                modifier = Modifier
                    .background(CardDark, RoundedCornerShape(16.dp))
                    .padding(12.dp)
                    .weight(1f)
            ) {
                Text(
                    text = comment.authorName,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = comment.text,
                    color = TextPrimary,
                    fontSize = 13.sp
                )
                if (comment.imageUri != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(CardDark.copy(alpha = 0.9f))
                    ) {
                        // TODO: cargar imagen desde comment.imageUri
                        // Ejemplo con Coil: AsyncImage(model = comment.imageUri, contentDescription = null)
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = comment.timeLabel,
            color = TextSecondary,
            fontSize = 11.sp,
            modifier = Modifier.padding(start = 40.dp)
        )
    }
}

// -------------------------------------------------------------
//  INPUT BAR DE COMENTARIOS
// -------------------------------------------------------------

@Composable
fun CommentInputBar(
    textState: MutableState<String>,
    onAttachClick: () -> Unit,
    onSendClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardDark, RoundedCornerShape(24.dp))
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "+",
            color = TextSecondary,
            fontSize = 20.sp,
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable { onAttachClick() }
        )

        TextField(
            value = textState.value,
            onValueChange = { textState.value = it },
            modifier = Modifier.weight(1f),
            placeholder = {
                Text(
                    text = "Añade un comentario...",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            },
            maxLines = 4,
            colors = TextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                cursorColor = PrimaryBlue,
                focusedContainerColor = CardDark,
                unfocusedContainerColor = CardDark,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                errorIndicatorColor = Color.Transparent
            )
        )

        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(PrimaryBlue)
                .clickable { onSendClick() },
            contentAlignment = Alignment.Center
        ) {
            // TODO: icono de enviar real
            Text(text = "➤", color = TextPrimary, fontSize = 16.sp)
        }
    }
}*/
