package ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import data.Exercise
import org.json.JSONObject
import sara.trachys.R
import ui.AddNewExerciseActivity
import viewmodel.ExerciseViewModel
import java.io.IOException

class AddExerciseActivity : AppCompatActivity() {

    private val exerciseViewModel: ExerciseViewModel by viewModels()
    private lateinit var spinnerCategory: Spinner
    private lateinit var spinnerExercise: Spinner
    private val REQUEST_CODE_NEW_EXERCISE = 1

    private val customExercises = mutableMapOf<String, List<String>>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_exercise)

        spinnerCategory = findViewById(R.id.spinner_category)
        spinnerExercise = findViewById(R.id.spinner_exercise)
        val buttonAddNewExercise = findViewById<Button>(R.id.button_add_new_exercise)
        val buttonSaveExercise = findViewById<Button>(R.id.button_save_exercise)

        // Cargar categorías desde el JSON
        val categories = loadCategoriesFromJson()
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = categoryAdapter

        // Actualizar el spinner de ejercicios al seleccionar una categoría
        spinnerCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedCategory = parent.getItemAtPosition(position).toString()
                val exercises = loadExercisesForCategory(selectedCategory)

                val exerciseAdapter = ArrayAdapter(this@AddExerciseActivity, android.R.layout.simple_spinner_item, exercises)
                exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerExercise.adapter = exerciseAdapter
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Mostrar los campos del ejercicio seleccionado
        spinnerExercise.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedExercise = parent.getItemAtPosition(position).toString()
                val fields = loadFieldsForExercise(spinnerCategory.selectedItem.toString(), selectedExercise)
                showFields(fields)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Abrir la actividad para añadir un nuevo ejercicio
        buttonAddNewExercise.setOnClickListener {
            val intent = Intent(this, AddNewExerciseActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_NEW_EXERCISE)
        }

        // Guardar el ejercicio
        buttonSaveExercise.setOnClickListener {
            val name = spinnerExercise.selectedItem?.toString() ?: ""
            val type = spinnerCategory.selectedItem.toString()
            val fields = loadFieldsForExercise(type, name)

            val sets = if ("sets" in fields) findViewById<EditText>(R.id.edit_text_sets).text.toString().toIntOrNull() ?: 0 else 0
            val reps = if ("repeticiones" in fields) findViewById<EditText>(R.id.edit_text_reps).text.toString().toIntOrNull() ?: 0 else 0
            val weight = if ("peso" in fields) findViewById<EditText>(R.id.edit_text_weight).text.toString().toDoubleOrNull() ?: 0.0 else 0.0
            val duration = if ("duracion" in fields) findViewById<EditText>(R.id.edit_text_duration).text.toString().toIntOrNull() ?: 0 else 0
            val distance = if ("distancia" in fields) findViewById<EditText>(R.id.edit_text_distance).text.toString().toDoubleOrNull() ?: 0.0 else 0.0
            val intensity = if ("intensidad" in fields) findViewById<EditText>(R.id.edit_text_intensity).text.toString() else ""

            if (name.isNotEmpty() && type.isNotEmpty()) {
                val exercise = Exercise(
                    name = name,
                    type = type,
                    sets = sets,
                    repetitions = reps,
                    weight = weight,
                    duration = duration,
                    distance = distance,
                    intensity = intensity
                )
                exerciseViewModel.insert(exercise)
                Toast.makeText(this, "Ejercicio guardado", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, "Completa todos los campos necesarios", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_NEW_EXERCISE && resultCode == RESULT_OK) {
            data?.let {
                val exerciseName = it.getStringExtra("exerciseName") ?: return
                val fields = it.getStringArrayListExtra("fields") ?: return

                // Guardar el ejercicio personalizado en el mapa temporal
                customExercises[exerciseName] = fields

                // Actualizar la lista de ejercicios en la categoría seleccionada
                val exerciseList = loadExercisesForCategory(spinnerCategory.selectedItem.toString()).toMutableList()
                exerciseList.add(exerciseName)

                // Actualizar el spinner de ejercicios con el nuevo ejercicio
                val exerciseAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, exerciseList)
                exerciseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spinnerExercise.adapter = exerciseAdapter
                spinnerExercise.setSelection(exerciseList.indexOf(exerciseName))
            }
        }
    }

    private fun loadCategoriesFromJson(): List<String> {
        val categoryList = mutableListOf<String>()
        try {
            val inputStream = assets.open("categories.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            val jsonObject = JSONObject(jsonString)
            val categories = jsonObject.getJSONArray("categories")
            for (i in 0 until categories.length()) {
                val category = categories.getJSONObject(i)
                categoryList.add(category.getString("name"))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return categoryList
    }

    private fun loadExercisesForCategory(categoryName: String): List<String> {
        val exerciseList = mutableListOf<String>()
        try {
            val inputStream = assets.open("categories.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            val jsonObject = JSONObject(jsonString)
            val categories = jsonObject.getJSONArray("categories")
            for (i in 0 until categories.length()) {
                val category = categories.getJSONObject(i)
                if (category.getString("name") == categoryName) {
                    val exercises = category.getJSONArray("exercises")
                    for (j in 0 until exercises.length()) {
                        val exercise = exercises.getJSONObject(j)
                        exerciseList.add(exercise.getString("name"))
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return exerciseList
    }

    private fun loadFieldsForExercise(categoryName: String, exerciseName: String): List<String> {
        // Si el ejercicio está en los ejercicios personalizados, devuelve sus fields
        if (customExercises.containsKey(exerciseName)) {
            return customExercises[exerciseName] ?: listOf()
        }

        // Si no es un ejercicio personalizado, cargar fields del archivo JSON
        val fieldsList = mutableListOf<String>()
        try {
            val inputStream = assets.open("categories.json")
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, Charsets.UTF_8)
            val jsonObject = JSONObject(jsonString)
            val categories = jsonObject.getJSONArray("categories")
            for (i in 0 until categories.length()) {
                val category = categories.getJSONObject(i)
                if (category.getString("name") == categoryName) {
                    val exercises = category.getJSONArray("exercises")
                    for (j in 0 until exercises.length()) {
                        val exercise = exercises.getJSONObject(j)
                        if (exercise.getString("name") == exerciseName) {
                            val fields = exercise.getJSONArray("fields")
                            for (k in 0 until fields.length()) {
                                fieldsList.add(fields.getString(k))
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return fieldsList
    }


    private fun showFields(fields: List<String>) {
        // Ocultar todos los campos primero
        findViewById<EditText>(R.id.edit_text_sets).visibility = View.GONE
        findViewById<EditText>(R.id.edit_text_reps).visibility = View.GONE
        findViewById<EditText>(R.id.edit_text_weight).visibility = View.GONE
        findViewById<EditText>(R.id.edit_text_duration).visibility = View.GONE
        findViewById<EditText>(R.id.edit_text_distance).visibility = View.GONE
        findViewById<EditText>(R.id.edit_text_intensity).visibility = View.GONE

        // Mostrar solo los campos especificados en "fields"
        if ("sets" in fields) findViewById<EditText>(R.id.edit_text_sets).visibility = View.VISIBLE
        if ("repeticiones" in fields) findViewById<EditText>(R.id.edit_text_reps).visibility = View.VISIBLE
        if ("peso" in fields) findViewById<EditText>(R.id.edit_text_weight).visibility = View.VISIBLE
        if ("duracion" in fields) findViewById<EditText>(R.id.edit_text_duration).visibility = View.VISIBLE
        if ("distancia" in fields) findViewById<EditText>(R.id.edit_text_distance).visibility = View.VISIBLE
        if ("intensidad" in fields) findViewById<EditText>(R.id.edit_text_intensity).visibility = View.VISIBLE
    }
}
