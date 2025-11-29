package com.ssbodea.workout_tracker.data.models

data class Exercise(
    val muscleGroup: String,
    val name: String,
    private val _sets: MutableList<ExerciseSet> = mutableListOf()
) {
    val sets: List<ExerciseSet> get() = _sets.toList()

    fun addSet(exerciseSet: ExerciseSet) = _sets.add(exerciseSet)

    fun removeLastSet(): Boolean = _sets.isNotEmpty().also {
        if (it) _sets.removeAt(_sets.size - 1)
    }

    fun clearSets() = _sets.clear()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        other as Exercise
        return muscleGroup == other.muscleGroup &&
                name == other.name &&
                _sets == other._sets
    }

    override fun hashCode(): Int {
        var result = muscleGroup.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + _sets.hashCode()
        return result
    }
}