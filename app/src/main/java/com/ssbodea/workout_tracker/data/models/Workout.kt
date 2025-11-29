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
        val formatter = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US)
        return formatter.format(dateTime)
    }

    // Add equals and hashCode for proper list operations
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Workout

        if (id != other.id) return false
        if (dateTime != other.dateTime) return false
        if (exercises != other.exercises) return false
        if (isLocked != other.isLocked) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + dateTime.hashCode()
        result = 31 * result + exercises.hashCode()
        result = 31 * result + isLocked.hashCode()
        return result
    }
}