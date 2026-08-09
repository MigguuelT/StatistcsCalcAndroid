package com.miguel.statscalculator.presentation.inferential

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

import com.miguel.statscalculator.ui.components.KpiHeroCard
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InferentialScreen(
    onNavigateBack: () -> Unit,
    viewModel: InferentialViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inferência & Hipóteses",
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
            // TABS DE NAVEGAÇÃO
            TabRow(
                selectedTabIndex = uiState.selectedTab.ordinal,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = uiState.selectedTab == InferentialTab.CONFIDENCE_INTERVAL,
                    onClick = { viewModel.selectTab(InferentialTab.CONFIDENCE_INTERVAL) },
                    text = { Text("IC Média", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = uiState.selectedTab == InferentialTab.HYPOTHESIS_TEST,
                    onClick = { viewModel.selectTab(InferentialTab.HYPOTHESIS_TEST) },
                    text = { Text("Teste t / Z", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = uiState.selectedTab == InferentialTab.ANOVA,
                    onClick = { viewModel.selectTab(InferentialTab.ANOVA) },
                    text = { Text("ANOVA", fontWeight = FontWeight.Bold) }
                )
            }

            uiState.errorMessage?.let { error ->
                Text(text = error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            when (uiState.selectedTab) {
                InferentialTab.CONFIDENCE_INTERVAL -> IcSection(uiState, viewModel)
                InferentialTab.HYPOTHESIS_TEST -> TestSection(uiState, viewModel)
                InferentialTab.ANOVA -> AnovaSection(uiState, viewModel)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// 1. SEÇÃO DE INTERVALO DE CONFIANÇA
@Composable
private fun IcSection(uiState: InferentialUiState, viewModel: InferentialViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = uiState.icDataText,
            onValueChange = { viewModel.onIcDataChanged(it) },
            label = { Text("Dados da Amostra") },
            placeholder = { Text("100, 102, 98, 105") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = uiState.icConfidenceLevelText,
            onValueChange = { viewModel.onIcLevelChanged(it) },
            label = { Text("Nível de Confiança (1 - α)") },
            placeholder = { Text("Ex: 0.95 para 95%") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { viewModel.loadIcExample() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Exemplo")
            }

            Button(
                onClick = { viewModel.calculateIC() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Calcular IC", fontWeight = FontWeight.Bold)
            }
        }

        uiState.icResult?.let { res ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiHeroCard(
                    title = "Limite Inferior",
                    value = formatNum(res.lowerBound),
                    subtitle = "IC ${formatNum(res.confidenceLevel * 100)}%",
                    modifier = Modifier.weight(1f)
                )
                KpiHeroCard(
                    title = "Limite Superior",
                    value = formatNum(res.upperBound),
                    subtitle = "IC ${formatNum(res.confidenceLevel * 100)}%",
                    modifier = Modifier.weight(1f)
                )
            }

            // --- GRÁFICO DO INTERVALO DE CONFIANÇA ---
            ConfidenceIntervalCanvas(result = res)

            DetailCard("Métricas do Intervalo de Confiança") {
                MetricRow("Média Amostral (x̄)", formatNum(res.mean))
                MetricRow("Margem de Erro (E)", "± ${formatNum(res.marginOfError)}")
                MetricRow("Valor Crítico (${if (res.isTDistribution) "t" else "z"})", formatNum(res.criticalValue))
                MetricRow("Desvio Padrão (s)", formatNum(res.stdDev))
            }

            DetailCard("Métricas do Intervalo de Confiança") {
                MetricRow("Média Amostral (x̄)", formatNum(res.mean))
                MetricRow("Margem de Erro (E)", "± ${formatNum(res.marginOfError)}")
                MetricRow("Valor Crítico (${if (res.isTDistribution) "t" else "z"})", formatNum(res.criticalValue))
                MetricRow("Desvio Padrão (s)", formatNum(res.stdDev))
            }
        }
    }
}

// 2. SEÇÃO DE TESTE DE HIPÓTESES
@Composable
private fun TestSection(uiState: InferentialUiState, viewModel: InferentialViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = uiState.testDataText,
            onValueChange = { viewModel.onTestDataChanged(it) },
            label = { Text("Dados da Amostra") },
            placeholder = { Text("Ex: 14.2, 15.1, 13.9") },
            modifier = Modifier.fillMaxWidth().heightIn(min = 90.dp),
            shape = RoundedCornerShape(12.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = uiState.hypoMeanText,
                onValueChange = { viewModel.onHypoMeanChanged(it) },
                label = { Text("Média Nula (μ₀)") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = uiState.alphaText,
                onValueChange = { viewModel.onAlphaChanged(it) },
                label = { Text("Nível de Sig. (α)") },
                singleLine = true,
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { viewModel.loadTestExample() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Exemplo")
            }

            Button(
                onClick = { viewModel.calculateTest() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Executar Teste", fontWeight = FontWeight.Bold)
            }
        }

        uiState.testResult?.let { res ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiHeroCard(
                    title = if (res.isTTest) "Estatística t" else "Estatística Z",
                    value = formatNum(res.testStatistic),
                    modifier = Modifier.weight(1f)
                )
                KpiHeroCard(
                    title = "Valor-p",
                    value = formatNum(res.pValue),
                    subtitle = if (res.isSignificant) "Rejeita H0" else "Não Rejeita H0",
                    modifier = Modifier.weight(1f)
                )
            }

            DetailCard("Decisão do Teste de Hipóteses") {
                MetricRow("Média Amostral Observada", formatNum(res.sampleMean))
                MetricRow("Média Hipotetizada (μ₀)", formatNum(res.hypoMean))
                MetricRow("Decisão Estatística", if (res.isSignificant) "Diferença Significativa (p < α)" else "Sem diferença significativa")
            }
        }
    }
}

// 3. SEÇÃO DE ANOVA ONE-WAY
@Composable
private fun AnovaSection(uiState: InferentialUiState, viewModel: InferentialViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        OutlinedTextField(
            value = uiState.anovaGroup1Text,
            onValueChange = { viewModel.onAnovaG1Changed(it) },
            label = { Text("Grupo 1") },
            placeholder = { Text("85, 88, 90") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = uiState.anovaGroup2Text,
            onValueChange = { viewModel.onAnovaG2Changed(it) },
            label = { Text("Grupo 2") },
            placeholder = { Text("78, 80, 84") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        OutlinedTextField(
            value = uiState.anovaGroup3Text,
            onValueChange = { viewModel.onAnovaG3Changed(it) },
            label = { Text("Grupo 3 (Opcional)") },
            placeholder = { Text("92, 95, 91") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedButton(
                onClick = { viewModel.loadAnovaExample() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Exemplo Grupos")
            }

            Button(
                onClick = { viewModel.calculateAnova() },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Analytics, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Calcular ANOVA", fontWeight = FontWeight.Bold)
            }
        }

        uiState.anovaResult?.let { res ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiHeroCard(
                    title = "Estatística F",
                    value = formatNum(res.fStatistic),
                    modifier = Modifier.weight(1f)
                )
                KpiHeroCard(
                    title = "Valor-p",
                    value = formatNum(res.pValue),
                    subtitle = if (res.isSignificant) "Médias Diferentes" else "Médias Iguais",
                    modifier = Modifier.weight(1f)
                )
            }

            DetailCard("Tabela Resumo ANOVA") {
                MetricRow("SS Entre Grupos (SS_B)", formatNum(res.ssBetween))
                MetricRow("SS Dentro dos Grupos (SS_W)", formatNum(res.ssWithin))
                MetricRow("Soma dos Quadrados Total", formatNum(res.ssTotal))
                MetricRow("Graus de Liberdade (DF_B / DF_W)", "${res.dfBetween} / ${res.dfWithin}")
                MetricRow("Média dos Quadrados (MS_B / MS_W)", "${formatNum(res.msBetween)} / ${formatNum(res.msWithin)}")
            }
        }
    }
}

@Composable
private fun DetailCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(18.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            content()
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