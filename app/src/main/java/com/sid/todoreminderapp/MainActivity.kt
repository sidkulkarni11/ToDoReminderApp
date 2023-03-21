package com.sid.todoreminderapp

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sid.toappdaggerhilt.TaskViewModel
import com.sid.todoreminderapp.model.DialogState
import com.sid.todoreminderapp.model.Task
import com.sid.todoreminderapp.model.TaskPriority
import com.sid.todoreminderapp.ui.theme.ToDoReminderAppTheme
import dagger.hilt.android.AndroidEntryPoint
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoReminderAppTheme {
                Scaffold(
                    content = {

                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            color = MaterialTheme.colors.background
                        ) {
                            DetailsContent()
                        }
                    }
                )

            }
        }

        var checkTask = Task(
            System.currentTimeMillis(),
            "CheckTask",
            getTaskPriority(true),
            "Check Desc",
            "21-03-2023",
            "15 : 32"
        )
//        setAlarm(checkTask, this)
//        testNotif(this@MainActivity, checkTask)


    }
    fun testNotif(context: Context, task: Task) {
        val alarmIntent = Intent(context, TaskBroadcastReceiver::class.java)

        val bundle = Bundle().apply {
            putSerializable("taskobj", task)
        }
        alarmIntent.putExtra("bundle", bundle)

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.taskId.toInt(),
            alarmIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val manager =
            context.getSystemService(ALARM_SERVICE) as? AlarmManager
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = System.currentTimeMillis()

        var taskDateString = task.taskDueDate + " " + task.taskDueTime
        var taskDate = stringToDate(taskDateString)

        var year = Integer.parseInt(dateToString(taskDate, "yyyy"))
        var month = Integer.parseInt(dateToString(taskDate, "MM"))
        var day = Integer.parseInt(dateToString(taskDate, "dd"))
        var hour = Integer.parseInt(dateToString(taskDate, "HH"))
        var min = Integer.parseInt(dateToString(taskDate, "mm"))
        calendar[Calendar.YEAR] = year
        calendar[Calendar.MONTH] = month-1
        calendar[Calendar.DATE] = day
        calendar[Calendar.HOUR_OF_DAY] = hour
        calendar[Calendar.MINUTE] = min
        calendar[Calendar.SECOND] = 0

        manager!!.setExact(
            AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent
        )
    }

    @Composable
    fun ToAddTile(task: Task) {

    }


    @Composable
    fun DescriptiondropdownButton(taskDescription: String?) {
        Column(modifier = Modifier.wrapContentWidth()) {
            var isExpanded by remember { mutableStateOf(false) }

            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut()
            ) {
                if (taskDescription != null) {
                    Text(
                        text = taskDescription
                    )
                }
            }
            IconButton(
                onClick = {
                    run { isExpanded = !isExpanded }
                },
                modifier = Modifier.size(24.dp)
            ) {
                if (isExpanded)
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "Drop-down button"
                    )
                else
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Drop-down button"
                    )
            }
        }

    }


    @Preview(showBackground = true)
    @Composable
    fun DefaultPreview() {
        ToDoReminderAppTheme() {
//        ToAddTile("Android")
        }
    }

    fun getTaskPriority(taskPriority: TaskPriority): String {
        if (taskPriority.equals(TaskPriority.HIGHPRORITY))
            return "High"
        return "Low"
    }


    @Composable
    fun DetailsContent() {
        var showDialog by remember { mutableStateOf(false) }
        var dialogState by remember { mutableStateOf(DialogState.CREATE) }
        var currentTask by remember { mutableStateOf(Task()) }

        val mytask by taskViewModel.getTasks().collectAsState(initial = emptyList())

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                items(mytask) { tas ->
                    Card(
                        backgroundColor = Color.White,
                        elevation = 4.dp,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .wrapContentHeight()
                            .padding(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(16.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = tas.taskName,
                                    modifier = Modifier
                                        .weight(3f)
                                        .height(30.dp)
                                )

                                Text(
                                    text = getTaskPriority(taskPriority = tas.taskPriority),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(30.dp)
                                )

                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                DescriptiondropdownButton(tas.taskDescription)
                            }
                            Row(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = tas.taskDueDate,
                                    style = TextStyle(
                                        textAlign = TextAlign.Start
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(30.dp)
                                )
                                Text(
                                    text = tas.taskDueTime,
                                    style = TextStyle(
                                        textAlign = TextAlign.End
                                    ),
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(30.dp)
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(10.dp),
                                    onClick = {
                                        showDialog = true
                                        currentTask = tas
                                        dialogState = DialogState.EDIT
                                    }) {
                                    Text(text = "Edit")
                                }
                                Button(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(10.dp),
                                    onClick = {
                                        taskViewModel.deleteUser(tas)
                                    }) {
                                    Text(text = "Delete")
                                }
                            }
                        }
                    }
                }
            }

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp),
                onClick = {
                    showDialog = true
                    dialogState = DialogState.CREATE
//                    TaskBroadcastReceiver.sendNotf(this@MainActivity, Task())
                },
                backgroundColor = Color.Red,
                contentColor = Color.White,
                content = {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            )

            if (showDialog) {
                var taskNameText by remember { mutableStateOf(TextFieldValue()) }
                var tName by remember { mutableStateOf("") }
                var tDescription by remember { mutableStateOf("") }
                var isChecked by remember { mutableStateOf(false) }
                var taskreminderDate by remember {
                    mutableStateOf("")
                }
                var taskreminderTime by remember {
                    mutableStateOf("")
                }

                var errorMessage by remember {
                    mutableStateOf("")
                }

                val mTime = remember { mutableStateOf("") }

                var taskDescriptionText by remember {
                    mutableStateOf(
                        TextFieldValue()
                    )
                }
                if (dialogState.equals(DialogState.EDIT)) {
                    tName = currentTask.taskName
                    tDescription = getTaskDescription(currentTask.taskDescription)
                    isChecked = getTaskPriorityValue(currentTask.taskPriority)
                    taskreminderDate = currentTask.taskDueDate
                    taskreminderTime = currentTask.taskDueTime
                } else {
                    currentTask = Task()
                }


                var alertDialogContext = LocalContext.current
                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Alert") },
                    text = {

                        Column {

                            TextField(
                                value = tName,
                                onValueChange = { tName = it },
                                label = { Text(text = "Task Name") }
                            )

                            TextField(
                                value = tDescription,
                                onValueChange = { tDescription = it },
                                label = { Text(text = "Task Description") }
                            )

                            Row() {
                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(10.dp),
                                    textAlign = TextAlign.Center,
                                    fontSize = 15.sp,
                                    text = "Priority"
                                )
                                Switch(
                                    modifier = Modifier.weight(1f),
                                    checked = isChecked,
                                    onCheckedChange = { isChecked = it },
                                    colors = SwitchDefaults.colors(checkedThumbColor = MaterialTheme.colors.primary),
                                )
                            }
                            Row {


                                val mContext = LocalContext.current

                                val mYear: Int
                                val mMonth: Int
                                val mDay: Int

                                val mCalendar = Calendar.getInstance()

                                mYear = mCalendar.get(Calendar.YEAR)
                                mMonth = mCalendar.get(Calendar.MONTH)
                                mDay = mCalendar.get(Calendar.DAY_OF_MONTH)

                                mCalendar.time = Date()

                                val mDate = remember { mutableStateOf("") }

                                /* if(dialogState.equals(DialogState.EDIT)){
                                     mDate.value = currentTask.taskDueDate
                                 }*/

                                val mDatePickerDialog = DatePickerDialog(
                                    mContext,
                                    { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
//                                        mDate.value = "$mDayOfMonth-${mMonth + 1}-$mYear"
                                        var cal = Calendar.getInstance()
                                        cal[Calendar.YEAR] = mYear
                                        cal[Calendar.MONTH] = mMonth
                                        cal[Calendar.DAY_OF_MONTH] = mDayOfMonth

                                        var calDate = calendarToDate(cal)
                                        var dateString = dateToString(calDate, "dd-MM-yyyy")
                                        mDate.value = dateString
                                        taskreminderDate = mDate.value
                                        if (dialogState.equals(DialogState.EDIT))
                                            currentTask.taskDueDate = dateString
                                    }, mYear, mMonth, mDay
                                )



                                Button(
                                    onClick = {
                                        mDatePickerDialog.show()
                                    },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(text = "Reminder date")
                                }


                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(10.dp),
                                    textAlign = TextAlign.Center,
                                    text = taskreminderDate
                                )
                            }



                            Row {
                                val mContext = LocalContext.current

                                // Declaring and initializing a calendar
                                val mCalendar = Calendar.getInstance()
                                val mHour = mCalendar[Calendar.HOUR_OF_DAY]
                                val mMinute = mCalendar[Calendar.MINUTE]

                                // Value for storing time as a string

                                // Creating a TimePicker dialod
                                val mTimePickerDialog = TimePickerDialog(
                                    mContext,
                                    { _, mHour: Int, mMinute: Int ->
                                        var cal = Calendar.getInstance()
                                        cal[Calendar.HOUR_OF_DAY] = mHour
                                        cal[Calendar.MINUTE] = mMinute

                                        var calTime = calendarToDate(cal)
                                        var calString = dateToString(calTime, "HH : mm")
                                        mTime.value = calString
                                        taskreminderTime = mTime.value

                                    }, mHour, mMinute, false
                                )

                                if (dialogState.equals(DialogState.EDIT)) {
                                    currentTask.taskDueTime = mTime.value
                                }

                                Button(
                                    onClick = {
                                        mTimePickerDialog.show()

                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 5.dp)
                                        .weight(1f)
                                ) {
                                    Text(text = "Reminder time")
                                }

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(10.dp),
                                    textAlign = TextAlign.Center,
                                    text = taskreminderTime
                                )
                            }

                        }

                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                var taskname = tName
                                var taskDescription = tDescription
                                var taskPriority = getTaskPriority(isChecked)
                                var reminderDate = taskreminderDate
                                var reminderTime = taskreminderTime

                                if (!taskname.isNullOrEmpty()) {
                                    if (!taskDescription.isNullOrEmpty()) {
                                        if (taskPriority != null) {
                                            if (!reminderDate.isNullOrEmpty()) {
                                                if (!reminderTime.isNullOrEmpty()) {
                                                    var saveTask = Task(
                                                        taskName = taskname,
                                                        taskDescription = taskDescription,
                                                        taskPriority = taskPriority,
                                                        taskDueDate = reminderDate,
                                                        taskDueTime = taskreminderTime
                                                    )
                                                    if (dialogState.equals(DialogState.CREATE))
                                                        taskViewModel.insertTask(saveTask)
                                                    else {
                                                        saveTask.taskId = currentTask.taskId
                                                        taskViewModel.updateUser(saveTask)
                                                    }

                                                    testNotif(alertDialogContext,saveTask)
                                                    /*setAlarm(
                                                        task = saveTask,
                                                        context = alertDialogContext
                                                    )*/
                                                } else {
                                                    errorMessage = "Please enter reminder time"
                                                }
                                            } else {
                                                errorMessage = "Please enter reminder date"
                                            }
                                        } else {
                                            errorMessage = "Please choose task prority"
                                        }
                                    } else {
                                        errorMessage = "Please enter Task Description"
                                    }
                                } else {
                                    errorMessage = "Please enter Task name"
                                }

                                showDialog = false
                            }
                        ) {
                            Text(text = "Confirm")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showDialog = false
                            }
                        ) {
                            Text(text = "Dismiss")
                        }
                    },
                    backgroundColor = Color.White,
                )

                showToast(errorMessage)
            }
        }
    }



    fun getTaskPriority(isChecked: Boolean): TaskPriority {
        if (isChecked) {
            return TaskPriority.HIGHPRORITY
        } else {
            return TaskPriority.LOWPRORITY
        }
    }


    @Composable
    fun showToast(message: String) {
        val context = LocalContext.current
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(context: Context, task: Task) {
        val channelId = task.taskId.toString()
        val channelName = task.taskName
        val channelDesc = task.taskDescription
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = channelDesc
        }
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        notificationManager.createNotificationChannel(channel)
    }


    fun setNotificationAlarm(task: Task, context: Context) {
        /*val dateFormat = "yyyy-MM-dd HH:mm:ss"
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())
        val date = formatter.parse(dateString)

        val alarmId = task.taskId.toInt()

        val alarmIntent = Intent(context, TaskBroadcastReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(context, alarmId, alarmIntent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_MUTABLE)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime.timeInMillis, pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context)
        }*/
    }
}

fun calendarToDate(calendar: Calendar): Date {
    return calendar.time
}

fun dateToString(date: Date, dateformat: String): String {
    val formatter = SimpleDateFormat(dateformat, Locale.getDefault())
    return formatter.format(date)
}

fun stringToDate(dateString: String): Date {
    val format = SimpleDateFormat("dd-MM-yyyy HH : mm")
    format.timeZone = TimeZone.getDefault()
    val date = format.parse(dateString)
    return date
}



fun getTaskDescription(taskDescription: String?): String {
    if (taskDescription.isNullOrEmpty()) {
        return ""
    }
    return taskDescription
}

fun getTaskPriorityValue(taskPriority: TaskPriority): Boolean {
    if (taskPriority.equals(TaskPriority.HIGHPRORITY)) {
        return true
    }
    return false
}
/*
fun setAlarm(task: Task, context: Context) {

    */
/* val `when` = System.currentTimeMillis() + 10000L

     val am = getSystemService(ALARM_SERVICE) as AlarmManager
     val intent = Intent(this, TaskBroadcastReceiver::class.java)
     intent.putExtra("myAction", "mDoNotify")
     val pendingIntent2 = PendingIntent.getBroadcast(this, task.taskId.toInt(), intent, PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_UPDATE_CURRENT)
     am[AlarmManager.RTC_WAKEUP, `when`] = pendingIntent2*//*


    var taskDateString = task.taskDueDate + " " + task.taskDueTime
    var taskDate = stringToDate(taskDateString)

    val taskCalendar = Calendar.getInstance()
    var taskDateS = dateToString(taskDate, "dd-MM-yyyy HH:mm")
//        Log.d("****Date",taskDateS)
//        var context = LocalContext.current


    taskCalendar.timeInMillis = System.currentTimeMillis()
    var year = Integer.parseInt(dateToString(taskDate, "yyyy"))
    var month = Integer.parseInt(dateToString(taskDate, "MM"))
    var day = Integer.parseInt(dateToString(taskDate, "dd"))
    var hour = Integer.parseInt(dateToString(taskDate, "HH"))
    var min = Integer.parseInt(dateToString(taskDate, "mm"))

    taskCalendar[Calendar.YEAR] = year
    taskCalendar[Calendar.MONTH] = month
    taskCalendar[Calendar.DAY_OF_MONTH] = day
    taskCalendar[Calendar.HOUR_OF_DAY] = hour
    taskCalendar[Calendar.MINUTE] = min
    taskCalendar[Calendar.SECOND] = 0

    taskCalendar[Calendar.HOUR_OF_DAY] = 19
    taskCalendar[Calendar.MINUTE] = 24
    taskCalendar[Calendar.SECOND] = 30

    taskCalendar.set(hour, min, 0)
//        var str = dateToString(calendarToDate(taskCalendar),"dd-MM-yyyy HH:mm:ss")
    val alarmManager =
        context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager
    val alarmIntent = Intent(context, TaskBroadcastReceiver::class.java)

    val bundle = Bundle().apply {
        putSerializable("taskobj", task)
    }
    alarmIntent.putExtra("bundle", bundle)

    val pendingIntent =
        PendingIntent.getBroadcast(
            context, task.taskId.toInt(), alarmIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

    if (pendingIntent != null && alarmManager != null) {
        alarmManager.cancel(pendingIntent)
    }



    alarmManager?.setExact(AlarmManager.RTC_WAKEUP, taskCalendar.timeInMillis, pendingIntent)
//        alarmManager?.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendingIntent)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        createNotificationChannel(context, task = task)
    }
}*/
