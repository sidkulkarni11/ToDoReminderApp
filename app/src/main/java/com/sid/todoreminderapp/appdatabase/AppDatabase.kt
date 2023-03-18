package com.sid.todoreminderapp.appdatabase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.sid.todoreminderapp.model.Task
import com.sid.todoreminderapp.taskdao.TaskDao


@Database(entities = [Task::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao() : TaskDao
}