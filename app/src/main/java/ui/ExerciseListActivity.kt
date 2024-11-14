package ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import sara.trachys.R
import viewmodel.ExerciseViewModel

class ExerciseListActivity : AppCompatActivity() {

    private val exerciseViewModel: ExerciseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_exercise_list)

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view_exercises)
        val fabAddExercise = findViewById<FloatingActionButton>(R.id.fab_add_exercise)

        recyclerView.layoutManager = LinearLayoutManager(this)

        exerciseViewModel.allExercises.observe(this) { exercises ->
            recyclerView.adapter = ExerciseAdapter(exercises)
        }

        fabAddExercise.setOnClickListener {
            val intent = Intent(this, AddExerciseActivity::class.java)
            startActivity(intent)
        }
    }
}
