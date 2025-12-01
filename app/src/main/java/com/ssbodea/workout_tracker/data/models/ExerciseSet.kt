package com.ssbodea.workout_tracker.data.models

data class ExerciseSet(
    val repetitions: Int,
    val weight: Int? = null
) {
    override fun toString() = weight?.let { "${repetitions}x${it}kg" } ?: repetitions.toString()
}