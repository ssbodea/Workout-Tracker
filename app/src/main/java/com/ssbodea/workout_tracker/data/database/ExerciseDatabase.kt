object ExerciseDatabase {
    val muscleGroups = listOf("Legs", "Core", "Push", "Pull")

    private val exercisesMap = mapOf(
        "Legs" to listOf(
            "Squat",
            "Leg Press",
            "Pistol Squat",
            "Dragon Squat",
            "Side Split",
            "Front Split"
        ),
        "Core" to listOf(
            "Plank",
            "Hollow Hold",
            "Arch Hold",
            "Side Plank",
            "Sit Ups",
            "Reverse Crunches",
            "Ab Wheel Rollout",
            "Hanging Leg Raise",
            "L-sit",
            "Toes to Bar",
            "V-sit",
            "Windshield Wiper",
            "Dragon Flag"
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
            "One Arm Handstand Hold"
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