package com.dkproject.compsoe_component.calendar.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.dkproject.compsoe_component.R
import com.dkproject.compsoe_component.calendar.state.CalendarMode
import com.dkproject.compsoe_component.calendar.state.MyDatePickerColor
import com.dkproject.compsoe_component.calendar.state.MyDatePickerDefaults.MONTHS_IN_YEAR
import com.dkproject.compsoe_component.calendar.state.MyDatePickerState
import kotlinx.datetime.LocalDate

@Composable
fun YearMonthMode(
    state: MyDatePickerState,
    colors: MyDatePickerColor,
    modifier: Modifier = Modifier,
) {
    var year by rememberSaveable {
        mutableIntStateOf(state.displayedMonthLocalDate.year)
    }
    Column(modifier = modifier) {
        YearMonthModeYearSection(
            year = year,
            onPreviousYearClick = { year-- },
            onNextYearClick = { year++ }
        )
        YearMonthModeMonthSection(
            displayedLocalDate = state.displayedMonthLocalDate,
            year = year,
            colors = colors,
            onMonthClick = { month ->
                state.setDisplayedMonth(LocalDate(year, month, 1))
                state.setCalendarMode(CalendarMode.CALENDAR)
            }
        )
    }
}

@Composable
private fun YearMonthModeYearSection(
    year: Int,
    onPreviousYearClick: () -> Unit,
    onNextYearClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth(),
    ) {
        IconButton(onClick = onPreviousYearClick) {
            Icon(
                Icons.AutoMirrored.Default.KeyboardArrowLeft,
                null
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(text = stringResource(R.string.text_year, year))
        Spacer(modifier = Modifier.weight(1f))
        IconButton(onClick = onNextYearClick) {
            Icon(
                Icons.AutoMirrored.Default.KeyboardArrowRight,
                null
            )
        }
    }
}

@Composable
private fun YearMonthModeMonthSection(
    displayedLocalDate: LocalDate,
    year: Int,
    colors: MyDatePickerColor,
    onMonthClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(8.dp),
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3), modifier = modifier,
        contentPadding = PaddingValues(vertical = 8.dp, horizontal = 16.dp)
    ) {
        items(MONTHS_IN_YEAR) {
            val selected =
                displayedLocalDate.year == year && displayedLocalDate.month.ordinal == it + 1
            val containerColor =
                if (selected) colors.selectedDayContainerColor else colors.containerColor
            val contentColor =
                if (selected) colors.selectedDayContentColor else colors.weekDayDefaultColor
            Surface(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onMonthClick(it + 1) },
                shape = shape,
                contentColor = contentColor,
                color = containerColor
            ) {
                Text(
                    text = stringResource(R.string.text_month, it + 1),
                    modifier = Modifier.padding(vertical = 24.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}