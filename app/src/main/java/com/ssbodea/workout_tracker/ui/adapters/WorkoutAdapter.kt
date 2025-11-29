package com.ssbodea.workout_tracker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.ssbodea.workout_tracker.MainActivity
import com.ssbodea.workout_tracker.data.database.ExerciseDatabase
import com.ssbodea.workout_tracker.data.models.Exercise
import com.ssbodea.workout_tracker.data.models.ExerciseSet
import com.ssbodea.workout_tracker.data.models.Workout
import com.ssbodea.workout_tracker.R

class WorkoutAdapter(
    private val workouts: MutableList<Workout>,
    private val activity: MainActivity
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    companion object {
        private const val ALPHA_ENABLED = 1.0f
        private const val ALPHA_DISABLED = 0.5f
        private const val NO_EXERCISES_TEXT = "No exercises recorded"
    }

    inner class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val dateHeader: TextView = itemView.findViewById(R.id.dateHeader)
        val lockButton: ImageButton = itemView.findViewById(R.id.lockButton)
        val removeWorkoutButton: ImageButton = itemView.findViewById(R.id.removeWorkoutButton)
        val addWorkoutButton: ImageButton = itemView.findViewById(R.id.addWorkoutButton)
        val muscleGroupSpinner: Spinner = itemView.findViewById(R.id.muscleGroupSpinner)
        val exerciseSpinner: Spinner = itemView.findViewById(R.id.exerciseSpinner)
        val repsInput: EditText = itemView.findViewById(R.id.repsInput)
        val weightInput: EditText = itemView.findViewById(R.id.weightInput)
        val removeSetButton: ImageButton = itemView.findViewById(R.id.removeSetButton)
        val addSetButton: ImageButton = itemView.findViewById(R.id.addSetButton)
        val setsDisplay: TextView = itemView.findViewById(R.id.setsDisplay)
    }

    var onWorkoutAdded: (() -> Unit)? = null
    var onWorkoutRemoved: ((Int) -> Unit)? = null
    var onDataChanged: (() -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        if (position !in workouts.indices) return
        val workout = workouts[position]
        setupWorkoutItem(holder, workout, position)
    }

    override fun getItemCount(): Int = workouts.size

    private fun setupWorkoutItem(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        holder.dateHeader.text = workout.getFormattedDateTime()
        setupSpinners(holder)
        setupButtons(holder, workout, position)
        displayExercises(holder, workout)
        updateLockState(holder, workout, position)
        updateAddButtonState(holder, position)
    }

    private fun updateAddButtonState(holder: WorkoutViewHolder, position: Int) {
        if (position !in workouts.indices) return

        val isLastWorkout = position == workouts.lastIndex
        val hasExercises = workouts[position].exercises.isNotEmpty()

        holder.addWorkoutButton.visibility = if (isLastWorkout) View.VISIBLE else View.GONE
        holder.addWorkoutButton.isEnabled = hasExercises
        holder.addWorkoutButton.alpha = if (hasExercises) ALPHA_ENABLED else ALPHA_DISABLED
    }

    private fun updateLockState(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        val lockIcon = if (workout.isLocked) R.drawable.button_lock else R.drawable.button_lock_open
        holder.lockButton.setImageResource(lockIcon)

        val enabled = !workout.isLocked
        listOf(
            holder.muscleGroupSpinner, holder.exerciseSpinner, holder.repsInput,
            holder.weightInput, holder.addSetButton, holder.removeSetButton
        ).forEach { element ->
            element.isEnabled = enabled
            element.alpha = if (enabled) ALPHA_ENABLED else ALPHA_DISABLED
        }

        val canRemove = position > 0 && !workout.isLocked
        holder.removeWorkoutButton.visibility = if (canRemove) View.VISIBLE else View.GONE
    }

    private fun setupSpinners(holder: WorkoutViewHolder) {
        val muscleGroupAdapter = ArrayAdapter(holder.itemView.context, R.layout.spinner_item_centered, ExerciseDatabase.muscleGroups)
        muscleGroupAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_centered)
        holder.muscleGroupSpinner.adapter = muscleGroupAdapter

        ExerciseDatabase.muscleGroups.firstOrNull()?.let {
            updateExerciseSpinner(holder, it)
        }

        holder.muscleGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                (parent?.getItemAtPosition(position) as? String)?.let {
                    updateExerciseSpinner(holder, it)
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateExerciseSpinner(holder: WorkoutViewHolder, muscleGroup: String) {
        val exercises = ExerciseDatabase.getExercisesForMuscleGroup(muscleGroup)
        val exerciseAdapter = ArrayAdapter(holder.itemView.context, R.layout.spinner_item_centered, exercises)
        exerciseAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_centered)
        holder.exerciseSpinner.adapter = exerciseAdapter
    }

    private fun setupButtons(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        setupLockButton(holder, workout, position)
        setupRemoveWorkoutButton(holder, workout, position)
        setupAddWorkoutButton(holder, position)
        setupAddSetButton(holder, workout, position)
        setupRemoveSetButton(holder, workout, position)
    }

    private fun setupLockButton(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        holder.lockButton.setOnClickListener {
            workout.isLocked = !workout.isLocked
            updateLockState(holder, workout, position)
            updateAddButtonState(holder, position)
            onDataChanged?.invoke()
        }
    }

    private fun setupRemoveWorkoutButton(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        holder.removeWorkoutButton.setOnClickListener {
            if (!workout.isLocked && position > 0) {
                onWorkoutRemoved?.invoke(position)
            }
        }
    }

    private fun setupAddWorkoutButton(holder: WorkoutViewHolder, position: Int) {
        holder.addWorkoutButton.setOnClickListener {
            if (position !in workouts.indices) return@setOnClickListener

            val isLastWorkout = position == workouts.lastIndex
            val hasExercises = workouts[position].exercises.isNotEmpty()

            when {
                isLastWorkout && hasExercises -> onWorkoutAdded?.invoke()
                !hasExercises -> Toast.makeText(activity, "Add exercises to current workout before creating a new one", Toast.LENGTH_SHORT).show()
            }
            onDataChanged?.invoke()
        }
    }

    private fun setupAddSetButton(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        holder.addSetButton.setOnClickListener {
            if (workout.isLocked) return@setOnClickListener

            val muscleGroup = holder.muscleGroupSpinner.selectedItem as? String ?: return@setOnClickListener
            val exerciseName = holder.exerciseSpinner.selectedItem as? String ?: return@setOnClickListener

            validateInputs(holder)?.let { (repetitions, weight) ->
                addExerciseSet(workout, muscleGroup, exerciseName, repetitions, weight)
                clearInputs(holder)
                activity.hideKeyboardFromActivity()
                displayExercises(holder, workout)
                updateAddButtonState(holder, position)
                onDataChanged?.invoke()
            }
        }
    }

    private fun setupRemoveSetButton(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        holder.removeSetButton.setOnClickListener {
            if (workout.isLocked) return@setOnClickListener

            val muscleGroup = holder.muscleGroupSpinner.selectedItem as? String ?: return@setOnClickListener
            val exerciseName = holder.exerciseSpinner.selectedItem as? String ?: return@setOnClickListener

            removeExerciseSet(workout, muscleGroup, exerciseName)
            displayExercises(holder, workout)
            updateAddButtonState(holder, position)
            onDataChanged?.invoke()
        }
    }

    private fun validateInputs(holder: WorkoutViewHolder): Pair<Int, Int?>? {
        val repsText = holder.repsInput.text.toString()
        val weightText = holder.weightInput.text.toString()

        if (repsText.isBlank()) {
            holder.repsInput.error = "Reps required"
            return null
        }

        val repetitions = repsText.toIntOrNull()?.takeIf { it > 0 } ?: run {
            holder.repsInput.error = "Invalid reps"
            return null
        }

        val weight = weightText.takeIf { it.isNotBlank() }?.toIntOrNull()?.takeIf { it > 0 } ?: run {
            if (weightText.isNotBlank()) holder.weightInput.error = "Invalid weight"
            null
        }

        holder.repsInput.error = null
        holder.weightInput.error = null
        return Pair(repetitions, weight)
    }

    private fun addExerciseSet(workout: Workout, muscleGroup: String, exerciseName: String, repetitions: Int, weight: Int?) {
        val exerciseSet = ExerciseSet(repetitions, weight)
        val existingExercise = workout.exercises.find { it.muscleGroup == muscleGroup && it.name == exerciseName }

        if (existingExercise != null) {
            existingExercise.addSet(exerciseSet)
        } else {
            workout.exercises.add(Exercise(muscleGroup, exerciseName).apply { addSet(exerciseSet) })
        }
    }

    private fun removeExerciseSet(workout: Workout, muscleGroup: String, exerciseName: String) {
        val selectedExercise = workout.exercises.find { it.muscleGroup == muscleGroup && it.name == exerciseName }

        if (selectedExercise != null) {
            val success = selectedExercise.removeLastSet()
            if (!success || selectedExercise.sets.isEmpty()) {
                workout.exercises.remove(selectedExercise)
            }
        } else {
            Toast.makeText(activity, "No sets to remove for selected exercise", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputs(holder: WorkoutViewHolder) {
        holder.repsInput.text?.clear()
        holder.weightInput.text?.clear()
        holder.repsInput.error = null
        holder.weightInput.error = null
    }

    private fun displayExercises(holder: WorkoutViewHolder, workout: Workout) {
        holder.setsDisplay.text = if (workout.exercises.isEmpty()) {
            NO_EXERCISES_TEXT
        } else {
            workout.exercises.joinToString("\n") { exercise ->
                "${exercise.name}: ${exercise.sets.joinToString(", ") { it.toString() }}"
            }
        }
    }

    fun notifyWorkoutAdded(oldLastPosition: Int) {
        if (oldLastPosition >= 0) notifyItemChanged(oldLastPosition)
        notifyItemInserted(workouts.lastIndex)
    }

    fun notifyWorkoutStructureChanged(removedPosition: Int, wasLastOrSecondLast: Boolean) {
        notifyItemRemoved(removedPosition)
        when {
            wasLastOrSecondLast -> workouts.lastIndex.takeIf { it >= 0 }?.let { notifyItemChanged(it) }
            removedPosition < workouts.size -> notifyItemRangeChanged(removedPosition, workouts.size - removedPosition)
        }
    }
}