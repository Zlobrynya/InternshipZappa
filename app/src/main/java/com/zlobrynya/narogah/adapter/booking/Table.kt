package com.zlobrynya.narogah.adapter.booking

/**
 * Класс данных для адаптера столиков
 */
data class Table(val seatCount: Int, var seatPosition: String?, val seatType: String, val seatId: Int) {
    init {
        // Необходимая проверка на null (в ответе с сервера может прийти null)
        if (seatPosition == null) seatPosition = ""
    }
}