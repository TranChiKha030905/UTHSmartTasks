package com.uth.smarttasks.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.uth.smarttasks.SmartTasksApplication
import com.uth.smarttasks.ui.screens.list.TaskItem
import com.uth.smarttasks.ui.viewmodel.CalendarViewModel
import com.uth.smarttasks.ui.viewmodel.TaskListViewModel
import com.uth.smarttasks.ui.viewmodel.ViewModelFactory
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    navController: NavController,
) {
    // --- SỬA CÁCH GỌI VIEWMODEL ---
    val application = LocalContext.current.applicationContext as SmartTasksApplication
    val calendarViewModel: CalendarViewModel = viewModel(
        factory = ViewModelFactory(application.taskRepository)
    )
    val taskListViewModel: TaskListViewModel = viewModel(
        factory = ViewModelFactory(application.taskRepository)
    )

    val uiState by calendarViewModel.uiState.collectAsState()
    val context = LocalContext.current

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val calendarState = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Calendar", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HorizontalCalendar(
                modifier = Modifier.padding(horizontal = 16.dp),
                state = calendarState,
                monthHeader = { monthState ->
                    Text(
                        text = monthState.yearMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 16.dp)
                    )
                },
                dayContent = { day ->
                    DayCell(
                        day = day,
                        isSelected = uiState.selectedDate == day.date,
                        hasTask = uiState.scheduledTasks.containsKey(day.date),
                        onClick = {
                            calendarViewModel.selectDate(it.date)
                        }
                    )
                }
            )

            DaysOfWeekTitle(daysOfWeek = firstDayOfWeek.getDaysOfWeek())
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                text = "Tasks for ${uiState.selectedDate.format(DateTimeFormatter.ofPattern("dd MMM"))}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            if (uiState.isLoading) {
                CircularProgressIndicator()
            } else if (uiState.selectedDateTasks.isEmpty()) {
                Text(text = "No tasks due on this date.")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.selectedDateTasks) { task ->
                        TaskItem(
                            task = task,
                            onItemClick = { },
                            onCheckedChange = {
                                taskListViewModel.toggleTaskStatus(task)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun DayCell(
    day: CalendarDay,
    isSelected: Boolean,
    hasTask: Boolean,
    onClick: (CalendarDay) -> Unit
) {
    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(
                color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
            )
            .clickable { onClick(day) },
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface
            )
            if (hasTask && !isSelected) {
                Box(
                    modifier = Modifier
                        .size(4.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                )
            }
        }
    }
}

@Composable
fun DaysOfWeekTitle(daysOfWeek: List<DayOfWeek>) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
    ) {
        for (dayOfWeek in daysOfWeek) {
            Text(
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center,
                text = dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

private fun DayOfWeek.getDaysOfWeek(): List<DayOfWeek> {
    return DayOfWeek.entries.run {
        val firstDay = this@getDaysOfWeek
        slice(indexOf(firstDay)..size - 1) + slice(0..indexOf(firstDay) - 1)
    }
}