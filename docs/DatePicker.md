# [MyDatePicker](https://github.com/dongykung/Compose_Component/blob/7878f5187183eb70fefcd5fad53c73bf63893887/app/src/main/java/com/dkproject/compsoe_component/calendar/component/MyDatePicker.kt#L57)
### 실행화면
[Screen_recording_20260318_004150.webm](https://github.com/user-attachments/assets/9f4137a5-19ab-42fb-ae0b-a8589ca73e50)

<br>

### 사용법
- [Kotlinx-datetime](https://github.com/Kotlin/kotlinx-datetime) 의존성을 추가합니다.
- [Kotlinx.immutable](https://github.com/Kotlin/kotlinx.collections.immutable) 의존성을 추가합니다.
```kotlin
val myDatePickerState = rememberMyDatePickerState()
MyDatePicker(state = myDatePickerState)

LaunchedEffect(myDatePickerState.selectedLocalDate) {
   // viewModel.updateSelectedDate(myDatePickerState.selectedLocalDate)
}
```

<br>

## Dev
### 요구사항
- 오늘 이후로 날짜를 선택할 수 있는 DatePicker Component 개발
- Calenadar Mode는 2가지로 달력 모드와(CalendarMode) | 년 월을 선택할 수 있는 모드(YearMonth Mode)가 존재한다
- 오늘 이전의 날자는 선택할 수 없다
- 일, 월, 화, 수, 목, 금, 토 순으로 날짜가 보여진다
- 달력 그리드는 항상 행 단위로 채워지며, 해당 월에 속하지 않는 칸은 이전/다음 달 날짜를 비활성 상태로 표시한다.

<br>

해당 요구사항을 구현하기 위해 Long 타입 대신 kotlinx-datetime의 LocalDate 타입을 선택했습니다. LocalDate는 날짜 연산(월 이동, 요일 계산, 범위 비교 등)을 직관적으로 표현할 수 있다고 생각했습니다.

LocalDate Type은 Unstable한 Type 입니다. LocalDate Type을 내부적으로 변경할 일이 없기에 [stability_config.conf](https://developer.android.com/develop/ui/compose/performance/stability/fix?hl=ko#configuration-file) kotlinx.datetime의 
LocalDate 타입을 Stable로 명시하여 Unstable 문제를 해결할 수 있습니다.

<br>

## 구조
<img width="705" height="421" alt="image" src="https://github.com/user-attachments/assets/7bd6efac-425c-4414-98e5-80c939d370fa" />

<Br>

### [MyDatePickerState]([https://github.com/dongykung/Compose_Component/blob/main/app/src/main/java/com/dkproject/compsoe_component/calendar/state/MyDatePickerState.kt](https://github.com/dongykung/Compose_Component/blob/7878f5187183eb70fefcd5fad53c73bf63893887/app/src/main/java/com/dkproject/compsoe_component/calendar/state/MyDatePickerState.kt#L19))
```kotlin
// PickerState가 가지는 속성들을 추상화 한 interface 입니다.
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
```

```kotlin
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
        // configuration에서도 살아남기 위해 Saver를 정의해줍니다.
        fun Saver(): Saver<MyDatePickerStateImpl, Any> = listSaver(
            save = {
                listOf(
                    it.selectedLocalDate?.toString(), // LocalDate Tpye은 Primitive Type이 아니기 때문에 String으로 변환하여 저장합니다.
                    it.displayedMonthLocalDate.toString(), // // LocalDate Tpye은 Primitive Type이 아니기 때문에 String으로 변환하여 저장합니다.
                    it.yearRange.first,
                    it.yearRange.last
                )
            },
            restore = { values ->
                MyDatePickerStateImpl(
                    initialSelectedDateMillis = (values[0] as String?)?.let { LocalDate.parse(it) }, // String으로 저장한 날짜 문자열을 LocalDate로 불러 옵니다.
                    initialDisplayedMonthMillis = (values[1] as String).let { LocalDate.parse(it) }, // String으로 저장한 날짜 문자열을 LocalDate로 불러 옵니다.
                    range = values[2] as Int..values[3] as Int
                )
            }
        )
    }
}
```

<br>
<br>

### [MyDatePickerModel](https://github.com/dongykung/Compose_Component/blob/7878f5187183eb70fefcd5fad53c73bf63893887/app/src/main/java/com/dkproject/compsoe_component/calendar/state/MyDatePickerModel.kt#L22)
달력 UI 데이터를 생성하는 핵심 모델 클래스 입니다. <br>

로케일과 색상 설정을 기반으로 요일 헤더를 구성하고, 특정 월의 날짜 셀 목록(이전/다음 달 채움, 오늘 표시, 선택 상태, 범위 포함 여부)을 계산하여 MyCalendarMonth 클래스를 반환합니다.
```kotlin
// DayOfWeek는 월요일 부터 시작하기에 일요일 부터 시작하게 변경하여 유지합니다.
   val allWeekNames = DayOfWeek.entries.map {
        it.displayName(locale = locale) to it.getDayOfWeekColor(colors)
    }.let { it.takeLast(1) + it.dropLast(1) }.toImmutableList()

// Kotlinx의 DayOfWeek를 사용하기 위해 displayName 확장 함수를 작성합니다.
// Kotlinx의 DayOfWeek에는 getDisplayName 확장 함수가 존재 하지 않기에 java의 함수를 사용합니다.
fun DayOfWeek.displayName(
    style: TextStyle = TextStyle.NARROW,
    locale: CalendarLocale = Locale.getDefault()
): String = java.time.DayOfWeek.of(this.isoDayNumber)
    .getDisplayName(style, locale)
```

<br>
해당 월에 속하지 않는 칸은 이전/다음 달 날짜 로직을 어떻게 구할가요? <br>
우선 이전 월의 날짜를 구하는 방법부터 알아보겠습니다.

```kotlin
// 일(7), 월(1), 화(2), 수(3), 목(4), 금(5), 토(6) 
val firstDayOfMonth = LocalDate(displayedMonth.year, displayedMonth.month, 1) 
val startOffset = firstDayOfMonth.dayOfWeek.isoDayNumber % 7

/**
 * 1일의 isoDayNumber를 통해 1일이 무슨 요일인지 구할 수 있습니다.
 * 2026.03.01의 isoDayNumber가 7이라고 해보겠습니다. 7이니 이전 월의 날짜는 필요없습니다.
 * 2026.04.01의 isoDayNumber는 3 입니다.
 * 즉 isoNumber % 7 모듈러 연산을 진행하면 이전 월의 필요한 날짜 수를 알 수 있습니다. 
 */
```

<br>

필요한 다음 월의 날짜를 구하는 방법도 알아보겠습니다.

```kotlin
/**
 * 1. 아까 구한 startOffset 값이 필요합니다.
 * 2. 현재 보여지고 있는 달의 총 day 수를 구합니다.
 * 1에서 더한 startOffset + 2에서 구한 총 day를 구합니다 => 이를 totalCells 라고 하겠습니다.
 * totalCells를 다 그리고도 뒤에 빈 요일이 생긴다면 다음 월의 날짜를 표시해야 합니다.
 * val trailingEmptyDays = if (totalCells % 7 == 0) 0 else 7 - (totalCells % 7)
 * 1주일은 7일이기 때문에 7 모듈러 연산이 0이라면 뒤에 빈 요일이 없는 것이고 그게 아니라면 7 - 모듈러 연산 값을 수행하면 값을 구할 수 있습니다.
 * 
 */
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
```

<br>

### [MyCalendarMonth](https://github.com/dongykung/Compose_Component/blob/7878f5187183eb70fefcd5fad53c73bf63893887/app/src/main/java/com/dkproject/compsoe_component/calendar/state/MyDatePickerModel.kt#L90)
- 현재 보여지고 있는 LocalDate가 가지는 days 리스트를 가지고 있는 데이터 클래스 입니다.
- compose Stability를 위해 ImmutableList Type으로 선언하였습니다.
```kotlin
@Immutable
data class MyCalendarMonth(
    val days: ImmutableList<MyCalendarDay>
) {
    val totalWeekCount = days.size / 7
}
```

<br>

### [MyCalendarDay](https://github.com/dongykung/Compose_Component/blob/7878f5187183eb70fefcd5fad53c73bf63893887/app/src/main/java/com/dkproject/compsoe_component/calendar/state/MyDatePickerModel.kt#L97)
- 해당 일이 어떤 속성을 가지는지 명시한 데이터 클래스 입니다.
- 향후 확장성을 위해 inRage 또한 포함하였습니다.
```kotlin
@Immutable
data class MyCalendarDay(
    val date: LocalDate,
    val isToday: Boolean,
    val isEnabled: Boolean,
    val inRange: Boolean,
    val isSelected: Boolean
)
```
