package kore.example.todo

import java.util.*

data class Task(
    var isCompleted: Boolean = false,
    val title: String,
    val createdAt: Date? = null
)
