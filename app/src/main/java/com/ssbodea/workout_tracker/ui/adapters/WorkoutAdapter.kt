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
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val ALPHA_ENABLED = 1.0f
        private const val ALPHA_DISABLED = 0.5f
        private const val NO_EXERCISES_TEXT = "No exercises recorded"
        private const val VIEW_TYPE_WORKOUT = 0
        private const val VIEW_TYPE_EMPTY_SPACE = 1
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

    inner class EmptySpaceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        init {
            val screenHeight = itemView.context.resources.displayMetrics.heightPixels
            itemView.layoutParams.height = screenHeight + 100
        }
    }

    var onWorkoutAdded: (() -> Unit)? = null
    var onWorkoutRemoved: ((Int) -> Unit)? = null
    var onDataChanged: (() -> Unit)? = null

    override fun getItemViewType(position: Int) = if (position < workouts.size) VIEW_TYPE_WORKOUT else VIEW_TYPE_EMPTY_SPACE

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_WORKOUT) {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_workout, parent, false)
            WorkoutViewHolder(view)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_empty_space, parent, false)
            EmptySpaceViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is WorkoutViewHolder && position < workouts.size) {
            setupWorkoutItem(holder, workouts[position], position)
        }
    }

    override fun getItemCount() = workouts.size + 1

    private fun setupWorkoutItem(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        holder.dateHeader.text = workout.getFormattedDateTime()
        setupSpinners(holder)
        setupInputListeners(holder, position)
        setupButtons(holder, workout, position)
        displayExercises(holder, workout)
        updateLockState(holder, workout, position)
        updateAddButtonState(holder, position)
    }

    private fun setupInputListeners(holder: WorkoutViewHolder, position: Int) {
        val scrollToTop = { activity.scrollItemToTop(position) }

        val focusListener = View.OnFocusChangeListener { _, hasFocus -> if (hasFocus) scrollToTop() }
        holder.repsInput.setOnFocusChangeListener(focusListener)
        holder.weightInput.setOnFocusChangeListener(focusListener)

        holder.repsInput.setOnClickListener { scrollToTop() }
        holder.weightInput.setOnClickListener { scrollToTop() }
    }

    private fun updateAddButtonState(holder: WorkoutViewHolder, position: Int) {
        val isLast = position == workouts.lastIndex
        val hasExercises = workouts[position].exercises.isNotEmpty()

        holder.addWorkoutButton.visibility = if (isLast) View.VISIBLE else View.GONE
        holder.addWorkoutButton.isEnabled = hasExercises
        holder.addWorkoutButton.alpha = if (hasExercises) ALPHA_ENABLED else ALPHA_DISABLED
    }

    private fun updateLockState(holder: WorkoutViewHolder, workout: Workout, position: Int) {
        holder.lockButton.setImageResource(if (workout.isLocked) R.drawable.button_lock else R.drawable.button_lock_open)

        val enabled = !workout.isLocked
        val elements = listOf(
            holder.muscleGroupSpinner, holder.exerciseSpinner, holder.repsInput,
            holder.weightInput, holder.addSetButton, holder.removeSetButton
        )
        elements.forEach {
            it.isEnabled = enabled
            it.alpha = if (enabled) ALPHA_ENABLED else ALPHA_DISABLED
        }

        val canRemove = position > 0 && !workout.isLocked
        holder.removeWorkoutButton.visibility = if (canRemove) View.VISIBLE else View.GONE
    }

    private fun setupSpinners(holder: WorkoutViewHolder) {
        val muscleGroupAdapter = ArrayAdapter(holder.itemView.context, R.layout.spinner_item_centered, ExerciseDatabase.muscleGroups)
        muscleGroupAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item_centered)
        holder.muscleGroupSpinner.adapter = muscleGroupAdapter

        ExerciseDatabase.muscleGroups.firstOrNull()?.let { updateExerciseSpinner(holder, it) }

        holder.muscleGroupSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                (parent?.getItemAtPosition(pos) as? String)?.let { updateExerciseSpinner(holder, it) }
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
        holder.lockButton.setOnClickListener {
            workout.isLocked = !workout.isLocked
            updateLockState(holder, workout, position)
            updateAddButtonState(holder, position)
            onDataChanged?.invoke()
        }

        holder.removeWorkoutButton.setOnClickListener {
            if (!workout.isLocked && position > 0) onWorkoutRemoved?.invoke(position)
        }

        holder.addWorkoutButton.setOnClickListener {
            if (position !in workouts.indices) return@setOnClickListener

            val isLast = position == workouts.lastIndex
            val hasExercises = workouts[position].exercises.isNotEmpty()

            when {
                isLast && hasExercises -> onWorkoutAdded?.invoke()
                !hasExercises -> Toast.makeText(activity, "Add exercises to current workout before creating a new one", Toast.LENGTH_SHORT).show()
            }
            onDataChanged?.invoke()
        }

        holder.addSetButton.setOnClickListener {
            if (workout.isLocked) return@setOnClickListener

            val muscleGroup = holder.muscleGroupSpinner.selectedItem as? String ?: return@setOnClickListener
            val exerciseName = holder.exerciseSpinner.selectedItem as? String ?: return@setOnClickListener

            validateInputs(holder)?.let { (reps, weight) ->
                addExerciseSet(workout, muscleGroup, exerciseName, reps, weight)
                clearInputsAndFocus(holder)
                activity.hideKeyboardFromActivity()
                displayExercises(holder, workout)
                updateAddButtonState(holder, position)
                onDataChanged?.invoke()
            }
        }

        holder.removeSetButton.setOnClickListener {
            if (workout.isLocked) return@setOnClickListener

            val muscleGroup = holder.muscleGroupSpinner.selectedItem as? String ?: return@setOnClickListener
            val exerciseName = holder.exerciseSpinner.selectedItem as? String ?: return@setOnClickListener

            removeExerciseSet(workout, muscleGroup, exerciseName)
            clearInputsAndFocus(holder)
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

        val weight = weightText.takeIf { it.isNotBlank() }?.toIntOrNull()?.takeIf { it > 0 }
        if (weightText.isNotBlank() && weight == null) holder.weightInput.error = "Invalid weight"

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

    private fun clearInputsAndFocus(holder: WorkoutViewHolder) {
        holder.repsInput.text?.clear()
        holder.weightInput.text?.clear()
        holder.repsInput.error = null
        holder.weightInput.error = null
        holder.repsInput.clearFocus()
        holder.weightInput.clearFocus()
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
        notifyItemChanged(workouts.size)
    }

    fun notifyWorkoutStructureChanged(removedPosition: Int, wasLastOrSecondLast: Boolean) {
        notifyItemRemoved(removedPosition)
        notifyItemChanged(workouts.size)
        when {
            wasLastOrSecondLast -> workouts.lastIndex.takeIf { it >= 0 }?.let { notifyItemChanged(it) }
            removedPosition < workouts.size -> notifyItemRangeChanged(removedPosition, workouts.size - removedPosition)
        }
    }
}