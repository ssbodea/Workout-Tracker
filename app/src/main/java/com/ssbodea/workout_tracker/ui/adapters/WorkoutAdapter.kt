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

    private fun notifyDataChanged() {
        onDataChanged?.invoke()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
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
    }

    private fun updateLockButton(holder: WorkoutViewHolder, workout: Workout) {
        val lockIcon = if (workout.isLocked) R.drawable.button_lock else R.drawable.button_lock_open
        holder.lockButton.setImageResource(lockIcon)
    }

    private fun updateLockState(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        updateLockButton(holder, workout)
        val enabled = !workout.isLocked

        val uiElements = listOf(
            holder.muscleGroupSpinner,
            holder.exerciseSpinner,
            holder.repsInput,
            holder.weightInput,
            holder.addSetButton,
            holder.removeSetButton
        )

        uiElements.forEach { element ->
            element.isEnabled = enabled
            element.alpha = if (enabled) ALPHA_ENABLED else ALPHA_DISABLED
        }

        val canRemove = position > 0 && !workout.isLocked
        holder.removeWorkoutButton.visibility = if (canRemove) View.VISIBLE else View.GONE
        holder.addWorkoutButton.isEnabled = true
    }

    private fun setupSpinners(holder: WorkoutViewHolder) {
        val muscleGroupAdapter = ArrayAdapter(holder.itemView.context, R.layout.spinner_item_centered, ExerciseDatabase.muscleGroups)
        muscleGroupAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_centered)
        holder.muscleGroupSpinner.adapter = muscleGroupAdapter

        updateExerciseSpinner(holder, ExerciseDatabase.muscleGroups.first())

        holder.muscleGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedMuscleGroup = parent?.getItemAtPosition(position) as String
                updateExerciseSpinner(holder, selectedMuscleGroup)
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
        setupAddWorkoutButton(holder)
        setupAddSetButton(holder, workout, position)
        setupRemoveSetButton(holder, workout, position)
    }

    private fun setupLockButton(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        holder.lockButton.setOnClickListener {
            workout.isLocked = !workout.isLocked
            updateLockState(holder, workout, position)
            notifyDataChanged()
        }
    }

    private fun setupRemoveWorkoutButton(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        holder.removeWorkoutButton.setOnClickListener {
            if (!workout.isLocked && position > 0) {
                onWorkoutRemoved?.invoke(position)
                notifyDataChanged()
            }
        }
    }

    private fun setupAddWorkoutButton(holder: WorkoutViewHolder) {
        holder.addWorkoutButton.setOnClickListener {
            onWorkoutAdded?.invoke()
            notifyDataChanged()
        }
    }

    private fun setupAddSetButton(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        holder.addSetButton.setOnClickListener {
            if (workout.isLocked) return@setOnClickListener

            val repetitionsText = holder.repsInput.text.toString()
            val weightText = holder.weightInput.text.toString()

            val validatedInput = validateInputs(repetitionsText, weightText, holder) ?: return@setOnClickListener
            val (repetitions, weight) = validatedInput

            val muscleGroup = holder.muscleGroupSpinner.selectedItem as String
            val exerciseName = holder.exerciseSpinner.selectedItem as String

            addExerciseSet(workout, muscleGroup, exerciseName, repetitions, weight)

            clearInputs(holder)
            activity.hideKeyboardFromActivity()
            displayExercises(holder, workout)
            updateLockState(holder, workout, position)
            notifyDataChanged()
        }
    }

    private fun setupRemoveSetButton(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        holder.removeSetButton.setOnClickListener {
            if (workout.isLocked) return@setOnClickListener

            val muscleGroup = holder.muscleGroupSpinner.selectedItem as String
            val exerciseName = holder.exerciseSpinner.selectedItem as String

            removeExerciseSet(workout, muscleGroup, exerciseName)
            displayExercises(holder, workout)
            updateLockState(holder, workout, position)
            notifyDataChanged()
        }
    }

    private fun validateInputs(repsText: String, weightText: String, holder: WorkoutViewHolder): Pair<Int, Int?>? {
        if (repsText.isBlank()) {
            holder.repsInput.error = "Reps required"
            return null
        }

        val repetitions = repsText.toIntOrNull() ?: run {
            holder.repsInput.error = "Invalid reps"
            return null
        }

        if (repetitions <= 0) {
            holder.repsInput.error = "Reps must be greater than 0"
            return null
        }

        val weight = if (weightText.isNotBlank()) {
            val weightValue = weightText.toIntOrNull() ?: run {
                holder.weightInput.error = "Invalid weight"
                return null
            }
            if (weightValue <= 0) {
                holder.weightInput.error = "Weight must be greater than 0"
                return null
            }
            weightValue
        } else {
            null
        }

        holder.weightInput.error = null
        return Pair(repetitions, weight)
    }

    private fun addExerciseSet(workout: Workout, muscleGroup: String, exerciseName: String, repetitions: Int, weight: Int?) {
        val exerciseSet = ExerciseSet(repetitions, weight)
        val existingExercise = workout.exercises.find { it.muscleGroup == muscleGroup && it.name == exerciseName }

        if (existingExercise != null) {
            existingExercise.addSet(exerciseSet)
        } else {
            val newExercise = Exercise(muscleGroup, exerciseName).apply { addSet(exerciseSet) }
            workout.exercises.add(newExercise)
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
        if (workout.exercises.isEmpty()) {
            holder.setsDisplay.text = NO_EXERCISES_TEXT
        } else {
            val exercisesText = workout.exercises.joinToString("\n") { exercise ->
                "${exercise.name}: ${exercise.sets.joinToString(", ") { it.toString() }}"
            }
            holder.setsDisplay.text = exercisesText
        }
    }
}