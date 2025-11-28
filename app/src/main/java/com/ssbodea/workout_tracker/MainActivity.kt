package com.ssbodea.workout_tracker

import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ssbodea.workout_tracker.data.models.Workout
import com.ssbodea.workout_tracker.ui.adapters.WorkoutAdapter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewWorkouts: RecyclerView
    private val workouts = mutableListOf<Workout>()
    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        loadWorkoutsFromStorage() // Load existing data
        setupWorkoutList()
    }

    private fun initializeViews() {
        recyclerViewWorkouts = findViewById(R.id.recyclerViewWorkouts)
    }

    private fun setupWorkoutList() {
        // If no workouts loaded, add initial workout
        if (workouts.isEmpty()) {
            workouts.add(Workout(id = 1))
        }

        workoutAdapter = WorkoutAdapter(workouts, this)

        // Set up callbacks
        workoutAdapter.onWorkoutAdded = {
            addNewWorkout()
        }
        workoutAdapter.onWorkoutRemoved = { position ->
            removeWorkout(position)
        }
        // ADD THIS: Save whenever any data changes
        workoutAdapter.onDataChanged = {
            saveWorkoutsToStorage()
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
        saveWorkoutsToStorage() // Also save when adding new workout

        // Scroll to the new workout
        recyclerViewWorkouts.smoothScrollToPosition(workouts.size - 1)
    }

    fun removeWorkout(position: Int) {
        if (workouts.size > 1 && position > 0) {
            workouts.removeAt(position)
            workoutAdapter.notifyItemRemoved(position)
            saveWorkoutsToStorage() // Also save when removing workout
        }
    }

    // ADD THESE METHODS FOR LOCAL STORAGE:

    private fun saveWorkoutsToStorage() {
        // Convert workouts to JSON string and save to SharedPreferences
        val sharedPref = getSharedPreferences("workout_data", MODE_PRIVATE)
        val jsonString = workoutsToJson(workouts) // You'll need to implement this
        sharedPref.edit().putString("workouts", jsonString).apply()
    }

    private fun loadWorkoutsFromStorage() {
        val sharedPref = getSharedPreferences("workout_data", MODE_PRIVATE)
        val jsonString = sharedPref.getString("workouts", null)
        if (jsonString != null) {
            val loadedWorkouts = jsonToWorkouts(jsonString) // You'll need to implement this
            workouts.clear()
            workouts.addAll(loadedWorkouts)
        }
    }

    fun hideKeyboardFromActivity() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
    }
    private fun workoutsToJson(workouts: List<Workout>): String {
        return Gson().toJson(workouts)
    }

    private fun jsonToWorkouts(jsonString: String): List<Workout> {
        val type = object : TypeToken<List<Workout>>() {}.type
        return Gson().fromJson(jsonString, type) ?: emptyList()
    }
}