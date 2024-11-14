package viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import data.AppDatabase
import data.Exercise
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import repository.ExerciseRepository

class ExerciseViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExerciseRepository
    val allExercises: LiveData<List<Exercise>>

    init {
        val exerciseDao = AppDatabase.getDatabase(application).exerciseDao()
        repository = ExerciseRepository(exerciseDao)
        allExercises = repository.allExercises
    }

    fun insert(exercise: Exercise) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insert(exercise)
        }
    }

    fun update(exercise: Exercise) = viewModelScope.launch {
        repository.update(exercise)
    }

    fun delete(exercise: Exercise) = viewModelScope.launch {
        repository.delete(exercise)
    }
}
