package com.sid.toappdaggerhilt

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.sid.todoreminderapp.TaskRepository
import com.sid.todoreminderapp.model.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskViewModel @Inject constructor(private val taskRepository: TaskRepository) : ViewModel() {
    /*fun getAllTasks(): LiveData<List<Task>> {
        return liveData {
            emit(taskRepository.getAllTasks())
        }
    }*/

    fun getCurrentDayAndTimeTasks(currentDate: String): LiveData<List<Task>> {
        return liveData {
            emit(taskRepository.getCurrentDayAndTimeTasks(currentDate))
        }
    }

    fun insertTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task)
        }
    }

    fun updateUser(task: Task) {
        viewModelScope.launch {
            taskRepository.updateTask(task)
        }
    }

    fun deleteUser(task: Task) {
        viewModelScope.launch {
            taskRepository.deleteTask(task)
        }
    }

    fun getTasks(): Flow<List<Task>> {
        return taskRepository.getTasks()
    }
}