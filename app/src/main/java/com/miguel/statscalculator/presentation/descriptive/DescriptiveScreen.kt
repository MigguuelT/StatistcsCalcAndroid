package com.miguel.statscalculator.presentation.descriptive

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
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.miguel.statscalculator.core.math.DescriptiveResult
import com.miguel.statscalculator.core.math.ModeType
import com.miguel.statscalculator.ui.components.InputActionBar
import com.miguel.statscalculator.ui.components.KpiHeroCard
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DescriptiveScreen(
    onNavigateBack: () -> Unit,
    viewModel: DescriptiveViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Gera o texto do relatório formatado quando há resultado
    val formattedReport = uiState.result?.let { generateDescriptiveReport(it) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Estatística Descritiva",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
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
            // --- BARRAS DE AÇÃO SISTÊMICA (COLAR / ARQUIVO CSV / EXPORTAR) ---
            InputActionBar(
                onTextPasted = { viewModel.onInputTextChanged(it) },
                onFileLoaded = { viewModel.onInputTextChanged(it) },
                reportToShare = formattedReport,
                reportTitle = "Relatório de Estatística Descritiva"
            )

            // --- ENTRADA DE DADOS ---
            OutlinedTextField(
                value = uiState.inputText,
                onValueChange = { viewModel.onInputTextChanged(it) },
                label = { Text("Conjunto de Dados (ex: 10, 20, 30.5, 40)") },
                placeholder = { Text("Separe por vírgula, espaço ou quebra de linha") },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // --- BOTÕES DE AÇÃO RÁPIDA ---
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

            // --- BOTÃO CALCULAR ---
            Button(
                onClick = { viewModel.calculate() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(imageVector = Icons.Default.Functions, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Calcular Medidas",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }

            uiState.errorMessage?.let { error ->
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // --- RESULTADOS ---
            uiState.result?.let { result ->
                Text(
                    text = "RESULTADOS DA ANÁLISE",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.2.sp
                    ),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 8.dp)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiHeroCard(
                        title = "Média",
                        value = formatNum(result.mean),
                        subtitle = "n = ${result.n}",
                        modifier = Modifier.weight(1f)
                    )
                    KpiHeroCard(
                        title = "Mediana",
                        value = formatNum(result.median),
                        subtitle = "Posição Central",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiHeroCard(
                        title = "Desvio Padrão",
                        value = formatNum(result.sampleStdDev),
                        subtitle = "Amostral (s)",
                        modifier = Modifier.weight(1f)
                    )
                    KpiHeroCard(
                        title = "Variância",
                        value = formatNum(result.sampleVariance),
                        subtitle = "Amostral (s²)",
                        modifier = Modifier.weight(1f)
                    )
                }

                DetailedMetricsCard(result)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailedMetricsCard(result: DescriptiveResult) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Detalhamento Completo",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            val modeText = when (result.modeType) {
                ModeType.AMODAL -> "Amodal (Nenhuma)"
                else -> "${result.modes.joinToString { formatNum(it) }} (${result.modeType.name})"
            }

            MetricRow("Moda", modeText)
            MetricRow("Mínimo / Máximo", "${formatNum(result.min)} / ${formatNum(result.max)}")
            MetricRow("Amplitude Total", formatNum(result.range))
            MetricRow("Desvio Médio Absoluto (DMA)", formatNum(result.meanAbsoluteDeviation))
            MetricRow("Coef. de Variação (CV)", "${formatNum(result.coefficientOfVariation)}%")
            MetricRow("Desvio Padrão Pop. (σ)", formatNum(result.populationStdDev))
            MetricRow("Variância Pop. (σ²)", formatNum(result.populationVariance))

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            MetricRow("Quartil 1 (Q1)", formatNum(result.q1))
            MetricRow("Quartil 3 (Q3)", formatNum(result.q3))
            MetricRow("Amplitude Interquartil (IQR)", formatNum(result.iqr))

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            MetricRow("Assimetria (Skewness)", formatNum(result.skewness))
            MetricRow("Curtose (Kurtosis)", formatNum(result.kurtosis))
        }
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.onSurface)
    }
}

private fun formatNum(value: Double): String {
    return String.format(Locale.US, "%.4f", value)
}

private fun generateDescriptiveReport(r: DescriptiveResult): String {
    return """
        📊 RELATÓRIO DE ESTATÍSTICA DESCRITIVA
        ====================================
        Tamanho da Amostra (n): ${r.n}
        Média: ${formatNum(r.mean)}
        Mediana: ${formatNum(r.median)}
        Moda: ${if (r.modes.isEmpty()) "Amodal" else r.modes.joinToString()}
        
        VARIABILIDADE:
        - Desvio Padrão (Amostral): ${formatNum(r.sampleStdDev)}
        - Variância (Amostral): ${formatNum(r.sampleVariance)}
        - Amplitude Total: ${formatNum(r.range)}
        - Mínimo / Máximo: ${formatNum(r.min)} / ${formatNum(r.max)}
        - Coeficiente de Variação: ${formatNum(r.coefficientOfVariation)}%
        
        SEPARATRIZES:
        - Q1 (25%): ${formatNum(r.q1)}
        - Q3 (75%): ${formatNum(r.q3)}
        - IQR: ${formatNum(r.iqr)}
        
        FORMA:
        - Assimetria (Skewness): ${formatNum(r.skewness)}
        - Curtose (Kurtosis): ${formatNum(r.kurtosis)}
        ====================================
        Gerado por StatsCalculator
    """.trimIndent()
}