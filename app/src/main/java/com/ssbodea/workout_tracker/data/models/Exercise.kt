package com.ssbodea.workout_tracker.data.models

data class Exercise(
    val muscleGroup: String,
    val name: String,
    private val _sets: MutableList<ExerciseSet> = mutableListOf()
) {
    val sets: List<ExerciseSet>
        get() = _sets.toList()

    fun addSet(exerciseSet: ExerciseSet) {
        _sets.add(exerciseSet)
    }

    fun removeLastSet(): Boolean {
        return if (_sets.isNotEmpty()) {
            _sets.removeAt(_sets.size - 1)
            true
        } else {
            false
        }
    }

    fun clearSets() {
        _sets.clear()
    }
}