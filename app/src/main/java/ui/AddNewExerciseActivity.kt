package ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import sara.trachys.R

class AddNewExerciseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_exercise)

        val editTextNewExerciseName = findViewById<EditText>(R.id.edit_text_new_exercise_name)
        val checkBoxSets = findViewById<CheckBox>(R.id.checkbox_sets)
        val checkBoxRepetitions = findViewById<CheckBox>(R.id.checkbox_repetitions)
        val checkBoxWeight = findViewById<CheckBox>(R.id.checkbox_weight)
        val checkBoxDuration = findViewById<CheckBox>(R.id.checkbox_duration)
        val checkBoxDistance = findViewById<CheckBox>(R.id.checkbox_distance)
        val checkBoxIntensity = findViewById<CheckBox>(R.id.checkbox_intensity)
        val buttonSaveNewExercise = findViewById<Button>(R.id.button_save_new_exercise)

        buttonSaveNewExercise.setOnClickListener {
            val exerciseName = editTextNewExerciseName.text.toString()
            val fields = mutableListOf<String>()

            if (checkBoxSets.isChecked) fields.add("sets")
            if (checkBoxRepetitions.isChecked) fields.add("repeticiones")
            if (checkBoxWeight.isChecked) fields.add("peso")
            if (checkBoxDuration.isChecked) fields.add("duracion")
            if (checkBoxDistance.isChecked) fields.add("distancia")
            if (checkBoxIntensity.isChecked) fields.add("intensidad")

            if (exerciseName.isNotEmpty() && fields.isNotEmpty()) {
                val resultIntent = Intent().apply {
                    putExtra("exerciseName", exerciseName)
                    putStringArrayListExtra("fields", ArrayList(fields))
                }
                setResult(RESULT_OK, resultIntent)
                finish()
            } else {
                Toast.makeText(this, "Completa el nombre y selecciona al menos un campo", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
