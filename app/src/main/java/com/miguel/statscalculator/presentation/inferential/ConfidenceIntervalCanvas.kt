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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.statscalculator.core.math.ConfidenceIntervalResult
import java.util.Locale

@Composable
fun ConfidenceIntervalCanvas(
    result: ConfidenceIntervalResult,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val gridColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val textMeasurer = rememberTextMeasurer()

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        Text(
            text = "Representação Visual do IC (${formatNum(result.confidenceLevel * 100)}%)",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = onSurfaceColor
        )
        Text(
            text = "Margem de Erro (E): ± ${formatNum(result.marginOfError)}",
            style = MaterialTheme.typography.bodySmall,
            color = primaryColor
        )

        Spacer(modifier = Modifier.height(12.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
        ) {
            val paddingLeftRight = 24f
            val paddingTop = 42f
            val paddingBottom = 48f

            val width = size.width
            val height = size.height

            // Declaração de variáveis base de layout
            val baselineY = height - paddingBottom
            val peakY = paddingTop

            // Declaração explícita extraída do resultado
            val mean = result.mean
            val confidenceLevel = result.confidenceLevel
            val lower = result.lowerBound
            val upper = result.upperBound

            val canvasMeanX = width / 2f
            val intervalHalfWidth = width * 0.28f
            val canvasLowerX = canvasMeanX - intervalHalfWidth
            val canvasUpperX = canvasMeanX + intervalHalfWidth

            // 1. DESENHO DA CURVA GAUSSIANA E PREENCHIMENTO
            val totalSteps = 100
            val curvePath = Path()
            val fillPath = Path()

            curvePath.moveTo(paddingLeftRight, baselineY)
            fillPath.moveTo(canvasLowerX, baselineY)

            for (i in 0..totalSteps) {
                val t = i.toFloat() / totalSteps
                val x = paddingLeftRight + t * (width - 2 * paddingLeftRight)
                val z = (x - canvasMeanX) / (intervalHalfWidth / 1.96f)
                val gaussianY = baselineY - (baselineY - peakY) * kotlin.math.exp(-0.5 * z * z).toFloat()

                curvePath.lineTo(x, gaussianY)

                if (x in canvasLowerX..canvasUpperX) {
                    fillPath.lineTo(x, gaussianY)
                }
            }

            fillPath.lineTo(canvasUpperX, baselineY)
            fillPath.close()

            // Desenhar área sombreada do IC
            drawPath(
                path = fillPath,
                color = primaryColor.copy(alpha = 0.18f),
                style = Fill
            )

            // Desenhar contorno da curva Gaussiana
            drawPath(
                path = curvePath,
                color = primaryColor,
                style = Stroke(width = 3.5f)
            )

            // 2. EIXO HORIZONTAL BASE DE REFERÊNCIA
            drawLine(
                color = gridColor,
                start = Offset(10f, baselineY),
                end = Offset(width - 10f, baselineY),
                strokeWidth = 2f
            )

            // 3. PONTO CENTRAL (MÉDIA)
            drawCircle(
                color = secondaryColor,
                radius = 7f,
                center = Offset(canvasMeanX, peakY)
            )

            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
            drawLine(
                color = secondaryColor.copy(alpha = 0.5f),
                start = Offset(canvasMeanX, peakY),
                end = Offset(canvasMeanX, baselineY),
                pathEffect = dashEffect,
                strokeWidth = 2f
            )

            // 4. LIMITADORES VERTICAIS DAS BORDAS (CAPS)
            val capHeight = 16f
            val lowerYAtCurve = baselineY - (baselineY - peakY) * kotlin.math.exp(-0.5 * 1.96 * 1.96).toFloat()
            drawLine(
                color = primaryColor,
                start = Offset(canvasLowerX, lowerYAtCurve - capHeight / 2f),
                end = Offset(canvasLowerX, baselineY + capHeight / 2f),
                strokeWidth = 3f
            )
            drawLine(
                color = primaryColor,
                start = Offset(canvasUpperX, lowerYAtCurve - capHeight / 2f),
                end = Offset(canvasUpperX, baselineY + capHeight / 2f),
                strokeWidth = 3f
            )

            // 5. RÓTULOS E TEXTOS NO CANVAS

            // A. Rótulo da Média Amostral (x̄) no topo do pico
            val meanStr = "x̄ = ${formatNum(mean)}"
            val meanLayout = textMeasurer.measure(
                text = meanStr,
                style = TextStyle(color = secondaryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            )
            drawText(
                textLayoutResult = meanLayout,
                topLeft = Offset(
                    (canvasMeanX - meanLayout.size.width / 2f).coerceIn(10f, width - meanLayout.size.width - 10f),
                    peakY - 48f
                )
            )

            // B. Rótulo da Porcentagem de Confiança na área sombreada central
            val confStr = "${formatNum(confidenceLevel * 100)}% de Confiança"
            val confLayout = textMeasurer.measure(
                text = confStr,
                style = TextStyle(color = primaryColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            )
            drawText(
                textLayoutResult = confLayout,
                topLeft = Offset(
                    canvasMeanX - confLayout.size.width / 2f,
                    peakY + (baselineY - peakY) * 0.60f
                )
            )

            // C. Limitadores Verticais e Rótulos Inferiores
            val lowerStr = "L: ${formatNum(lower)}"
            val lowerLayout = textMeasurer.measure(
                text = lowerStr,
                style = TextStyle(color = textColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            )
            drawText(
                textLayoutResult = lowerLayout,
                topLeft = Offset(
                    (canvasLowerX - lowerLayout.size.width / 2f).coerceAtLeast(10f),
                    baselineY + 12f
                )
            )

            val upperStr = "U: ${formatNum(upper)}"
            val upperLayout = textMeasurer.measure(
                text = upperStr,
                style = TextStyle(color = textColor, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            )
            drawText(
                textLayoutResult = upperLayout,
                topLeft = Offset(
                    (canvasUpperX - upperLayout.size.width / 2f).coerceAtMost(width - upperLayout.size.width - 10f),
                    baselineY + 12f
                )
            )
        }
    }
}

private fun formatNum(value: Double): String {
    return String.format(Locale.US, "%.2f", value)
}