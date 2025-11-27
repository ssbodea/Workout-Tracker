package com.ssbodea.workout_tracker.data.models

import java.text.SimpleDateFormat
import java.util.*

data class Workout(
    val id: Int,
    val dateTime: Date = Date(),
    val exercises: MutableList<Exercise> = mutableListOf(),
    var isLocked: Boolean = false
) {
    fun getFormattedDateTime(): String {
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
        return formatter.format(dateTime)
    }
}