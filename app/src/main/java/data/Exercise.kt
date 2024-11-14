package data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "exercises")
data class Exercise(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val type: String,
    val sets: Int,
    val repetitions: Int,
    val weight: Double,
    val duration: Int? = null,
    val distance: Double? = null,
    val intensity: String? = null
)
