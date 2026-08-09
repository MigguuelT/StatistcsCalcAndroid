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
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.statscalculator.util.AlternativeHypothesis
import com.miguel.statscalculator.util.HypothesisTestResult
import java.util.Locale
import kotlin.math.abs
import kotlin.math.exp

@Composable
fun HypothesisTestCanvas(
    result: HypothesisTestResult,
    alternative: AlternativeHypothesis,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()

    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val surfaceColor = MaterialTheme.colorScheme.surface
    val axisColor = MaterialTheme.colorScheme.outline
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(surfaceColor)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "Distribuição Z sob H₀ e Região Crítica (α = ${result.alpha})",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = onSurfaceColor
        )

        Spacer(modifier = Modifier.height(12.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            val width = size.width
            val height = size.height

            val paddingTop = 25f
            val paddingBottom = 45f
            val paddingX = 35f

            val baselineY = height - paddingBottom
            val peakY = paddingTop
            val curveHeight = baselineY - peakY

            val maxZScale = maxOf(3.8, abs(result.testStatistic) + 0.5)
            val minX = -maxZScale
            val maxX = maxZScale
            val rangeX = maxX - minX

            fun toCanvasX(z: Double): Float {
                return (paddingX + (z - minX) / rangeX * (width - 2 * paddingX)).toFloat()
            }

            fun gaussianY(z: Double): Float {
                val normY = exp(-0.5 * z * z).toFloat()
                return baselineY - normY * curveHeight
            }

            // 1. Eixo X
            drawLine(
                color = axisColor,
                start = Offset(paddingX - 10f, baselineY),
                end = Offset(width - paddingX + 10f, baselineY),
                strokeWidth = 2f
            )

            // 2. Região Crítica de Rejeição H0
            fun drawCriticalShade(startZ: Double, endZ: Double) {
                val path = Path()
                val sCX = toCanvasX(startZ)
                val eCX = toCanvasX(endZ)
                path.moveTo(sCX, baselineY)

                val steps = 40
                val stepSize = (endZ - startZ) / steps
                for (i in 0..steps) {
                    val currZ = startZ + i * stepSize
                    path.lineTo(toCanvasX(currZ), gaussianY(currZ))
                }
                path.lineTo(eCX, baselineY)
                path.close()

                drawPath(path = path, color = errorColor.copy(alpha = 0.3f))
            }

            val crit = abs(result.criticalValue)
            when (alternative) {
                AlternativeHypothesis.LESS -> drawCriticalShade(minX, -crit)
                AlternativeHypothesis.GREATER -> drawCriticalShade(crit, maxX)
                AlternativeHypothesis.TWO_SIDED -> {
                    drawCriticalShade(minX, -crit)
                    drawCriticalShade(crit, maxX)
                }
            }

            // 3. Sino da Normal
            val curvePath = Path()
            val steps = 100
            val stepSize = (maxX - minX) / steps
            curvePath.moveTo(toCanvasX(minX), gaussianY(minX))
            for (i in 1..steps) {
                val currZ = minX + i * stepSize
                curvePath.lineTo(toCanvasX(currZ), gaussianY(currZ))
            }
            drawPath(path = curvePath, color = primaryColor, style = Stroke(width = 3f))

            // 4. Linhas dos Valores Críticos
            fun drawCriticalLine(zVal: Double) {
                val cx = toCanvasX(zVal)
                val cy = gaussianY(zVal)
                drawLine(
                    color = errorColor,
                    start = Offset(cx, baselineY),
                    end = Offset(cx, cy),
                    strokeWidth = 2.5f,
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(4f, 4f), 0f)
                )

                val txt = "Z_crit=${formatVal(zVal)}"
                val meas = textMeasurer.measure(
                    text = txt,
                    style = TextStyle(fontSize = 9.sp, fontWeight = FontWeight.Bold, color = errorColor)
                )
                drawText(textLayoutResult = meas, topLeft = Offset(cx - meas.size.width / 2f, baselineY + 5f))
            }

            if (alternative == AlternativeHypothesis.TWO_SIDED) {
                drawCriticalLine(-crit)
                drawCriticalLine(crit)
            } else if (alternative == AlternativeHypothesis.LESS) {
                drawCriticalLine(-crit)
            } else {
                drawCriticalLine(crit)
            }

            // 5. Linha do Z Calculado
            val zCalc = result.testStatistic
            val zCalcCX = toCanvasX(zCalc)
            val zCalcCY = gaussianY(zCalc)

            val zCalcColor = if (result.rejectNull) errorColor else primaryColor

            drawLine(
                color = zCalcColor,
                start = Offset(zCalcCX, baselineY),
                end = Offset(zCalcCX, zCalcCY),
                strokeWidth = 3f
            )
            drawCircle(color = zCalcColor, radius = 6f, center = Offset(zCalcCX, zCalcCY))

            val calcTxt = "Z_calc = ${formatVal(zCalc)}"
            val calcMeas = textMeasurer.measure(
                text = calcTxt,
                style = TextStyle(fontSize = 10.sp, fontWeight = FontWeight.Bold, color = zCalcColor)
            )
            val textX = (zCalcCX - calcMeas.size.width / 2f).coerceIn(10f, width - calcMeas.size.width - 10f)
            drawText(textLayoutResult = calcMeas, topLeft = Offset(textX, baselineY + 20f))
        }
    }
}

private fun formatVal(v: Double): String {
    return String.format(Locale.US, "%.2f", v)
}