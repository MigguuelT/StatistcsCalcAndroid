package com.miguel.statscalculator.presentation.regression

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.miguel.statscalculator.core.math.LinearRegressionResult
import com.miguel.statscalculator.core.math.Point2D

@Composable
fun ScatterPlotCanvas(
    points: List<Point2D>,
    result: LinearRegressionResult,
    modifier: Modifier = Modifier
) {
    val pointColor = MaterialTheme.colorScheme.primary
    val lineColor = MaterialTheme.colorScheme.secondary
    val gridColor = MaterialTheme.colorScheme.surfaceVariant
    val textColor = MaterialTheme.colorScheme.onSurfaceVariant

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
            text = "Gráfico de Dispersão & Reta de Ajuste",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = result.equationString,
            style = MaterialTheme.typography.bodySmall,
            color = lineColor
        )

        Spacer(modifier = Modifier.height(16.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            if (points.isEmpty()) return@Canvas

            val padding = 40f
            val width = size.width
            val height = size.height

            val minX = points.minOf { it.x }
            val maxX = points.maxOf { it.x }
            val minY = points.minOf { it.y }
            val maxY = points.maxOf { it.y }

            val rangeX = if (maxX - minX == 0.0) 1.0 else maxX - minX
            val rangeY = if (maxY - minY == 0.0) 1.0 else maxY - minY

            // Margem extra para os pontos não colarem na borda
            val plotMinX = minX - rangeX * 0.1
            val plotMaxX = maxX + rangeX * 0.1
            val plotMinY = minY - rangeY * 0.1
            val plotMaxY = maxY + rangeY * 0.1

            val plotRangeX = plotMaxX - plotMinX
            val plotRangeY = plotMaxY - plotMinY

            fun toCanvasX(x: Double): Float {
                return (padding + (x - plotMinX) / plotRangeX * (width - 2 * padding)).toFloat()
            }

            fun toCanvasY(y: Double): Float {
                return (height - padding - (y - plotMinY) / plotRangeY * (height - 2 * padding)).toFloat()
            }

            // 1. Desenhar Linhas de Grade
            val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
            val gridSteps = 4
            for (i in 0..gridSteps) {
                val fraction = i.toFloat() / gridSteps
                val gx = padding + fraction * (width - 2 * padding)
                val gy = padding + fraction * (height - 2 * padding)

                // Grade Vertical
                drawLine(
                    color = gridColor,
                    start = Offset(gx, padding),
                    end = Offset(gx, height - padding),
                    pathEffect = dashEffect,
                    strokeWidth = 1f
                )
                // Grade Horizontal
                drawLine(
                    color = gridColor,
                    start = Offset(padding, gy),
                    end = Offset(width - padding, gy),
                    pathEffect = dashEffect,
                    strokeWidth = 1f
                )
            }

            // 2. Desenhar Eixos X e Y
            drawLine(
                color = textColor,
                start = Offset(padding, height - padding),
                end = Offset(width - padding, height - padding),
                strokeWidth = 2f
            )
            drawLine(
                color = textColor,
                start = Offset(padding, padding),
                end = Offset(padding, height - padding),
                strokeWidth = 2f
            )

            // 3. Desenhar a Linha de Tendência (Y = a + bX)
            val lineStartX = plotMinX
            val lineEndX = plotMaxX
            val lineStartY = result.intercept + result.slope * lineStartX
            val lineEndY = result.intercept + result.slope * lineEndX

            drawLine(
                color = lineColor,
                start = Offset(toCanvasX(lineStartX), toCanvasY(lineStartY)),
                end = Offset(toCanvasX(lineEndX), toCanvasY(lineEndY)),
                strokeWidth = 4f
            )

            // 4. Desenhar os Pontos de Dispersão
            points.forEach { point ->
                val cx = toCanvasX(point.x)
                val cy = toCanvasY(point.y)

                drawCircle(
                    color = pointColor,
                    radius = 9f,
                    center = Offset(cx, cy)
                )
                drawCircle(
                    color = Color.White,
                    radius = 4f,
                    center = Offset(cx, cy)
                )
            }
        }
    }
}