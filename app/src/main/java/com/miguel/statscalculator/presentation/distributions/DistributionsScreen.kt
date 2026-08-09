package com.miguel.statscalculator.presentation.distributions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.statscalculator.ui.components.KpiHeroCard
import com.miguel.statscalculator.util.StatisticalUtils
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistributionsScreen(
    onNavigateBack: () -> Unit
) {
    var selectedTab by rememberSaveable { mutableStateOf(0) } // 0: Normal, 1: Poisson, 2: Binomial

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Distribuições de Probabilidade",
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
            // --- SELETOR DE ABAS ---
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Normal", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Poisson", fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Binomial", fontWeight = FontWeight.Bold) }
                )
            }

            // --- CONTEÚDO DA ABA SELECIONADA ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))

                when (selectedTab) {
                    0 -> NormalSection()
                    1 -> PoissonSection()
                    2 -> BinomialSection()
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ==========================================
// 1. ABA DISTRIBUIÇÃO NORMAL
// ==========================================
@Composable
private fun NormalSection() {
    var meanText by rememberSaveable { mutableStateOf("") }
    var stdDevText by rememberSaveable { mutableStateOf("") }
    var x1Text by rememberSaveable { mutableStateOf("") }
    var x2Text by rememberSaveable { mutableStateOf("") }
    var probType by rememberSaveable { mutableStateOf(NormalProbabilityType.LESS_THAN) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var canvasParams by remember { mutableStateOf<NormalDistributionParams?>(null) }
    var calculatedZ1 by remember { mutableStateOf<Double?>(null) }

    fun calculateNormal() {
        errorMessage = null
        val mean = meanText.toDoubleOrNull()
        val stdDev = stdDevText.toDoubleOrNull()
        val x1 = x1Text.toDoubleOrNull()
        val x2 = x2Text.toDoubleOrNull()

        if (mean == null || stdDev == null || x1 == null || (probType == NormalProbabilityType.BETWEEN && x2 == null)) {
            errorMessage = "Insira valores numéricos válidos."
            return
        }
        if (stdDev <= 0) {
            errorMessage = "O desvio padrão (σ) deve ser maior que zero."
            return
        }

        val z1 = (x1 - mean) / stdDev
        calculatedZ1 = z1

        val prob = when (probType) {
            NormalProbabilityType.LESS_THAN -> StatisticalUtils.normalCdf(x1, mean, stdDev)
            NormalProbabilityType.GREATER_THAN -> 1.0 - StatisticalUtils.normalCdf(x1, mean, stdDev)
            NormalProbabilityType.BETWEEN -> {
                val x2Val = x2 ?: x1
                abs(StatisticalUtils.normalCdf(x2Val, mean, stdDev) - StatisticalUtils.normalCdf(x1, mean, stdDev))
            }
        }

        canvasParams = NormalDistributionParams(
            mean = mean,
            stdDev = stdDev,
            x1 = x1,
            x2 = x2 ?: x1,
            type = probType,
            calculatedProbability = prob
        )
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = meanText,
            onValueChange = { meanText = it },
            label = { Text("Média (μ)") },
            placeholder = { Text("ex: 0.0") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = stdDevText,
            onValueChange = { stdDevText = it },
            label = { Text("Desvio Padrão (σ)") },
            placeholder = { Text("ex: 1.0") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
    }

    Text(text = "Tipo de Probabilidade Desejada:", style = MaterialTheme.typography.labelMedium)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FilterChip(
            selected = probType == NormalProbabilityType.LESS_THAN,
            onClick = { probType = NormalProbabilityType.LESS_THAN },
            label = { Text("P(X ≤ x)", fontSize = 12.sp) },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = probType == NormalProbabilityType.GREATER_THAN,
            onClick = { probType = NormalProbabilityType.GREATER_THAN },
            label = { Text("P(X ≥ x)", fontSize = 12.sp) },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = probType == NormalProbabilityType.BETWEEN,
            onClick = { probType = NormalProbabilityType.BETWEEN },
            label = { Text("P(x1 ≤ X ≤ x2)", fontSize = 11.sp) },
            modifier = Modifier.weight(1.2f)
        )
    }

    if (probType == NormalProbabilityType.BETWEEN) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            OutlinedTextField(
                value = x1Text,
                onValueChange = { x1Text = it },
                label = { Text("Limite x1") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
            OutlinedTextField(
                value = x2Text,
                onValueChange = { x2Text = it },
                label = { Text("Limite x2") },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(12.dp)
            )
        }
    } else {
        OutlinedTextField(
            value = x1Text,
            onValueChange = { x1Text = it },
            label = { Text("Valor de Corte (x)") },
            placeholder = { Text("ex: 1.96") },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = {
                meanText = "0.0"
                stdDevText = "1.0"
                x1Text = "1.96"
                x2Text = "2.5"
                probType = NormalProbabilityType.LESS_THAN
                calculateNormal()
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Exemplo")
        }

        OutlinedButton(
            onClick = {
                meanText = ""
                stdDevText = ""
                x1Text = ""
                x2Text = ""
                canvasParams = null
                calculatedZ1 = null
                errorMessage = null
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Limpar")
        }
    }

    Button(
        onClick = { calculateNormal() },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Calcular Distribuição Normal", fontWeight = FontWeight.Bold)
    }

    errorMessage?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
    }

    canvasParams?.let { params ->
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiHeroCard(
                title = "Probabilidade (P)",
                value = "${formatNum(params.calculatedProbability * 100)}%",
                subtitle = "P = ${formatNum(params.calculatedProbability)}",
                modifier = Modifier.weight(1f)
            )
            calculatedZ1?.let { z ->
                KpiHeroCard(
                    title = "Escore Z",
                    value = formatNum(z),
                    subtitle = "Z = (x - μ) / σ",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        NormalDistributionCanvas(params = params)
    }
}

// ==========================================
// 2. ABA DISTRIBUIÇÃO DE POISSON
// ==========================================
@Composable
private fun PoissonSection() {
    var lambdaText by rememberSaveable { mutableStateOf("") }
    var kText by rememberSaveable { mutableStateOf("") }
    var poissonType by rememberSaveable { mutableStateOf(0) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var calculatedProb by remember { mutableStateOf<Double?>(null) }
    var lambdaVal by remember { mutableStateOf(0.0) }

    fun calculatePoisson() {
        errorMessage = null
        val l = lambdaText.toDoubleOrNull()
        val k = kText.toIntOrNull()

        if (l == null || k == null) {
            errorMessage = "Insira valores válidos para Lambda (λ) e Ocorrências (k)."
            return
        }
        if (l <= 0 || k < 0) {
            errorMessage = "Lambda deve ser > 0 e k deve ser ≥ 0."
            return
        }

        lambdaVal = l
        calculatedProb = when (poissonType) {
            0 -> StatisticalUtils.poissonPmf(k, l)
            1 -> StatisticalUtils.poissonCdf(k, l)
            else -> 1.0 - StatisticalUtils.poissonCdf(k - 1, l)
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        OutlinedTextField(
            value = lambdaText,
            onValueChange = { lambdaText = it },
            label = { Text("Taxa Média (λ)") },
            placeholder = { Text("ex: 3.5") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = kText,
            onValueChange = { kText = it },
            label = { Text("Ocorrências (k)") },
            placeholder = { Text("ex: 2") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
    }

    Text(text = "Tipo de Probabilidade Poisson:", style = MaterialTheme.typography.labelMedium)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FilterChip(
            selected = poissonType == 0,
            onClick = { poissonType = 0 },
            label = { Text("Exata: P(X = k)", fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = poissonType == 1,
            onClick = { poissonType = 1 },
            label = { Text("Acum.: P(X ≤ k)", fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = poissonType == 2,
            onClick = { poissonType = 2 },
            label = { Text("Acum.: P(X ≥ k)", fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = {
                lambdaText = "3.5"
                kText = "2"
                poissonType = 0
                calculatePoisson()
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Exemplo")
        }

        OutlinedButton(
            onClick = {
                lambdaText = ""
                kText = ""
                calculatedProb = null
                errorMessage = null
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Limpar")
        }
    }

    Button(
        onClick = { calculatePoisson() },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Calcular Poisson", fontWeight = FontWeight.Bold)
    }

    errorMessage?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
    }

    calculatedProb?.let { prob ->
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiHeroCard(
                title = "Probabilidade (P)",
                value = "${formatNum(prob * 100)}%",
                subtitle = "P = ${formatNum(prob)}",
                modifier = Modifier.weight(1f)
            )
            KpiHeroCard(
                title = "Média / Variância",
                value = formatNum(lambdaVal),
                subtitle = "E[X] = Var[X] = λ",
                modifier = Modifier.weight(1f)
            )
        }

        PoissonDetailCard(lambda = lambdaVal, k = kText.toIntOrNull() ?: 0, prob = prob)
    }
}

// ==========================================
// 3. ABA DISTRIBUIÇÃO BINOMIAL
// ==========================================
@Composable
private fun BinomialSection() {
    var nText by rememberSaveable { mutableStateOf("") }
    var pText by rememberSaveable { mutableStateOf("") }
    var kText by rememberSaveable { mutableStateOf("") }
    var binomialType by rememberSaveable { mutableStateOf(0) }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var calculatedProb by remember { mutableStateOf<Double?>(null) }
    var meanVal by remember { mutableStateOf(0.0) }
    var varianceVal by remember { mutableStateOf(0.0) }

    fun calculateBinomial() {
        errorMessage = null
        val n = nText.toIntOrNull()
        val p = pText.toDoubleOrNull()
        val k = kText.toIntOrNull()

        if (n == null || p == null || k == null) {
            errorMessage = "Insira valores numéricos válidos."
            return
        }
        if (n <= 0 || p < 0.0 || p > 1.0 || k < 0 || k > n) {
            errorMessage = "Garanta que n > 0, 0 ≤ p ≤ 1 e 0 ≤ k ≤ n."
            return
        }

        meanVal = n * p
        varianceVal = n * p * (1.0 - p)

        calculatedProb = when (binomialType) {
            0 -> StatisticalUtils.binomialPmf(k, n, p)
            1 -> StatisticalUtils.binomialCdf(k, n, p)
            else -> 1.0 - StatisticalUtils.binomialCdf(k - 1, n, p)
        }
    }

    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = nText,
            onValueChange = { nText = it },
            label = { Text("Ensaios (n)") },
            placeholder = { Text("ex: 10") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = pText,
            onValueChange = { pText = it },
            label = { Text("Prob. (p)") },
            placeholder = { Text("ex: 0.5") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
        OutlinedTextField(
            value = kText,
            onValueChange = { kText = it },
            label = { Text("Sucessos (k)") },
            placeholder = { Text("ex: 5") },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(12.dp)
        )
    }

    Text(text = "Tipo de Probabilidade Binomial:", style = MaterialTheme.typography.labelMedium)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        FilterChip(
            selected = binomialType == 0,
            onClick = { binomialType = 0 },
            label = { Text("Exata: P(X = k)", fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = binomialType == 1,
            onClick = { binomialType = 1 },
            label = { Text("Acum.: P(X ≤ k)", fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
        )
        FilterChip(
            selected = binomialType == 2,
            onClick = { binomialType = 2 },
            label = { Text("Acum.: P(X ≥ k)", fontSize = 11.sp) },
            modifier = Modifier.weight(1f)
        )
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedButton(
            onClick = {
                nText = "10"
                pText = "0.5"
                kText = "5"
                binomialType = 0
                calculateBinomial()
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Exemplo")
        }

        OutlinedButton(
            onClick = {
                nText = ""
                pText = ""
                kText = ""
                calculatedProb = null
                errorMessage = null
            },
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Limpar")
        }
    }

    Button(
        onClick = { calculateBinomial() },
        modifier = Modifier.fillMaxWidth().height(50.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Icon(imageVector = Icons.Default.Calculate, contentDescription = null)
        Spacer(modifier = Modifier.width(8.dp))
        Text("Calcular Binomial", fontWeight = FontWeight.Bold)
    }

    errorMessage?.let {
        Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
    }

    calculatedProb?.let { prob ->
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KpiHeroCard(
                title = "Probabilidade (P)",
                value = "${formatNum(prob * 100)}%",
                subtitle = "P = ${formatNum(prob)}",
                modifier = Modifier.weight(1f)
            )
            KpiHeroCard(
                title = "Média (E[X])",
                value = formatNum(meanVal),
                subtitle = "E[X] = n * p",
                modifier = Modifier.weight(1f)
            )
        }

        BinomialDetailCard(
            mean = meanVal,
            variance = varianceVal,
            stdDev = sqrt(varianceVal)
        )
    }
}

// ==========================================
// COMPONENTES AUXILIARES
// ==========================================
@Composable
private fun PoissonDetailCard(lambda: Double, k: Int, prob: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Parâmetros do Modelo de Poisson",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            MetricRow("Fórmula PMF", "P(X = k) = (λ^k * e^-λ) / k!")
            MetricRow("Desvio Padrão (σ)", formatNum(sqrt(lambda)))
            MetricRow("Resultado Computado", formatNum(prob))
        }
    }
}

@Composable
private fun BinomialDetailCard(mean: Double, variance: Double, stdDev: Double) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Parâmetros do Modelo Binomial",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.onSurface
            )
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            MetricRow("Fórmula PMF", "P(X = k) = C(n,k) * p^k * (1-p)^(n-k)")
            MetricRow("Variância (σ²)", formatNum(variance))
            MetricRow("Desvio Padrão (σ)", formatNum(stdDev))
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