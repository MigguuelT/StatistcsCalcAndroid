package com.miguel.statscalculator.presentation.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.miguel.statscalculator.presentation.navigation.Screen
import com.miguel.statscalculator.ui.components.ThemeToggleButton

data class ModuleItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

@Composable
fun DashboardScreen(
    isDarkMode: Boolean,
    onToggleTheme: () -> Unit,
    onNavigate: (String) -> Unit
) {
    val modules = listOf(
        ModuleItem(
            title = "Estatística Descritiva",
            description = "Média, Mediana, Moda, Desvio Padrão, Separatrizes e Quartis",
            icon = Icons.Default.BarChart,
            route = Screen.Descriptive.route
        ),
        ModuleItem(
            title = "Regressão & Correlação",
            description = "Regressão Linear Simples, Coeficiente r, R² e P-Value",
            icon = Icons.Default.ShowChart,
            route = Screen.Regression.route
        ),
        ModuleItem(
            title = "Probabilidade & Combinatória",
            description = "Fatorial, Permutações, Combinações e Teorema de Bayes",
            icon = Icons.Default.Casino,
            route = Screen.Probability.route
        ),
        ModuleItem(
            title = "Distribuições",
            description = "Distribuição Normal (Z-Score), Poisson e Binomial",
            icon = Icons.Default.Functions,
            route = Screen.Distributions.route
        ),
        ModuleItem(
            title = "Inferência & Hipóteses",
            description = "Intervalos de Confiança, Teste t, Teste Z e ANOVA",
            icon = Icons.Default.Analytics,
            route = Screen.Inferential.route
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        // --- CABEÇALHO ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp, bottom = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "StatsCalculator",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Análise estatística de alta precisão",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigate(Screen.History.route) }) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "Histórico",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                ThemeToggleButton(
                    isDarkMode = isDarkMode,
                    onToggleTheme = onToggleTheme
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // --- GRID DE MÓDULOS ---
        LazyVerticalGrid(
            columns = GridCells.Fixed(1),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(modules) { module ->
                ModuleCard(
                    item = module,
                    onClick = { onNavigate(module.route) }
                )
            }
        }
    }
}

@Composable
private fun ModuleCard(
    item: ModuleItem,
    onClick: () -> Unit
) {
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
            .clickable { onClick() }
            .padding(18.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(26.dp)
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
        }
    }
}