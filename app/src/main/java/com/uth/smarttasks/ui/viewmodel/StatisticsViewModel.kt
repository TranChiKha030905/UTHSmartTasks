package com.uth.smarttasks.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.uth.smarttasks.data.model.Task
import com.uth.smarttasks.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class StatsData(
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val inProgressTasks: Int = 0,
    val pendingTasks: Int = 0,
    val byPriority: Map<String, Int> = emptyMap(),
    val byCategory: Map<String, Int> = emptyMap()
)

data class StatsUiState(
    val isLoading: Boolean = false,
    val stats: StatsData = StatsData(),
    val error: String? = null
)

// Constructor đã nhận Repository
class StatisticsViewModel(private val repository: TaskRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(StatsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadStats()
    }

    fun loadStats() {
        _uiState.value = StatsUiState(isLoading = true)
        viewModelScope.launch {
            // Logic đã lắng nghe từ Repository
            repository.getTasks().collectLatest { tasks ->
                val stats = processTasks(tasks)
                _uiState.value = StatsUiState(stats = stats)
            }
        }
    }

    private fun processTasks(tasks: List<Task>): StatsData {
        val total = tasks.size
        val completed = tasks.count { it.status.equals("completed", ignoreCase = true) }
        val inProgress = tasks.count { it.status.equals("in progress", ignoreCase = true) }
        val pending = tasks.count { it.status.equals("pending", ignoreCase = true) }

        val byPriority = tasks
            .groupBy { it.priority.ifEmpty { "N/A" } }
            .mapValues { it.value.size }

        val byCategory = tasks
            .groupBy { it.category.ifEmpty { "N/A" } }
            .mapValues { it.value.size }

        return StatsData(
            totalTasks = total,
            completedTasks = completed,
            inProgressTasks = inProgress,
            pendingTasks = pending,
            byPriority = byPriority,
            byCategory = byCategory
        )
    }
}