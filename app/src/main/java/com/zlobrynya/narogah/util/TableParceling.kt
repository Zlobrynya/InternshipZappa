package com.zlobrynya.narogah.util

import android.os.Parcelable
import com.zlobrynya.narogah.tools.retrofit.DTOs.bookingDTOs.tableList
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * Модель для передачи tableList через аргументы
 */
@Parcelize
data class TableParceling(val tableList: @RawValue tableList) : Parcelable