package com.sid.todoreminderapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
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
import com.sid.todoreminderapp.model.Task
import com.sid.todoreminderapp.model.TaskPriority
import com.sid.todoreminderapp.ui.theme.ToDoReminderAppTheme
import dagger.hilt.android.AndroidEntryPoint
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




    }

    @Composable
    fun ToAddTile(task: Task) {
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
                        text = task.taskName,
                        modifier = Modifier
                            .weight(3f)
                            .height(30.dp)
                    )

                    Text(
                        text = getTaskPriority(taskPriority = task.taskPriority),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                    )

                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    DescriptiondropdownButton(task.taskDescription)
                }
                Row(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = task.taskDueDate,
                        style = TextStyle(
                            textAlign = TextAlign.Start
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp)
                    )
                    Text(
                        text = task.taskDueTime,
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

                        }) {
                        Text(text = "Edit")
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(10.dp),
                        onClick = {
                            taskViewModel.deleteUser(task)
                        }) {
                        Text(text = "Delete")
                    }
                }
            }
        }
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
        var taskNameText by remember { mutableStateOf(TextFieldValue()) }
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

        var taskDescriptionText by remember {
            mutableStateOf(
                TextFieldValue()
            )
        }
        val mytask by taskViewModel.getTasks().collectAsState(initial = emptyList())

        Box(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                items(mytask) { tas ->
                    ToAddTile(tas)
                }
            }

            FloatingActionButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp),
                onClick = {
                    showDialog = true
                },
                backgroundColor = Color.Red,
                contentColor = Color.White,
                content = {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            )

            if (showDialog) {

                AlertDialog(
                    onDismissRequest = { showDialog = false },
                    title = { Text(text = "Alert") },
                    text = {

                        Column {

                            TextField(
                                value = taskNameText,
                                onValueChange = { taskNameText = it },
                                label = { Text(text = "Task Name") }
                            )

                            TextField(
                                value = taskDescriptionText,
                                onValueChange = { taskDescriptionText = it },
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

                                val mDatePickerDialog = DatePickerDialog(
                                    mContext,
                                    { _: DatePicker, mYear: Int, mMonth: Int, mDayOfMonth: Int ->
                                        mDate.value = "$mDayOfMonth-${mMonth + 1}-$mYear"
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
                                taskreminderDate = mDate.value

                                Text(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(10.dp),
                                    textAlign = TextAlign.Center,
                                    text = mDate.value
                                )
                            }



                            Row {
                                val mContext = LocalContext.current

                                // Declaring and initializing a calendar
                                val mCalendar = Calendar.getInstance()
                                val mHour = mCalendar[Calendar.HOUR_OF_DAY]
                                val mMinute = mCalendar[Calendar.MINUTE]

                                // Value for storing time as a string
                                val mTime = remember { mutableStateOf("") }

                                // Creating a TimePicker dialod
                                val mTimePickerDialog = TimePickerDialog(
                                    mContext,
                                    { _, mHour: Int, mMinute: Int ->
                                        mTime.value = "$mHour:$mMinute"
                                    }, mHour, mMinute, false
                                )


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
                                taskreminderTime = mTime.value

                                Text(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .padding(10.dp),
                                    textAlign = TextAlign.Center,
                                    text = mTime.value
                                )
                            }

                        }

                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                var taskname = taskNameText.text
                                var taskDescription = taskDescriptionText.text
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

                                                    taskViewModel.insertTask(saveTask)

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
}