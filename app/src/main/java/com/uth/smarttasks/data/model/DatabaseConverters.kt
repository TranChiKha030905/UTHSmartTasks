package com.uth.smarttasks.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class DatabaseConverters {
    private val gson = Gson()
    // --- Cho Subtask ---
    @TypeConverter
    fun fromSubtaskList(subtasks: List<Subtask>?): String? {
        return gson.toJson(subtasks)
    }
    @TypeConverter
    fun toSubtaskList(subtasksString: String?): List<Subtask>? {
        if (subtasksString == null) return emptyList()
        val listType = object : TypeToken<List<Subtask>>() {}.type
        return gson.fromJson(subtasksString, listType)
    }
    // --- Cho Attachment ---
    @TypeConverter
    fun fromAttachmentList(attachments: List<Attachment>?): String? {
        return gson.toJson(attachments)
    }
    @TypeConverter
    fun toAttachmentList(attachmentsString: String?): List<Attachment>? {
        if (attachmentsString == null) return emptyList()
        val listType = object : TypeToken<List<Attachment>>() {}.type
        return gson.fromJson(attachmentsString, listType)
    }
}