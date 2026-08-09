package com.miguel.statscalculator.presentation.distributions

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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.abs
import kotlin.math.exp

enum class NormalProbabilityType {
    LESS_THAN,    // P(X <= x)
    GREATER_THAN, // P(X >= x)
    BETWEEN       // P(x1 <= X <= x2)
}

data class NormalDistributionParams(
    val mean: Double = 0.0,
    val stdDev: Double = 1.0,
    val x1: Double = 1.96,
    val x2: Double = 2.5,
    val type: NormalProbabilityType = NormalProbabilityType.LESS_THAN,
    val calculatedProbability: Double = 0.9750
)

@Composable
fun NormalDistributionCanvas(
    params: NormalDistributionParams,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.tertiary
    val surfaceColor = MaterialTheme.colorScheme.surface
    val gridLineColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f)
    val axisColor = MaterialTheme.colorScheme.outline
    val labelTextColor = MaterialTheme.colorScheme.onSurfaceVariant
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(16.dp)
    ) {
        // Título Superior Integrado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text(
                text = "Curva Normal N(μ, σ²)",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = onSurfaceColor
            )
            Text(
                text = "μ=${formatVal(params.mean)}  σ=${formatVal(params.stdDev)}",
                style = MaterialTheme.typography.labelMedium,
                color = labelTextColor
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
        ) {
            val width = size.width
            val height = size.height

            val paddingTop = 50f     // Espaço superior para o Badge P(...) não encostar na curva
            val paddingBottom = 65f  // Espaço inferior para organizar SD labels, μ e X sem sobreposição
            val paddingX = 35f

            val baselineY = height - paddingBottom
            val peakY = paddingTop + 10f
            val curveHeight = baselineY - peakY

            val mu = params.mean
            val sigma = if (params.stdDev > 0) params.stdDev else 1.0

            val minX = mu - 3.5 * sigma
            val maxX = mu + 3.5 * sigma
            val rangeX = maxX - minX

            fun toCanvasX(x: Double): Float {
                return (paddingX + (x - minX) / rangeX * (width - 2 * paddingX)).toFloat()
            }

            fun gaussianY(x: Double): Float {
                val z = (x - mu) / sigma
                val normY = exp(-0.5 * z * z).toFloat()
                return baselineY - normY * curveHeight
            }

            // 1. Grade Suave de Fundo (-3σ a +3σ)
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
            val sdList = listOf(-3, -2, -1, 1, 2, 3)

            sdList.forEach { k ->
                val sdXVal = mu + k * sigma
                val cx = toCanvasX(sdXVal)
                val cy = gaussianY(sdXVal)

                // Linha pontilhada
                drawLine(
                    color = gridLineColor,
                    start = Offset(cx, baselineY),
                    end = Offset(cx, cy),
                    pathEffect = dashEffect,
                    strokeWidth = 1.5f
                )

                // Marcador discreto no eixo (Tick)
                drawLine(
                    color = axisColor,
                    start = Offset(cx, baselineY),
                    end = Offset(cx, baselineY + 4f),
                    strokeWidth = 2f
                )

                // Rótulos do desvio padrão (Nível 1 de texto: logo abaixo da baseline)
                val sdText = if (k > 0) "+${k}σ" else "${k}σ"
                val sdMeasured = textMeasurer.measure(
                    text = sdText,
                    style = TextStyle(fontSize = 9.sp, color = labelTextColor.copy(alpha = 0.7f))
                )
                drawText(
                    textLayoutResult = sdMeasured,
                    topLeft = Offset(cx - sdMeasured.size.width / 2f, baselineY + 5f)
                )
            }

            // 2. Eixo X Principal
            drawLine(
                color = axisColor,
                start = Offset(paddingX - 10f, baselineY),
                end = Offset(width - paddingX + 10f, baselineY),
                strokeWidth = 2f
            )

            // 3. Área Sombreada da Probabilidade
            val shadowPath = Path()
            val (shadeStart, shadeEnd) = when (params.type) {
                NormalProbabilityType.LESS_THAN -> minX to params.x1.coerceIn(minX, maxX)
                NormalProbabilityType.GREATER_THAN -> params.x1.coerceIn(minX, maxX) to maxX
                NormalProbabilityType.BETWEEN -> {
                    val s = minOf(params.x1, params.x2).coerceIn(minX, maxX)
                    val e = maxOf(params.x1, params.x2).coerceIn(minX, maxX)
                    s to e
                }
            }

            val startCX = toCanvasX(shadeStart)
            val endCX = toCanvasX(shadeEnd)

            shadowPath.moveTo(startCX, baselineY)
            val steps = 80
            val stepSize = (shadeEnd - shadeStart) / steps
            for (i in 0..steps) {
                val currX = shadeStart + i * stepSize
                shadowPath.lineTo(toCanvasX(currX), gaussianY(currX))
            }
            shadowPath.lineTo(endCX, baselineY)
            shadowPath.close()

            drawPath(
                path = shadowPath,
                color = primaryColor.copy(alpha = 0.22f)
            )

            // 4. Contorno Principal do Sino
            val curvePath = Path()
            val totalSteps = 120
            val totalStepSize = (maxX - minX) / totalSteps
            curvePath.moveTo(toCanvasX(minX), gaussianY(minX))
            for (i in 1..totalSteps) {
                val currX = minX + i * totalStepSize
                curvePath.lineTo(toCanvasX(currX), gaussianY(currX))
            }
            drawPath(
                path = curvePath,
                color = primaryColor,
                style = Stroke(width = 3.5f)
            )

            // 5. Linha Central da Média (μ)
            val meanCX = toCanvasX(mu)
            val meanCY = gaussianY(mu)

            drawLine(
                color = secondaryColor,
                start = Offset(meanCX, baselineY),
                end = Offset(meanCX, meanCY),
                strokeWidth = 2.5f,
                pathEffect = dashEffect
            )

            drawCircle(color = secondaryColor, radius = 5f, center = Offset(meanCX, meanCY))
            drawCircle(color = Color.White, radius = 2f, center = Offset(meanCX, meanCY))

            // Rótulo "μ" (Nível 2 de texto: desceu para baseline + 20f)
            val muText = "μ (${formatVal(mu)})"
            val muMeasured = textMeasurer.measure(
                text = muText,
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = secondaryColor)
            )
            drawText(
                textLayoutResult = muMeasured,
                topLeft = Offset(meanCX - muMeasured.size.width / 2f, baselineY + 20f)
            )

            // 6. Linha(s) de Corte para os limites X (com Badge de Z-Score)
            fun drawCutoff(xVal: Double, label: String, textOffsetY: Float) {
                if (xVal < minX || xVal > maxX) return
                val cx = toCanvasX(xVal)
                val cy = gaussianY(xVal)
                val z = (xVal - mu) / sigma

                drawLine(
                    color = primaryColor,
                    start = Offset(cx, baselineY),
                    end = Offset(cx, cy),
                    strokeWidth = 3f
                )
                drawCircle(color = primaryColor, radius = 4.5f, center = Offset(cx, cy))

                val infoText = "$label=${formatVal(xVal)} (Z=${formatVal(z)})"
                val infoMeasured = textMeasurer.measure(
                    text = infoText,
                    style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = primaryColor)
                )

                val textX = (cx - infoMeasured.size.width / 2f).coerceIn(10f, width - infoMeasured.size.width - 10f)

                drawText(
                    textLayoutResult = infoMeasured,
                    topLeft = Offset(textX, baselineY + textOffsetY)
                )
            }

            if (params.type == NormalProbabilityType.BETWEEN) {
                drawCutoff(params.x1, "x1", 38f)
                drawCutoff(params.x2, "x2", 38f)
            } else {
                // Nível 3 de texto: desceu para baseline + 38f
                drawCutoff(params.x1, "x", 38f)
            }

            // 7. Badge de Resultado Flutuante (Canto Superior)
            val probPercent = params.calculatedProbability * 100
            val probStr = when (params.type) {
                NormalProbabilityType.LESS_THAN -> "P(X ≤ ${formatVal(params.x1)}) = ${formatVal(probPercent)}%"
                NormalProbabilityType.GREATER_THAN -> "P(X ≥ ${formatVal(params.x1)}) = ${formatVal(probPercent)}%"
                NormalProbabilityType.BETWEEN -> "P(${formatVal(params.x1)} ≤ X ≤ ${formatVal(params.x2)}) = ${formatVal(probPercent)}%"
            }

            val badgeMeasured = textMeasurer.measure(
                text = probStr,
                style = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold, color = primaryColor)
            )

            val badgePaddingHorizontal = 12f
            val badgePaddingVertical = 5f
            val badgeWidth = badgeMeasured.size.width + (badgePaddingHorizontal * 2)
            val badgeHeight = badgeMeasured.size.height + (badgePaddingVertical * 2)
            val badgeX = (width - badgeWidth) / 2f
            val badgeY = 2f // Posicionado no topo do canvas, sem encostar na curva

            // Fundo do Badge
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.12f),
                topLeft = Offset(badgeX, badgeY),
                size = Size(badgeWidth, badgeHeight),
                cornerRadius = CornerRadius(8f, 8f)
            )
            // Borda do Badge
            drawRoundRect(
                color = primaryColor.copy(alpha = 0.4f),
                topLeft = Offset(badgeX, badgeY),
                size = Size(badgeWidth, badgeHeight),
                cornerRadius = CornerRadius(8f, 8f),
                style = Stroke(width = 1f)
            )
            // Texto do Badge
            drawText(
                textLayoutResult = badgeMeasured,
                topLeft = Offset(badgeX + badgePaddingHorizontal, badgeY + badgePaddingVertical)
            )
        }
    }
}

private fun formatVal(v: Double): String {
    return String.format(Locale.US, "%.2f", v)
}