package com.miguel.statscalculator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// IMPORTS DAS TELAS DO PROJETO
import com.miguel.statscalculator.presentation.dashboard.DashboardScreen
import com.miguel.statscalculator.presentation.descriptive.DescriptiveScreen
import com.miguel.statscalculator.presentation.distributions.DistributionsScreen
import com.miguel.statscalculator.presentation.inferential.InferentialScreen
import com.miguel.statscalculator.presentation.navigation.Screen
import com.miguel.statscalculator.presentation.probability.ProbabilityScreen
import com.miguel.statscalculator.presentation.regression.RegressionScreen
import com.miguel.statscalculator.ui.theme.StatsCalculatorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val systemInDark = isSystemInDarkTheme()
            var isDarkMode by remember { mutableStateOf(systemInDark) }

            StatsCalculatorTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = Screen.Dashboard.route
                    ) {
                        // 1. Dashboard Principal
                        composable(Screen.Dashboard.route) {
                            DashboardScreen(
                                isDarkMode = isDarkMode,
                                onToggleTheme = { isDarkMode = !isDarkMode },
                                onNavigate = { route ->
                                    try {
                                        navController.navigate(route)
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                }
                            )
                        }

                        // 2. Estatística Descritiva
                        composable(Screen.Descriptive.route) {
                            DescriptiveScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // 3. Regressão & Correlação
                        composable(Screen.Regression.route) {
                            RegressionScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // 4. Probabilidade & Combinatória
                        composable(Screen.Probability.route) {
                            ProbabilityScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // 5. Distribuições de Probabilidade
                        composable(Screen.Distributions.route) {
                            DistributionsScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        // 6. Inferência & Hipóteses
                        composable(Screen.Inferential.route) {
                            InferentialScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable(Screen.History.route) {
                            com.miguel.statscalculator.presentation.history.HistoryScreen(
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}