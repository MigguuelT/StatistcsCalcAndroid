package com.miguel.statscalculator.presentation.navigation

sealed class Screen(val route: String, val title: String) {
    object Dashboard : Screen("dashboard", "Painel Principal")
    object Descriptive : Screen("descriptive", "Estatística Descritiva")
    object Regression : Screen("regression", "Regressão Linear")
    object Probability : Screen("probability", "Probabilidade & Combinatória")
    object Distributions : Screen("distributions", "Distribuições")
    object Inferential : Screen("inferential", "Estatística Inferencial")
    object History : Screen("history", "Histórico de Cálculos")
}