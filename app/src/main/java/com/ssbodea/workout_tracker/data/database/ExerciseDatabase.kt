package com.ssbodea.workout_tracker.data.database

object ExerciseDatabase {
    val muscleGroups = listOf("Legs", "Core", "Push", "Pull")

    private val exercisesMap = mapOf(
        "Legs" to listOf("Squat", "Pistol Squat", "Dragon Squat", "Side Split", "Front Split"),
        "Core" to listOf("Hanging Leg Raise", "L-sit", "Toes to Bar", "V-sit", "Windshield Wiper", "Dragon Flag", "Ab Wheel Rollout"),
        "Push" to listOf("Push-up", "Pike Push-up", "Dips", "Handstand Push-up", "One Arm Push-up", "Planche Push-up", "One Arm Handstand Hold"),
        "Pull" to listOf("Pull-up", "Chin-up", "Muscle-up", "Back Lever", "Front Lever", "Human Flag", "One Arm Pull-up")
    )

    fun getExercisesForMuscleGroup(muscleGroup: String): List<String> {
        return exercisesMap[muscleGroup].orEmpty()
    }
}