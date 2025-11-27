package com.ssbodea.workout_tracker

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssbodea.workout_tracker.data.models.Workout
import com.ssbodea.workout_tracker.ui.adapters.WorkoutAdapter

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewWorkouts: RecyclerView
    private val workouts = mutableListOf<Workout>()
    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupWorkoutList()
    }

    private fun initializeViews() {
        recyclerViewWorkouts = findViewById(R.id.recyclerViewWorkouts)
    }

    private fun setupWorkoutList() {
        // Add initial workout
        workouts.add(Workout(id = 1))

        workoutAdapter = WorkoutAdapter(workouts, this)

        // Set up callbacks
        workoutAdapter.onWorkoutAdded = {
            addNewWorkout()
        }
        workoutAdapter.onWorkoutRemoved = { position ->
            removeWorkout(position)
        }

        recyclerViewWorkouts.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = workoutAdapter
        }
    }

    fun addNewWorkout() {
        val newWorkout = Workout(id = workouts.size + 1)
        workouts.add(newWorkout)
        workoutAdapter.notifyItemInserted(workouts.size - 1)

        // Scroll to the new workout
        recyclerViewWorkouts.smoothScrollToPosition(workouts.size - 1)
    }

    fun removeWorkout(position: Int) {
        if (workouts.size > 1 && position > 0) {
            workouts.removeAt(position)
            workoutAdapter.notifyItemRemoved(position)
        }
    }

    fun hideKeyboardFromActivity() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
}