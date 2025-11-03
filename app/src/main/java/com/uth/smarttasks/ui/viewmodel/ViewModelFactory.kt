package com.uth.smarttasks.ui.viewmodel

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.uth.smarttasks.data.repository.TaskRepository

// "Nhà máy" này biết cách tạo ra TẤT CẢ các ViewModel
class ViewModelFactory(private val repository: TaskRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TaskListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskListViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(TaskDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TaskDetailViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(CreateTaskViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CreateTaskViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(CalendarViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CalendarViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(StatisticsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StatisticsViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(ProjectViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ProjectViewModel(repository) as T
        }
        // AuthViewModel không cần Repository, nên ta không thêm vào đây
        // Mày có thể thêm AuthViewModel vào đây nếu muốn, nhưng nó không cần Repository

        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

// HÀM HELPER ĐỂ GỌI VIEWMODEL TRONG UI
// Mày không dùng hàm này, nhưng tao để đây cho đầy đủ
@Composable
inline fun <reified T : ViewModel> viewModelWithFactory(
    factory: ViewModelProvider.Factory
): T {
    return viewModel(factory = factory)
}