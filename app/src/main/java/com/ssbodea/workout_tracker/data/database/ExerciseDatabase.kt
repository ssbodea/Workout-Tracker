package com.ssbodea.workout_tracker.data.database

object ExerciseDatabase {

    val muscleGroups = listOf(
        "Legs",
        "Push",
        "Pull",
        "Core Dynamic",
        "Neck",
        "Core Isometrics",
        "Push Isometrics",
        "Pull Isometrics",
        "Grip Isometrics",
        "Flexibility"
    )

    private val exercisesMap = mapOf(
        "Legs" to listOf(
            "Squat",
            "Lunge",
            "Pistol Squat",
            "Dragon Squat"
        ),
        "Push" to listOf(
            "Push-up",
            "Diamond Push-up",
            "Archer Push-up",
            "One-Arm Push-up",
            "Pike Push-up",
            "Handstand Push-up",
            "Dips",
            "Planche Push-up"
        ),
        "Pull" to listOf(
            "Inverted Row",
            "Chin-up",
            "Pull-up",
            "Archer Pull-up",
            "Muscle-Up",
            "One-Arm Row",
            "One-Arm Pull-up",
            "One-Arm Muscle-up"
        ),
        "Core Dynamic" to listOf(
            "Sit-up",
            "Reverse Crunch",
            "Hanging Knee Raise",
            "Hanging Leg Raise",
            "Toes-to-Bar",
            "Windshield Wipers",
            "Dragon Flag"
        ),
        "Neck" to listOf(
            "Neck Flexion",
            "Neck Extension",
            "Neck Side Flexion",
            "Neck Bridge"
        ),
        "Core Isometrics" to listOf(
            "Plank",
            "Side Plank",
            "Hollow Hold",
            "Reverse Plank",
            "L-sit",
            "V-sit",
            "Dragon Press"
        ),
        "Push Isometrics" to listOf(
            "Arch Hold",
            "Handstand Hold",
            "Planche Hold"
        ),
        "Pull Isometrics" to listOf(
            "Front Lever",
            "Back Lever",
            "Human Flag"
        ),
        "Grip Isometrics" to listOf(
            "Dead Hang",
            "Towel Hang",
            "One-Arm Hang",
            "Hand-gripper"
        ),
        "Flexibility" to listOf(
            "Front Split",
            "Side Split"
        )
    )

    fun getExercisesForMuscleGroup(muscleGroup: String) = exercisesMap[muscleGroup].orEmpty()
}