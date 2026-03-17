package com.dkproject.compsoe_component.calendar.state

import androidx.compose.material3.CalendarLocale
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.DayOfWeek
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.isoDayNumber
import kotlinx.datetime.minus
import kotlinx.datetime.plus
import kotlinx.datetime.todayIn
import java.time.format.TextStyle
import java.util.Locale
import kotlin.collections.plus
import kotlin.time.Clock

@Immutable
data class MyDatePickerModel(
    val locale: CalendarLocale,
    val colors: MyDatePickerColor
) {
    val allWeekNames = DayOfWeek.entries.map {
        it.displayName(locale = locale) to it.getDayOfWeekColor(colors)
    }.let { it.takeLast(1) + it.dropLast(1) }.toImmutableList()

    fun getMonth(
        displayedMonth: LocalDate,
        selectedDate: LocalDate? = null,
        selectedEndDate: LocalDate? = null,
    ): MyCalendarMonth {
        val firstDayOfMonth = LocalDate(displayedMonth.year, displayedMonth.month, 1)
        val startOffset = firstDayOfMonth.dayOfWeek.isoDayNumber % 7
        val today = Clock.System.todayIn(TimeZone.currentSystemDefault())

        return MyCalendarMonth(
            createDays(
                startOffset = startOffset,
                currentLocalDate = firstDayOfMonth,
                today = today,
                selectedDate = selectedDate,
                selectedEndDate = selectedEndDate
            )
        )
    }

    fun createDays(
        startOffset: Int,
        currentLocalDate: LocalDate,
        today: LocalDate,
        selectedDate: LocalDate?,
        selectedEndDate: LocalDate?,
    ): ImmutableList<MyCalendarDay> {
        val daysInMonthCount = daysInMonth(currentLocalDate)
        val totalCells = startOffset + daysInMonthCount
        val trailingEmptyDays = if (totalCells % 7 == 0) 0 else 7 - (totalCells % 7)
        val startDate = currentLocalDate.minus(startOffset, DateTimeUnit.DAY)
        val days = List(totalCells + trailingEmptyDays) { index ->
            val date = startDate.plus(index, DateTimeUnit.DAY)
            MyCalendarDay(
                date = date,
                isToday = date == today,
                isEnabled = date.month == currentLocalDate.month && today <= date,
                inRange = selectedDate != null && selectedEndDate != null && date in selectedDate..selectedEndDate,
                isSelected = date == selectedDate || date == selectedEndDate,
            )
        }
        return days.toImmutableList()
    }


    private fun daysInMonth(date: LocalDate): Int {
        val firstDay = LocalDate(date.year, date.month, 1)
        return firstDay.plus(1, DateTimeUnit.MONTH)
            .minus(1, DateTimeUnit.DAY)
            .day
    }
}

fun DayOfWeek.displayName(
    style: TextStyle = TextStyle.NARROW,
    locale: CalendarLocale = Locale.getDefault()
): String = java.time.DayOfWeek.of(this.isoDayNumber)
    .getDisplayName(style, locale)

@Immutable
data class MyCalendarMonth(
    val days: ImmutableList<MyCalendarDay>
) {
    val totalWeekCount = days.size / 7
}

@Immutable
data class MyCalendarDay(
    val date: LocalDate,
    val isToday: Boolean,
    val isEnabled: Boolean,
    val inRange: Boolean,
    val isSelected: Boolean
)