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

    // Add equals and hashCode for proper list operations
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Exercise

        if (muscleGroup != other.muscleGroup) return false
        if (name != other.name) return false
        if (_sets != other._sets) return false

        return true
    }

    override fun hashCode(): Int {
        var result = muscleGroup.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + _sets.hashCode()
        return result
    }
}