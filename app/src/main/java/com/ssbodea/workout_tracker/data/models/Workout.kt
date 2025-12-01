package com.ssbodea.workout_tracker.data.models

import java.text.SimpleDateFormat
import java.util.*

data class Workout(
    val id: Int,
    val dateTime: Date = Date(),
    val exercises: MutableList<Exercise> = mutableListOf(),
    var isLocked: Boolean = false
) {
    fun getFormattedDateTime() = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.US).format(dateTime)

    override fun equals(other: Any?) = this === other ||
            (javaClass == other?.javaClass && other is Workout &&
                    id == other.id &&
                    dateTime == other.dateTime &&
                    exercises == other.exercises &&
                    isLocked == other.isLocked)

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + dateTime.hashCode()
        result = 31 * result + exercises.hashCode()
        result = 31 * result + isLocked.hashCode()
        return result
    }
}