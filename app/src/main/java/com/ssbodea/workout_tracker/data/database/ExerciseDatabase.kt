package com.ssbodea.workout_tracker.data.database

object ExerciseDatabase {
    val muscleGroups = listOf(
        "Legs", "Core", "Chest", "Back", "Arms", "Neck", "Full Body"
    )

    val exercisesByMuscleGroup = mapOf(
        "Legs" to listOf(
            "Bodyweight Squats", "Pistol Squats", "Lunges", "Jump Squats",
            "Calf Raises", "Glute Bridges", "Bulgarian Split Squats",
            "Step-ups", "Wall Sits", "Single Leg Deadlifts", "Squat Jumps",
            "Lunge Jumps", "Side Lunges", "Curtsy Lunges", "Sissy Squats",
            "Shrimp Squats", "Nordic Curls", "Single Leg Calf Raises"
        ),
        "Core" to listOf(
            "Plank", "Side Plank", "Leg Raises", "Hollow Body Hold",
            "Russian Twists", "L-sit", "V-ups", "Bicycle Crunches",
            "Flutter Kicks", "Reverse Crunches", "Mountain Climbers",
            "Scissor Kicks", "Dragon Flags", "Windshield Wipers",
            "Toes to Bar", "Hanging Knee Raises", "Arch Ups", "Superman Plank"
        ),
        "Chest" to listOf(
            "Push-ups", "Wide Push-ups", "Diamond Push-ups",
            "Decline Push-ups", "Archer Push-ups", "Incline Push-ups",
            "Plyometric Push-ups", "Typewriter Push-ups", "Spiderman Push-ups",
            "One Arm Push-ups", "Clap Push-ups", "Pseudo Planche Push-ups",
            "Hindu Push-ups", "Dive Bomber Push-ups", "Staggered Push-ups"
        ),
        "Back" to listOf(
            "Pull-ups", "Chin-ups", "Australian Pull-ups",
            "Bodyweight Rows", "Superman Holds", "Arch Hangs",
            "Skin the Cat", "L-sit Pull-ups", "Archer Pull-ups",
            "Typewriter Pull-ups", "Commando Pull-ups", "Muscle-ups",
            "Back Lever Progressions", "Front Lever Progressions"
        ),
        "Arms" to listOf(
            "Pike Push-ups", "Handstand Push-ups", "Triceps Dips",
            "Close Grip Push-ups", "Chin-ups", "Diamond Push-ups",
            "Bench Dips", "Bodyweight Triceps Extensions", "Handstand Hold",
            "Wall Handstand Push-ups", "Ring Dips", "L-sit Hold",
            "Planche Progressions", "Korean Dips", "Support Hold"
        ),
        "Neck" to listOf(
            "Neck Bridges", "Neck Curls", "Side Neck Raises",
            "Neck Retractions", "Resisted Neck Flexion", "Neck Nods",
            "Wrestler's Bridge", "Front Neck Plank", "Side Neck Plank",
            "Neck Circles", "Chin Tucks", "Neck Isometrics"
        ),
        "Full Body" to listOf(
            "Burpees", "Bear Crawls", "Mountain Climbers",
            "Plank to Push-up", "Jumping Jacks", "Squat Thrusts",
            "High Knees", "Butt Kicks", "Sprawls", "Man Makers",
            "Get-ups", "Crawling Variations", "Jumping Lunges",
            "Broad Jumps", "Tuck Jumps", "Star Jumps"
        )
    )

    fun getExercisesForMuscleGroup(muscleGroup: String): List<String> {
        return exercisesByMuscleGroup[muscleGroup] ?: emptyList()
    }
}