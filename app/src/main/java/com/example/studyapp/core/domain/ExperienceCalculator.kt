package com.example.studyapp.core.domain

import kotlin.math.pow

object ExperienceCalculator {
    /**
     * Calcula la experiencia necesaria para subir del nivel actual al siguiente.
     * Formula ajustada para que el crecimiento en niveles altos sea más suave:
     * MaxExp = 500 * Level + 10 * Level^1.8
     */
    fun calculateMaxExp(level: Int): Int {
        if (level <= 0) return 100
        val linearPart = 500.0 * level
        val exponentialPart = 10.0 * level.toDouble().pow(1.8)
        return (linearPart + exponentialPart).toInt()
    }
}
