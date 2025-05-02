package br.all.domain.services

interface LevenshteinSimilarityCalculator {
    fun similarity(text1: String, text2: String): Double
}