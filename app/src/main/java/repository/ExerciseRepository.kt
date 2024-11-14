package repository

import androidx.lifecycle.LiveData
import data.Exercise
import data.ExerciseDao

class ExerciseRepository(private val exerciseDao: ExerciseDao) {

    val allExercises: LiveData<List<Exercise>> = exerciseDao.getAllExercises()

    suspend fun insert(exercise: Exercise) {
        exerciseDao.insert(exercise)
    }

    suspend fun update(exercise: Exercise) {
        exerciseDao.update(exercise)
    }

    suspend fun delete(exercise: Exercise) {
        exerciseDao.delete(exercise)
    }
}
