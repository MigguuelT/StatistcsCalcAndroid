package com.miguel.statscalculator.presentation.inferential

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.statscalculator.util.ConfidenceIntervalResult
import java.util.Locale

@Composable
fun ConfidenceIntervalCanvas(
    result: ConfidenceIntervalResult,
    pointEstimate: Double,
    confidenceLevel: String,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val axisColor = MaterialTheme.colorScheme.outline
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val labelTextColor = MaterialTheme.colorScheme.onSurfaceVariant

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Visualização do Intervalo de Confiança ($confidenceLevel%)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = onSurfaceColor
        )

        Spacer(modifier = Modifier.height(12.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
        ) {
            val width = size.width
            val height = size.height

            val paddingX = 60f
            val centerY = height / 2f - 5f

            val totalMargin = result.marginOfError * 1.6
            val minX = pointEstimate - totalMargin
            val maxX = pointEstimate + totalMargin
            val rangeX = if (maxX - minX > 0) maxX - minX else 1.0

            fun toCanvasX(v: Double): Float {
                return (paddingX + (v - minX) / rangeX * (width - 2 * paddingX)).toFloat()
            }

            val lowerCX = toCanvasX(result.lowerLimit)
            val upperCX = toCanvasX(result.upperLimit)
            val centerCX = toCanvasX(pointEstimate)

            // Eixo de Referência
            drawLine(
                color = axisColor.copy(alpha = 0.4f),
                start = Offset(paddingX - 20f, centerY),
                end = Offset(width - paddingX + 20f, centerY),
                strokeWidth = 2f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            )

            // Segmento de Erro
            drawLine(
                color = primaryColor,
                start = Offset(lowerCX, centerY),
                end = Offset(upperCX, centerY),
                strokeWidth = 6f
            )

            // Marcadores dos Limites
            val tickHalfHeight = 16f
            drawLine(
                color = primaryColor,
                start = Offset(lowerCX, centerY - tickHalfHeight),
                end = Offset(lowerCX, centerY + tickHalfHeight),
                strokeWidth = 4f
            )
            drawLine(
                color = primaryColor,
                start = Offset(upperCX, centerY - tickHalfHeight),
                end = Offset(upperCX, centerY + tickHalfHeight),
                strokeWidth = 4f
            )

            // Ponto Central
            drawCircle(color = secondaryColor, radius = 8f, center = Offset(centerCX, centerY))
            drawCircle(color = Color.White, radius = 3f, center = Offset(centerCX, centerY))

            // Rótulos Numéricos
            val lowerText = formatVal(result.lowerLimit)
            val lowerMeasured = textMeasurer.measure(
                text = lowerText,
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
            )
            drawText(
                textLayoutResult = lowerMeasured,
                topLeft = Offset(lowerCX - lowerMeasured.size.width / 2f, centerY + tickHalfHeight + 6f)
            )

            val upperText = formatVal(result.upperLimit)
            val upperMeasured = textMeasurer.measure(
                text = upperText,
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
            )
            drawText(
                textLayoutResult = upperMeasured,
                topLeft = Offset(upperCX - upperMeasured.size.width / 2f, centerY + tickHalfHeight + 6f)
            )

            val centerText = "x̂ = ${formatVal(pointEstimate)}"
            val centerMeasured = textMeasurer.measure(
                text = centerText,
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = secondaryColor)
            )
            drawText(
                textLayoutResult = centerMeasured,
                topLeft = Offset(centerCX - centerMeasured.size.width / 2f, centerY - tickHalfHeight - 18f)
            )

            val errText = "E = ±${formatVal(result.marginOfError)}"
            val errMeasured = textMeasurer.measure(
                text = errText,
                style = TextStyle(fontSize = 10.sp, color = labelTextColor)
            )
            drawText(
                textLayoutResult = errMeasured,
                topLeft = Offset(centerCX - errMeasured.size.width / 2f, centerY + tickHalfHeight + 22f)
            )
        }
    }
}

private fun formatVal(v: Double): String {
    return String.format(Locale.US, "%.2f", v)
}