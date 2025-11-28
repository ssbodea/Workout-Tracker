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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_workout, parent, false)
        return WorkoutViewHolder(view)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = workouts[position]
        setupWorkoutItem(holder, workout, position)
    }

    override fun getItemCount(): Int = workouts.size

    private fun setupWorkoutItem(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        // Set date header
        holder.dateHeader.text = workout.getFormattedDateTime()

        // Setup spinners
        setupSpinners(holder)

        // Setup buttons
        setupButtons(holder, workout, position)

        // Display exercises
        displayExercises(holder, workout)

        // Update UI state based on lock status
        updateLockState(holder, workout, position)
    }

    private fun updateLockButton(holder: WorkoutViewHolder, workout: Workout) {
        val lockIcon = if (workout.isLocked) {
            R.drawable.button_lock
        } else {
            R.drawable.button_lock_open
        }
        holder.lockButton.setImageResource(lockIcon)
    }

    private fun updateLockState(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        // Update lock button icon
        updateLockButton(holder, workout)

        // Enable/disable inputs based on lock state
        val enabled = !workout.isLocked

        holder.muscleGroupSpinner.isEnabled = enabled
        holder.exerciseSpinner.isEnabled = enabled
        holder.repsInput.isEnabled = enabled
        holder.weightInput.isEnabled = enabled
        holder.addSetButton.isEnabled = enabled
        holder.removeSetButton.isEnabled = enabled && workout.exercises.isNotEmpty()

        // Show/hide remove workout button based on lock state and position
        // Can't remove first workout, and can't remove locked workouts
        val canRemove = position > 0 && !workout.isLocked
        holder.removeWorkoutButton.visibility = if (canRemove) View.VISIBLE else View.GONE

        // Add workout button is always enabled (even when locked)
        holder.addWorkoutButton.isEnabled = true

        // Visual feedback for disabled state
        val alpha = if (enabled) 1.0f else 0.5f
        holder.muscleGroupSpinner.alpha = alpha
        holder.exerciseSpinner.alpha = alpha
        holder.repsInput.alpha = alpha
        holder.weightInput.alpha = alpha
        holder.addSetButton.alpha = alpha
        holder.removeSetButton.alpha = alpha
    }

    private fun setupSpinners(holder: WorkoutViewHolder) {
        // Muscle Group Spinner with centered text
        val muscleGroupAdapter = ArrayAdapter(
            holder.itemView.context,
            R.layout.spinner_item_centered,  // Use your custom centered layout
            ExerciseDatabase.muscleGroups
        )
        muscleGroupAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_centered)  // Use your custom dropdown layout
        holder.muscleGroupSpinner.adapter = muscleGroupAdapter

        // Exercise Spinner - will be updated when muscle group changes
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
        val exerciseAdapter = ArrayAdapter(
            holder.itemView.context,
            R.layout.spinner_item_centered,  // Use your custom centered layout
            exercises
        )
        exerciseAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_centered)  // Use your custom dropdown layout
        holder.exerciseSpinner.adapter = exerciseAdapter
    }

    private fun setupButtons(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        // Lock button
        holder.lockButton.setOnClickListener {
            workout.isLocked = !workout.isLocked
            updateLockState(holder, workout, position)
        }

        // Remove workout button
        holder.removeWorkoutButton.setOnClickListener {
            if (!workout.isLocked && position > 0) {
                onWorkoutRemoved?.invoke(position)
            }
        }

        // Add workout button
        holder.addWorkoutButton.setOnClickListener {
            onWorkoutAdded?.invoke()
        }

// Add set button
        holder.addSetButton.setOnClickListener {
            if (workout.isLocked) return@setOnClickListener

            val repetitionsText = holder.repsInput.text.toString()
            if (repetitionsText.isBlank()) {
                holder.repsInput.error = "Reps required"
                return@setOnClickListener
            }

            val repetitions = repetitionsText.toIntOrNull() ?: return@setOnClickListener
            val weightText = holder.weightInput.text.toString()

            // ADD THIS VALIDATION:
            val weight = if (weightText.isNotBlank()) {
                val weightValue = weightText.toIntOrNull() ?: return@setOnClickListener
                if (weightValue <= 0) {
                    holder.weightInput.error = "Weight must be greater than 0"
                    return@setOnClickListener
                }
                weightValue
            } else {
                null
            }

            // Clear any previous errors
            holder.weightInput.error = null

            val muscleGroup = holder.muscleGroupSpinner.selectedItem as String
            val exerciseName = holder.exerciseSpinner.selectedItem as String

            val exerciseSet = ExerciseSet(repetitions, weight)

            // Find existing exercise or create new one
            val existingExercise = workout.exercises.find {
                it.muscleGroup == muscleGroup && it.name == exerciseName
            }

            if (existingExercise != null) {
                existingExercise.sets.add(exerciseSet)
            } else {
                val newExercise = Exercise(muscleGroup, exerciseName, mutableListOf(exerciseSet))
                workout.exercises.add(newExercise)
            }

            // Clear inputs and hide keyboard
            holder.repsInput.text?.clear()
            holder.weightInput.text?.clear()
            holder.repsInput.error = null
            activity.hideKeyboardFromActivity()

            // Update exercise display
            displayExercises(holder, workout)
            updateLockState(holder, workout, position)
        }

        // Remove last set button
        holder.removeSetButton.setOnClickListener {
            if (workout.isLocked) return@setOnClickListener

            if (workout.exercises.isNotEmpty()) {
                val lastExercise = workout.exercises.last()
                if (lastExercise.sets.size > 1) {
                    lastExercise.sets.removeAt(lastExercise.sets.lastIndex)
                } else {
                    workout.exercises.removeAt(workout.exercises.lastIndex)
                }
                displayExercises(holder, workout)
                updateLockState(holder, workout, position)
            }
        }
    }

    private fun displayExercises(holder: WorkoutViewHolder, workout: Workout) {
        if (workout.exercises.isEmpty()) {
            holder.setsDisplay.text = "No exercises recorded"
        } else {
            val exercisesText = workout.exercises.joinToString("\n") { exercise ->
                "${exercise.name}: ${exercise.sets.joinToString(", ") { it.toString() }}"
            }
            holder.setsDisplay.text = exercisesText
        }
    }

    // Callback interfaces to communicate with MainActivity
    var onWorkoutAdded: (() -> Unit)? = null
    var onWorkoutRemoved: ((Int) -> Unit)? = null
}