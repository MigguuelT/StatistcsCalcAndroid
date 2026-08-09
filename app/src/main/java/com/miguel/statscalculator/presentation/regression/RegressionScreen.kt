package com.miguel.statscalculator.presentation.regression

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.miguel.statscalculator.core.math.LinearRegressionResult
import com.miguel.statscalculator.ui.components.InputActionBar
import com.miguel.statscalculator.ui.components.KpiHeroCard
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegressionScreen(
    onNavigateBack: () -> Unit,
    viewModel: RegressionViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val formattedReport = uiState.result?.let { generateRegressionReport(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Regressão & Correlação",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 20.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- BARRAS DE AÇÃO SISTÊMICA ---
            InputActionBar(
                onTextPasted = { viewModel.onInputXChanged(it) },
                onFileLoaded = { viewModel.onInputXChanged(it) },
                reportToShare = formattedReport,
                reportTitle = "Relatório de Regressão Linear"
            )

            // Inputs Lado a Lado (X e Y)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.inputTextX,
                    onValueChange = { viewModel.onInputXChanged(it) },
                    label = { Text("Variável X (Indep.)") },
                    placeholder = { Text("1, 2, 3, 4") },
                    modifier = Modifier.weight(1f).heightIn(min = 90.dp),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = uiState.inputTextY,
                    onValueChange = { viewModel.onInputYChanged(it) },
                    label = { Text("Variável Y (Dep.)") },
                    placeholder = { Text("2, 4, 5, 8") },
                    modifier = Modifier.weight(1f).heightIn(min = 90.dp),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Ações Rápidas
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { viewModel.loadSampleData() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Exemplo")
                }

                OutlinedButton(
                    onClick = { viewModel.clear() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Limpar")
                }
            }

            // Botão Calcular
            Button(
                onClick = { viewModel.calculate() },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(imageVector = Icons.Default.ShowChart, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ajustar Modelo Linear", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
            }

            uiState.errorMessage?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            // Exibição de Resultados e Gráfico
            uiState.result?.let { result ->
                Text(
                    text = "MÉTRICAS DA REGRESSÃO",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiHeroCard(title = "R² (Determinação)", value = formatNum(result.rSquared), subtitle = "Ajuste do modelo", modifier = Modifier.weight(1f))
                    KpiHeroCard(title = "Pearson (r)", value = formatNum(result.pearsonR), subtitle = "Correlação", modifier = Modifier.weight(1f))
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiHeroCard(title = "Inclin. (b)", value = formatNum(result.slope), subtitle = "Coef. Angular", modifier = Modifier.weight(1f))
                    KpiHeroCard(title = "Intercepto (a)", value = formatNum(result.intercept), subtitle = "Constante", modifier = Modifier.weight(1f))
                }

                ScatterPlotCanvas(points = uiState.points, result = result)

                PredictionCard(
                    predictX = uiState.predictXInput,
                    predictedY = uiState.predictedY,
                    onPredictXChange = { viewModel.onPredictXChanged(it) }
                )

                RegressionDetailsCard(result)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PredictionCard(predictX: String, predictedY: Double?, onPredictXChange: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)).padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "Projeção de Valores (Predição)", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = predictX,
                    onValueChange = onPredictXChange,
                    label = { Text("Valor X") },
                    placeholder = { Text("Ex: 12.5") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                )
                Box(
                    modifier = Modifier.weight(1f).clip(RoundedCornerShape(10.dp)).background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)).padding(14.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "Y ESTIMADO (Ŷ)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Text(text = if (predictedY != null) formatNum(predictedY) else "--", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}

@Composable
private fun RegressionDetailsCard(result: LinearRegressionResult) {
    Box(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(16.dp)).background(MaterialTheme.colorScheme.surface).border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp)).padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(text = "Inspecionar Parâmetros de Ajuste", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = MaterialTheme.colorScheme.onSurface)
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            MetricRow("Equação do Modelo", result.equationString)
            MetricRow("R² Ajustado", formatNum(result.adjustedRSquared))
            MetricRow("Erro Padrão da Estimativa (S_e)", formatNum(result.stdErrorEstimate))
            MetricRow("Erro Padrão do Coeficiente", formatNum(result.stdErrorSlope))
            MetricRow("Estatística t", formatNum(result.tStatistic))
            MetricRow("Valor-p (Significância)", formatNum(result.pValue))
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun formatNum(value: Double): String {
    return String.format(Locale.US, "%.4f", value)
}

private fun generateRegressionReport(r: LinearRegressionResult): String {
    return """
        📈 RELATÓRIO DE REGRESSÃO LINEAR SIMPLES
        ====================================
        Equação Ajustada: ${r.equationString}
        Pares de Dados (n): ${r.n}
        
        MÉTRICAS DE AJUSTE:
        - Coeficiente de Determinação (R²): ${formatNum(r.rSquared)}
        - R² Ajustado: ${formatNum(r.adjustedRSquared)}
        - Correlação de Pearson (r): ${formatNum(r.pearsonR)}
        
        PARÂMETROS DO MODELO:
        - Coeficiente Angular (b): ${formatNum(r.slope)}
        - Intercepto (a): ${formatNum(r.intercept)}
        - Erro Padrão da Estimativa: ${formatNum(r.stdErrorEstimate)}
        - Estatística t: ${formatNum(r.tStatistic)}
        - Valor-p: ${formatNum(r.pValue)}
        ====================================
        Gerado por StatsCalculator
    """.trimIndent()
}