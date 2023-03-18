package com.sid.todoreminderapp.taskdao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.sid.todoreminderapp.model.Task
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    /*@Query("SELECT * FROM tasks")
    suspend fun getAllTasks() : List<Task>*/

    @Query("SELECT * FROM tasks WHERE taskDueDate = :currentDate")
    suspend fun getCurrentDayAndTimeTasks(currentDate : String) : List<Task>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(task: Task)

    @Update
    suspend fun updateUser(task: Task)

    @Delete
    suspend fun deleteUser(task: Task)

    @Query("SELECT * FROM tasks")
    fun getTasks() : Flow<List<Task>>
}