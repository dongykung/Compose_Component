package com.dkproject.compsoe_component.calendar.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlinx.datetime.DayOfWeek

object MyDatePickerDefaults {
    const val DAYS_IN_WEEK = 7
    const val MONTHS_IN_YEAR = 12

    @Composable
    fun colors(
        containerColor: Color = Color.White,
        weekDayDefaultColor: Color = Color.Black,
        weekDaySundayColor: Color = Color(0xFFE53935),
        weekDaySaturdayColor: Color = Color(0xFF1E88E5),
        todayContentColor: Color = Color.Black,
        todayContainerColor: Color = Color(0xFFF5F5F5),
        selectedDayContentColor: Color = Color.White,
        selectedDayContainerColor: Color = Color.Black,
        disabledDayContentColor: Color = Color.LightGray,
        disabledDayContainerColor: Color = Color.Transparent,
        dayInSelectionRangeContentColor: Color = Color.White,
        dayInSelectionRangeContainerColor: Color = Color.Black,
        dayInSelectionRangeBackgroundColor: Color = Color.Black
    ): MyDatePickerColor = MyDatePickerColor(
        containerColor = containerColor,
        weekDayDefaultColor = weekDayDefaultColor,
        weekDaySundayColor = weekDaySundayColor,
        weekDaySaturdayColor = weekDaySaturdayColor,
        todayContentColor = todayContentColor,
        todayContainerColor = todayContainerColor,
        selectedDayContentColor = selectedDayContentColor,
        selectedDayContainerColor = selectedDayContainerColor,
        disabledDayContentColor = disabledDayContentColor,
        disabledDayContainerColor = disabledDayContainerColor,
        dayInSelectionRangeContentColor = dayInSelectionRangeContentColor,
        dayInSelectionRangeContainerColor = dayInSelectionRangeContainerColor,
        dayInSelectionRangeBackgroundColor = dayInSelectionRangeBackgroundColor
    )
}

@Immutable
data class MyDatePickerColor(
    val containerColor: Color,
    val weekDayDefaultColor: Color,
    val weekDaySundayColor: Color,
    val weekDaySaturdayColor: Color,
    val todayContentColor: Color,
    val todayContainerColor: Color,
    val selectedDayContentColor: Color,
    val selectedDayContainerColor: Color,
    val disabledDayContentColor: Color,
    val disabledDayContainerColor: Color,
    val dayInSelectionRangeContentColor: Color,
    val dayInSelectionRangeContainerColor: Color,
    val dayInSelectionRangeBackgroundColor: Color
) {
    @Composable
    fun dayContentColor(
        selected: Boolean,
        inRange: Boolean,
        enabled: Boolean,
        isToday: Boolean,
    ): Color {
        return when {
            !enabled -> disabledDayContentColor
            selected -> selectedDayContentColor
            inRange -> dayInSelectionRangeContentColor
            isToday -> todayContentColor
            else -> weekDayDefaultColor
        }
    }

    @Composable
    fun dayContainerColor(
        selected: Boolean,
        inRange: Boolean,
        enabled: Boolean,
        isToday: Boolean,
    ): Color {
        return when {
            !enabled -> disabledDayContainerColor
            selected -> selectedDayContainerColor
            inRange -> dayInSelectionRangeContainerColor
            isToday -> todayContainerColor
            else -> containerColor
        }
    }
}

fun DayOfWeek.getDayOfWeekColor(color: MyDatePickerColor): Color {
    return when (this) {
        DayOfWeek.SUNDAY -> color.weekDaySundayColor
        DayOfWeek.SATURDAY -> color.weekDaySaturdayColor
        else -> color.weekDayDefaultColor
    }
}