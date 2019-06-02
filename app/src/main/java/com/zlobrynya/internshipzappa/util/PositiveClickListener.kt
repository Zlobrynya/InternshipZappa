package com.zlobrynya.internshipzappa.util

/**
 * Интерфейс для "позитивной" кнопки кастомного таймпикера
 */
interface PositiveClickListener {
    fun onClick(hours: String, minutes: String)
}