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

    companion object {
        private const val SHARED_PREFS_NAME = "workout_data"
        private const val WORKOUTS_KEY = "workouts"
        private const val NEXT_WORKOUT_ID_KEY = "next_workout_id"
        private const val INITIAL_WORKOUT_ID = 1
    }

    private lateinit var recyclerViewWorkouts: RecyclerView
    private val workouts = mutableListOf<Workout>()
    private lateinit var workoutAdapter: WorkoutAdapter
    private val gson = Gson()
    private var shouldAutoScroll = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        loadWorkoutsFromStorage()
        setupWorkoutList()
    }

    override fun onResume() {
        super.onResume()
        if (shouldAutoScroll) {
            scrollToLatestWorkout()
        }
    }

    override fun onPause() {
        super.onPause()
        saveWorkoutsToStorage()
    }

    private fun initializeViews() {
        recyclerViewWorkouts = findViewById(R.id.recyclerViewWorkouts)
    }

    private fun setupWorkoutList() {
        if (workouts.isEmpty()) {
            addInitialWorkout()
        }

        workoutAdapter = WorkoutAdapter(workouts, this)
        setupAdapterCallbacks()
        setupRecyclerView()

        if (shouldAutoScroll) {
            scrollToLatestWorkout()
        }
    }

    private fun addInitialWorkout() {
        workouts.add(Workout(id = generateNextWorkoutId()))
    }

    private fun setupAdapterCallbacks() {
        workoutAdapter.onWorkoutAdded = {
            addNewWorkout()
        }

        workoutAdapter.onWorkoutRemoved = { position ->
            removeWorkout(position)
        }

        workoutAdapter.onDataChanged = {
            saveWorkoutsToStorage()
        }
    }

    private fun setupRecyclerView() {
        recyclerViewWorkouts.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = workoutAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy < 0) {
                        shouldAutoScroll = false
                    } else if (!canScrollVertically(1)) {
                        shouldAutoScroll = true
                    }
                }

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)
                    if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
                        shouldAutoScroll = true
                    }
                }
            })
        }
    }

    fun addNewWorkout() {
        val newWorkout = Workout(id = generateNextWorkoutId())
        workouts.add(newWorkout)
        workoutAdapter.notifyItemInserted(workouts.size - 1)
        saveWorkoutsToStorage()
        scrollToLatestWorkout()
        shouldAutoScroll = true
    }

    private fun generateNextWorkoutId(): Int {
        val sharedPref = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        val nextId = sharedPref.getInt(NEXT_WORKOUT_ID_KEY, INITIAL_WORKOUT_ID)
        sharedPref.edit().putInt(NEXT_WORKOUT_ID_KEY, nextId + 1).apply()
        return nextId
    }

    private fun scrollToLatestWorkout() {
        if (workouts.isNotEmpty()) {
            recyclerViewWorkouts.smoothScrollToPosition(workouts.size - 1)
        }
    }

    fun removeWorkout(position: Int) {
        if (canRemoveWorkout(position)) {
            workouts.removeAt(position)
            // Update the adapter with the new list and notify about the change
            workoutAdapter.updateWorkouts(workouts.toMutableList())
            saveWorkoutsToStorage()

            if (position <= workouts.size) { // Check if we still have workouts
                scrollToLatestWorkout()
            }
        }
    }

    private fun canRemoveWorkout(position: Int): Boolean {
        return workouts.size > 1 && position > 0 && position < workouts.size
    }

    private fun saveWorkoutsToStorage() {
        val sharedPref = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        val jsonString = workoutsToJson(workouts)
        sharedPref.edit().putString(WORKOUTS_KEY, jsonString).apply()
    }

    private fun loadWorkoutsFromStorage() {
        val sharedPref = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE)
        val jsonString = sharedPref.getString(WORKOUTS_KEY, null)
        if (jsonString != null) {
            val loadedWorkouts = jsonToWorkouts(jsonString)
            workouts.clear()
            workouts.addAll(loadedWorkouts)
        }
    }

    private fun workoutsToJson(workouts: List<Workout>): String {
        return gson.toJson(workouts)
    }

    private fun jsonToWorkouts(jsonString: String): List<Workout> {
        return try {
            val type = object : TypeToken<List<Workout>>() {}.type
            gson.fromJson<List<Workout>>(jsonString, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun hideKeyboardFromActivity() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        currentFocus?.windowToken?.let { windowToken ->
            imm.hideSoftInputFromWindow(windowToken, 0)
        }
    }
}