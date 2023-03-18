package com.sid.todoreminderapp


import com.sid.todoreminderapp.model.Task
import com.sid.todoreminderapp.taskdao.TaskDao
import javax.inject.Inject

class TaskRepository @Inject constructor(private val taskDao: TaskDao) {

//    suspend fun getAllTasks(): List<Task> = taskDao.getAllTasks()

    suspend fun getCurrentDayAndTimeTasks(currentDate : String): List<Task> = taskDao.getCurrentDayAndTimeTasks(currentDate)

    suspend fun insertTask(task: Task) {
        taskDao.insertUser(task)
    }

    suspend fun updateTask(task: Task) = taskDao.updateUser(task)

    suspend fun deleteTask(task: Task) = taskDao.deleteUser(task)

     fun getTasks() = taskDao.getTasks()
}