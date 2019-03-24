package com.zlobrynya.internshipzappa.util

import android.os.Parcelable
import com.zlobrynya.internshipzappa.tools.retrofit.DTOs.bookingDTOs.tableList
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Модель для передачи tableList через аргументы
 */
@Parcelize
data class TableParceling(val tableList: @RawValue tableList) : Parcelable