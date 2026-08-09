package com.miguel.statscalculator.presentation.inferential

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DataObject
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.miguel.statscalculator.ui.components.KpiHeroCard
import com.miguel.statscalculator.util.AlternativeHypothesis
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InferentialScreen(
    onNavigateBack: () -> Unit,
    viewModel: InferentialViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Inferência Estatística",
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
        ) {
            TabRow(
                selectedTabIndex = state.mainTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = state.mainTab == 0,
                    onClick = { viewModel.onMainTabSelected(0) },
                    text = { Text("Intervalo de Confiança", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = state.mainTab == 1,
                    onClick = { viewModel.onMainTabSelected(1) },
                    text = { Text("Teste de Hipóteses", fontWeight = FontWeight.Bold) }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                if (state.mainTab == 0) {
                    ConfidenceIntervalContent(state = state, viewModel = viewModel)
                } else {
                    HypothesisTestContent(state = state, viewModel = viewModel)
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun HypothesisTestContent(
    state: InferentialState,
    viewModel: InferentialViewModel
) {
    Text(text = "Parâmetro em Teste:", style = MaterialTheme.typography.labelMedium)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = state.htParamType == InferentialParamType.MEAN,
            onClick = { viewModel.onHtParamTypeChanged(InferentialParamType.MEAN) },
            label = { Text("Média (μ)") },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = state.htParamType == InferentialParamType.PROPORTION,
            onClick = { viewModel.onHtParamTypeChanged(InferentialParamType.PROPORTION) },
            label = { Text("Proporção (p)") },
            modifier = Modifier.weight(1f)
        )
    }

    Text(text = "Hipótese Alternativa (Hₐ):", style = MaterialTheme.typography.labelMedium)
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        FilterChip(
            selected = state.htAlternative == AlternativeHypothesis.TWO_SIDED,
            onClick = { viewModel.onHtAlternativeChanged(AlternativeHypothesis.TWO_SIDED) },
            label = { Text("≠ (Bilateral)", fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = state.htAlternative == AlternativeHypothesis.LESS,
            onClick = { viewModel.onHtAlternativeChanged(AlternativeHypothesis.LESS) },
            label = { Text("< (Esquerda)", fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = state.htAlternative == AlternativeHypothesis.GREATER,
            onClick = { viewModel.onHtAlternativeChanged(AlternativeHypothesis.GREATER) },
            label = { Text("> (Direita)", fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = state.htNullValueText,
            onValueChange = { viewModel.onHtNullValueChange(it) },
            label = { Text(if (state.htParamType == InferentialParamType.MEAN) "Valor H₀ (μ₀)" else "Valor H₀ (p₀)") },
            placeholder = { Text(if (state.htParamType == InferentialParamType.MEAN) "ex: 100.0" else "ex: 0.50") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = state.htSampleEstimateText,
            onValueChange = { viewModel.onHtSampleEstimateChange(it) },
            label = { Text(if (state.htParamType == InferentialParamType.MEAN) "Média (x̅)" else "Proporção (p̂)") },
            placeholder = { Text(if (state.htParamType == InferentialParamType.MEAN) "ex: 104.0" else "ex: 0.58") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        if (state.htParamType == InferentialParamType.MEAN) {
            OutlinedTextField(
                value = state.htStdDevText,
                onValueChange = { viewModel.onHtStdDevChange(it) },
                label = { Text("Desvio Padrão (s)") },
                placeholder = { Text("ex: 15.0") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }
        OutlinedTextField(
            value = state.htSampleSizeText,
            onValueChange = { viewModel.onHtSampleSizeChange(it) },
            label = { Text("Amostra (n)") },
            placeholder = { Text("ex: 100") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = state.htAlphaText,
            onValueChange = { viewModel.onHtAlphaChange(it) },
            label = { Text("Significância (α)") },
            placeholder = { Text("ex: 0.05") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = { viewModel.loadExample() },
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

    Button(
        onClick = { viewModel.calculateHt() },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Calcular Teste de Hipóteses", fontWeight = FontWeight.Bold)
    }

    state.errorMessage?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
    }

    state.htResult?.let { res ->
        val decisionText = if (res.rejectNull) "Rejeitar H₀" else "Não Rejeitar H₀"
        val decisionSubtitle = if (res.rejectNull) "Evidência estatística significativa (p < α)" else "Sem evidência suficiente (p ≥ α)"

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiHeroCard(
                title = "Decisão Final",
                value = decisionText,
                subtitle = decisionSubtitle,
                modifier = Modifier.weight(1.2f)
            )
            KpiHeroCard(
                title = "Valor-p",
                value = formatNum(res.pValue),
                subtitle = "α = ${formatNum(res.alpha)}",
                modifier = Modifier.weight(0.8f)
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiHeroCard(
                title = "Escore Z_calc",
                value = formatNum(res.testStatistic),
                subtitle = "Estatística do Teste",
                modifier = Modifier.weight(1f)
            )
            KpiHeroCard(
                title = "Z_crítico",
                value = formatNum(res.criticalValue),
                subtitle = "Ponto de Corte (α=${formatNum(res.alpha)})",
                modifier = Modifier.weight(1f)
            )
        }

        HypothesisTestCanvas(
            result = res,
            alternative = state.htAlternative
        )
    }
}

@Composable
private fun ConfidenceIntervalContent(
    state: InferentialState,
    viewModel: InferentialViewModel
) {
    Text(text = "Parâmetro a Estimar:", style = MaterialTheme.typography.labelMedium)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        FilterChip(
            selected = state.ciParamType == InferentialParamType.MEAN,
            onClick = { viewModel.onCiParamTypeChanged(InferentialParamType.MEAN) },
            label = { Text("Média Populacional (μ)") },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = state.ciParamType == InferentialParamType.PROPORTION,
            onClick = { viewModel.onCiParamTypeChanged(InferentialParamType.PROPORTION) },
            label = { Text("Proporção (p)") },
            modifier = Modifier.weight(1f)
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = state.ciEstimateText,
            onValueChange = { viewModel.onCiEstimateChange(it) },
            label = { Text(if (state.ciParamType == InferentialParamType.MEAN) "Média Amostral (x̅)" else "Proporção (p̂)") },
            placeholder = { Text(if (state.ciParamType == InferentialParamType.MEAN) "ex: 100.0" else "ex: 0.45") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
        if (state.ciParamType == InferentialParamType.MEAN) {
            OutlinedTextField(
                value = state.ciStdDevText,
                onValueChange = { viewModel.onCiStdDevChange(it) },
                label = { Text("Desvio Padrão (s)") },
                placeholder = { Text("ex: 15.0") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = state.ciSampleSizeText,
            onValueChange = { viewModel.onCiSampleSizeChange(it) },
            label = { Text("Amostra (n)") },
            placeholder = { Text("ex: 100") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = state.ciConfidenceLevelText,
            onValueChange = { viewModel.onCiConfidenceLevelChange(it) },
            label = { Text("Confiança (%)") },
            placeholder = { Text("ex: 95") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
    }

    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedButton(
            onClick = { viewModel.loadExample() },
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

    Button(
        onClick = { viewModel.calculateCi() },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Calcular Intervalo de Confiança", fontWeight = FontWeight.Bold)
    }

    state.errorMessage?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
    }

    state.ciResult?.let { res ->
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiHeroCard(
                title = "Intervalo Estimado",
                value = "[${formatNum(res.lowerLimit)} ; ${formatNum(res.upperLimit)}]",
                subtitle = "IC de ${state.ciConfidenceLevelText}%",
                modifier = Modifier.weight(1f)
            )
            KpiHeroCard(
                title = "Margem de Erro (E)",
                value = "± ${formatNum(res.marginOfError)}",
                subtitle = "Z_critico = ${formatNum(res.criticalValue)}",
                modifier = Modifier.weight(1f)
            )
        }

        ConfidenceIntervalCanvas(
            result = res,
            pointEstimate = state.ciPointEstimateValue,
            confidenceLevel = state.ciConfidenceLevelText
        )
    }
}

private fun formatNum(v: Double): String {
    return String.format(Locale.US, "%.4f", v)
}