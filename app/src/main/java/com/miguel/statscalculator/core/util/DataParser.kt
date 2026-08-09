package com.miguel.statscalculator.core.util

import com.miguel.statscalculator.core.math.Point2D

object DataParser {

    /**
     * Converte texto bruto em uma lista de números Double.
     * Suporta dados separados por espaço, quebra de linha, ponto e vírgula ou vírgulas.
     */
    fun parseSingleList(input: String): List<Double> {
        if (input.isBlank()) return emptyList()

        // 1. Normaliza separadores de linha, tabs e ponto-e-vírgula para espaço simples
        val normalized = input
            .replace("\n", " ")
            .replace(";", " ")
            .replace("\t", " ")
            .trim()

        // 2. Decide a estratégia de divisão (por espaço ou por vírgula isolada)
        val rawTokens = if (normalized.contains(" ")) {
            normalized.split(Regex("\\s+"))
        } else {
            normalized.split(",")
        }

        // 3. Limpa caracteres residuais e converte para Double
        return rawTokens.mapNotNull { token ->
            val cleanToken = token.trim()
                .removeSuffix(",") // Remove vírgula isolada no final do token (ex: "2.1,")
                .replace(",", ".") // Converte vírgula decimal para ponto decimal
            cleanToken.toDoubleOrNull()
        }
    }

    /**
     * Converte duas entradas de texto (X e Y) em uma lista de Point2D.
     */
    fun parsePairedList(inputX: String, inputY: String): List<Point2D> {
        val listX = parseSingleList(inputX)
        val listY = parseSingleList(inputY)

        val minSize = minOf(listX.size, listY.size)
        return (0 until minSize).map { i ->
            Point2D(x = listX[i], y = listY[i])
        }
    }
}