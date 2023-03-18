package com.sid.todoreminderapp.model
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "tasks")
data class Task (
    @PrimaryKey
    var taskId : Long = System.currentTimeMillis(),
    var taskName :String,
    var taskPriority : TaskPriority,
    var taskDescription: String?=null,
    var taskDueDate : String,
    var taskDueTime : String
) : Serializable
