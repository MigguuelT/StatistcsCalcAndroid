package com.miguel.statscalculator.presentation.probability

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Casino
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
fun ProbabilityScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProbabilityViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Probabilidade & Combinatória",
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
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // --- SEÇÃO 1: ANÁLISE COMBINATÓRIA ---
            Text(
                text = "1. ANÁLISE COMBINATÓRIA",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.inputN,
                    onValueChange = { viewModel.onInputNChanged(it) },
                    label = { Text("Elementos (n)") },
                    placeholder = { Text("Ex: 10") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )

                OutlinedTextField(
                    value = uiState.inputK,
                    onValueChange = { viewModel.onInputKChanged(it) },
                    label = { Text("Agrupamento (k)") },
                    placeholder = { Text("Ex: 3") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            Button(
                onClick = { viewModel.calculateCombinatorics() },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Casino, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Calcular Combinatória", fontWeight = FontWeight.Bold)
            }

            uiState.combinatoricsError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            if (uiState.factorialResult != null) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiHeroCard(
                        title = "Fatorial (n!)",
                        value = uiState.factorialResult!!,
                        modifier = Modifier.weight(1f)
                    )
                    KpiHeroCard(
                        title = "Combinação C(n,k)",
                        value = uiState.combinationResult ?: "--",
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    KpiHeroCard(
                        title = "Permutação P(n)",
                        value = uiState.permutationResult!!,
                        modifier = Modifier.weight(1f)
                    )
                    KpiHeroCard(
                        title = "Arranjo A(n,k)",
                        value = uiState.arrangementResult ?: "--",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(vertical = 8.dp))

            // --- SEÇÃO 2: TEOREMA DE BAYES ---
            Text(
                text = "2. TEOREMA DE BAYES",
                style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp),
                color = MaterialTheme.colorScheme.secondary
            )

            OutlinedTextField(
                value = uiState.inputPriorA,
                onValueChange = { viewModel.onPriorAChanged(it) },
                label = { Text("P(A) - Probabilidade a Priori") },
                placeholder = { Text("Ex: 0.01 (1%)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = uiState.inputLikelihoodBGivenA,
                onValueChange = { viewModel.onLikelihoodBGivenAChanged(it) },
                label = { Text("P(B|A) - Verossimilhança") },
                placeholder = { Text("Ex: 0.95") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
                value = uiState.inputLikelihoodBGivenNotA,
                onValueChange = { viewModel.onLikelihoodBGivenNotAChanged(it) },
                label = { Text("P(B|¬A) - Falso Positivo") },
                placeholder = { Text("Ex: 0.05") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { viewModel.loadBayesExample() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.DataObject, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Exemplo Médico")
                }

                Button(
                    onClick = { viewModel.calculateBayes() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                ) {
                    Text("Calcular P(A|B)", fontWeight = FontWeight.Bold)
                }
            }

            uiState.bayesError?.let {
                Text(text = it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
            }

            uiState.bayesResult?.let { bayes ->
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
                            text = "Resultado da Inferência Bayesiana",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                        MetricRow("Probabilidade a Posteriori P(A|B)", "${String.format(Locale.US, "%.4f", bayes.posteriorA * 100)}%")
                        MetricRow("Probabilidade Total de B P(B)", String.format(Locale.US, "%.4f", bayes.totalProbabilityB))
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
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
        Text(text = value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold), color = MaterialTheme.colorScheme.primary)
    }
}