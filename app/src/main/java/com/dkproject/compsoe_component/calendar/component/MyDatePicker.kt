package com.dkproject.compsoe_component.calendar.component

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.dkproject.compsoe_component.R
import com.dkproject.compsoe_component.calendar.state.CalendarMode
import com.dkproject.compsoe_component.calendar.state.MyCalendarDay
import com.dkproject.compsoe_component.calendar.state.MyCalendarMonth
import com.dkproject.compsoe_component.calendar.state.MyDatePickerColor
import com.dkproject.compsoe_component.calendar.state.MyDatePickerDefaults
import com.dkproject.compsoe_component.calendar.state.MyDatePickerDefaults.DAYS_IN_WEEK
import com.dkproject.compsoe_component.calendar.state.MyDatePickerModel
import com.dkproject.compsoe_component.calendar.state.MyDatePickerState
import com.dkproject.compsoe_component.calendar.state.MyDatePickerStateImpl
import com.dkproject.compsoe_component.ui.theme.Compsoe_ComponentTheme
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.plus
import java.util.Locale

@Composable
fun rememberMyDatePickerState(
    initialSelectedDateMillis: LocalDate? = null,
    initialDisplayedMonthMillis: LocalDate? = null,
    range: IntRange = IntRange(2023, 2027)
): MyDatePickerState {
    return rememberSaveable(saver = MyDatePickerStateImpl.Saver()) {
        MyDatePickerStateImpl(
            initialSelectedDateMillis = initialSelectedDateMillis,
            initialDisplayedMonthMillis = initialDisplayedMonthMillis,
            range = range
        )
    }
}

@Composable
fun MyDatePicker(
    state: MyDatePickerState,
    modifier: Modifier = Modifier,
    colors: MyDatePickerColor = MyDatePickerDefaults.colors()
) {
    val locale = getCurrentLocale()
    val calendarModel = remember(locale) {
        MyDatePickerModel(locale, colors)
    }

    Column(modifier = modifier.background(colors.containerColor)) {
        AnimatedContent(targetState = state.calendarMode) { mode ->
            when (mode) {
                CalendarMode.CALENDAR -> CalendarMode(
                    state = state,
                    calendarModel = calendarModel,
                    colors = colors
                )

                CalendarMode.YEAR_MONTH_PICKER -> YearMonthMode(
                    state = state,
                    colors = colors,
                )
            }
        }
    }
}

@Composable
internal fun CalendarMode(
    state: MyDatePickerState,
    calendarModel: MyDatePickerModel,
    colors: MyDatePickerColor,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        CalendarModeYearMonthSection(
            displayedMonth = state.displayedMonthLocalDate,
            onPreviousMonthClick = {
                state.setDisplayedMonth(state.displayedMonthLocalDate.plus(-1, DateTimeUnit.MONTH))
            },
            onNextMonthClick = {
                state.setDisplayedMonth(state.displayedMonthLocalDate.plus(1, DateTimeUnit.MONTH))
            },
            onModeChangeClick = {
                state.setCalendarMode(CalendarMode.YEAR_MONTH_PICKER)
            },
            colors = colors,
        )
        CalendarModeWeekDaysSection(calendarModel = calendarModel)
        CalendarModeMonthSection(
            calendarMonth = calendarModel.getMonth(
                displayedMonth = state.displayedMonthLocalDate,
                selectedDate = state.selectedLocalDate,
            ),
            colors = colors,
            onDateSelectionChange = {
                state.setSelection(it)
            }
        )
    }
}

@Composable
internal fun CalendarModeYearMonthSection(
    displayedMonth: LocalDate,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onModeChangeClick: () -> Unit,
    colors: MyDatePickerColor,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onPreviousMonthClick) {
            Icon(
                Icons.AutoMirrored.Default.KeyboardArrowLeft,
                null
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Surface(
            onClick = onModeChangeClick,
            color = colors.containerColor
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    stringResource(
                        R.string.text_year_month,
                        displayedMonth.year,
                        displayedMonth.monthNumber
                    )
                )
                Icon(Icons.Default.KeyboardArrowDown, null)
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onNextMonthClick) {
            Icon(
                Icons.AutoMirrored.Default.KeyboardArrowRight,
                null
            )
        }
    }
}

@Composable
internal fun CalendarModeWeekDaysSection(
    calendarModel: MyDatePickerModel,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        calendarModel.allWeekNames.forEach { (weekName, color) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = weekName,
                    color = color,
                )
            }
        }
    }
}

@Composable
internal fun CalendarModeMonthSection(
    calendarMonth: MyCalendarMonth,
    colors: MyDatePickerColor,
    onDateSelectionChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        for (weekIndex in 0 until calendarMonth.totalWeekCount) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                for (dayIndex in 0 until DAYS_IN_WEEK) {
                    val index = weekIndex * DAYS_IN_WEEK + dayIndex
                    val day = calendarMonth.days[index]
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .aspectRatio(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CalendarModeDay(
                            day = day,
                            colors = colors,
                            onClickDay = { onDateSelectionChange(day.date) },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun CalendarModeDay(
    day: MyCalendarDay,
    colors: MyDatePickerColor,
    onClickDay: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
) {
    Surface(
        modifier = modifier,
        enabled = day.isEnabled,
        onClick = onClickDay,
        shape = shape,
        contentColor = colors.dayContentColor(
            selected = day.isSelected,
            inRange = day.inRange,
            enabled = day.isEnabled,
            isToday = day.isToday,
        ),
        color = colors.dayContainerColor(
            selected = day.isSelected,
            inRange = day.inRange,
            enabled = day.isEnabled,
            isToday = day.isToday
        )
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = day.date.day.toString(),
                fontWeight = getFontWeight(
                    today = day.isToday,
                    selected = day.isSelected,
                    isRange = day.inRange
                )
            )
        }
    }
}

@Composable
internal fun getFontWeight(
    today: Boolean,
    selected: Boolean,
    isRange: Boolean
): FontWeight = when {
    selected || isRange || today -> FontWeight.SemiBold
    else -> FontWeight.Normal
}

@Composable
@ReadOnlyComposable
internal fun getCurrentLocale(): Locale {
    return LocalConfiguration.current.locales[0]
}

@Composable
@Preview
private fun MyDatePickerPreview() {
    Compsoe_ComponentTheme {
        MyDatePicker(
            state = rememberMyDatePickerState()
        )
    }
}
