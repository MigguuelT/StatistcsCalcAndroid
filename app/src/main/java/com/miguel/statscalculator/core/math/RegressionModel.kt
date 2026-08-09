package com.miguel.statscalculator.core.math

data class Point2D(
    val x: Double,
    val y: Double
)

data class LinearRegressionResult(
    val n: Int,
    val slope: Double,              // Coeficiente Angular (b)
    val intercept: Double,          // Intercepto (a)
    val pearsonR: Double,           // Coeficiente de Correlação (r)
    val rSquared: Double,           // Coeficiente de Determinação (R²)
    val adjustedRSquared: Double,   // R² Ajustado
    val stdErrorEstimate: Double,   // S_e
    val stdErrorSlope: Double,      // Erro Padrão do Coeficiente
    val tStatistic: Double,         // Valor t
    val pValue: Double,             // Valor-p do modelo
    val equationString: String      // Ex: "y = 2.500 + 1.250x"
)