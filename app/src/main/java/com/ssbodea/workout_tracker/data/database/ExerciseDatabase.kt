package com.ssbodea.workout_tracker.data.database

object ExerciseDatabase {
    val muscleGroups = listOf(
        "Chest", "Legs", "Core", "Back", "Shoulders", "Arms"
    )

    val exercisesByMuscleGroup = mapOf(
        "Chest" to listOf("Pushups", "Dips", "Diamond Pushups", "Wide Pushups"),
        "Legs" to listOf("Squats", "Lunges", "Calf Raises", "Glute Bridges"),
        "Core" to listOf("Crunches", "Leg Raises", "Plank", "Russian Twists"),
        "Back" to listOf("Pull-ups", "Chin-ups", "Superman", "Reverse Snow Angels"),
        "Shoulders" to listOf("Pike Pushups", "Handstand Pushups", "Shoulder Taps"),
        "Arms" to listOf("Triceps Dips", "Diamond Pushups", "Close Grip Pushups")
    )

    fun getExercisesForMuscleGroup(muscleGroup: String): List<String> {
        return exercisesByMuscleGroup[muscleGroup] ?: emptyList()
    }
}