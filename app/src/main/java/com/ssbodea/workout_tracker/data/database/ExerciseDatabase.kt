package com.ssbodea.workout_tracker.data.database

object ExerciseDatabase {
    val muscleGroups = listOf("Legs", "Core", "Push", "Pull")

    private val exercisesMap = mapOf(
        "Legs" to listOf(
            "Squat",
            "Leg Press",
            "Side Split",
            "Front Split",
            "Pistol Squat",
            "Dragon Squat"
        ),
        "Core" to listOf(
            "Plank",
            "Hollow Hold",
            "Arch Hold",
            "Side Plank",
            "Reverse Plank",
            "Sit-ups",
            "Reverse Crunch",
            "Ab Wheel Rollout",
            "Hanging Leg Raise",
            "L-sit",
            "V-sit",
            "Toes to Bar",
            "Windshield Wipers",
            "Dragon Flag",
            "Dragon Press"
        ),
        "Push" to listOf(
            "Bench Press",
            "Shoulder Press",
            "Push-up",
            "Dips",
            "Pike Push-up",
            "Handstand Push-up",
            "One Arm Push-up",
            "Planche Push-up",
            "One Arm Handstand Hold",
            "One Arm Planche"
        ),
        "Pull" to listOf(
            "Deadlift",
            "Bent Over Row",
            "Seated Row",
            "Back Extension",
            "Curl",
            "Pull-up",
            "Chin-up",
            "Muscle-up",
            "Back Lever",
            "Front Lever",
            "Human Flag",
            "One Arm Pull-up",
            "One Arm Muscle-up"
        )
    )

    fun getExercisesForMuscleGroup(muscleGroup: String) = exercisesMap[muscleGroup].orEmpty()
}