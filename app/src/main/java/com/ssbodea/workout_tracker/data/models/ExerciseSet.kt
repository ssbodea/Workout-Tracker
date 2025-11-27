package com.ssbodea.workout_tracker.data.models

data class ExerciseSet(
    val repetitions: Int,
    val weight: Int? = null
) {
    override fun toString(): String {
        return if (weight != null) "${repetitions}x${weight}kg" else repetitions.toString()
    }
}