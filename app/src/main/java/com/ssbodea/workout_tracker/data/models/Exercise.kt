package com.ssbodea.workout_tracker.data.models

data class Exercise(
    val muscleGroup: String,
    val name: String,
    val sets: MutableList<ExerciseSet> = mutableListOf()
)