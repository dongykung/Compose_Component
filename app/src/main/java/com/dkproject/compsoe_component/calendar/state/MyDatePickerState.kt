package com.dkproject.compsoe_component.calendar.state

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import kotlin.time.Clock

enum class CalendarMode {
    CALENDAR,
    YEAR_MONTH_PICKER
}

@Stable
interface MyDatePickerState {
    val selectedLocalDate: LocalDate?
    val displayedMonthLocalDate: LocalDate
    val yearRange: IntRange
    val calendarMode: CalendarMode

    fun setSelection(localDate: LocalDate)
    fun setDisplayedMonth(localDate: LocalDate)
    fun setCalendarMode(calendarMode: CalendarMode)
}

@Stable
class MyDatePickerStateImpl(
    initialSelectedDateMillis: LocalDate? = null,
    initialDisplayedMonthMillis: LocalDate? = null,
    range: IntRange
) : MyDatePickerState {

    private var _selectedDateMillis: MutableState<LocalDate?> = mutableStateOf(initialSelectedDateMillis)
    private var _displayedMonthMillis: MutableState<LocalDate> = mutableStateOf(
        initialDisplayedMonthMillis ?: Clock.System.todayIn(TimeZone.currentSystemDefault())
    )
    private var _calendarMode: MutableState<CalendarMode> = mutableStateOf(CalendarMode.CALENDAR)


    override val selectedLocalDate: LocalDate?
        get() = _selectedDateMillis.value

    override val displayedMonthLocalDate: LocalDate
        get() = _displayedMonthMillis.value

    override val yearRange: IntRange = range

    override val calendarMode: CalendarMode
        get() = _calendarMode.value

    override fun setSelection(localDate: LocalDate) {
        _selectedDateMillis.value = localDate
    }

    override fun setDisplayedMonth(localDate: LocalDate) {
        _displayedMonthMillis.value = localDate
    }

    override fun setCalendarMode(calendarMode: CalendarMode) {
        _calendarMode.value = calendarMode
    }

    companion object {
        fun Saver(): Saver<MyDatePickerStateImpl, Any> = listSaver(
            save = {
                listOf(
                    it.selectedLocalDate?.toString(),
                    it.displayedMonthLocalDate.toString(),
                    it.yearRange.first,
                    it.yearRange.last
                )
            },
            restore = { values ->
                MyDatePickerStateImpl(
                    initialSelectedDateMillis = (values[0] as String?)?.let { LocalDate.parse(it) },
                    initialDisplayedMonthMillis = (values[1] as String).let { LocalDate.parse(it) },
                    range = values[2] as Int..values[3] as Int
                )
            }
        )
    }
}