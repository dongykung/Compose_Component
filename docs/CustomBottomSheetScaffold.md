# [CustomBottomSheetScaffold](https://github.com/dongykung/Compose_Component/blob/a919d415164e6be2b3a9309556710971be62dd05/app/src/main/java/com/dkproject/compsoe_component/custombottomsheetscaffold/CustomBottomSheetScaffold.kt#L53)
### 실행화면


<table>
<tr>
<td width="50%">

https://github.com/user-attachments/assets/4c077e0e-f7a6-4941-9d65-1652f257698c

</td>
<td width="50%">

https://github.com/user-attachments/assets/b5f08eb0-26fb-4976-a42b-da9473a75207

</td>
</tr>
</table>
<br>

### 사용법
```kotlin
@Composable
fun CustomBottomSheetScaffoldEx(modifier: Modifier = Modifier) {
    CustomBottomSheetScaffold(
        modifier = modifier,
        topBar = { CustomBottomSheetTopBarSection("TopAppBar") }, // 사용자 정의 Composable
        titleContent = {
            CustomBottomSheetTitleSection(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                title = "Title Section",
                content = "Content Section"
            )
        }, // 사용자 정의 Composable
        mapContent = { OiBottomSheetContent() }, // 사용자 정의 Composable
        sheetContent = { CustomBottomSheetContentSection(IntRange(1, 100)) }, // 사용자 정의 Composable
        floatingActionButton = { CustomBottomSheetFloatingButton() } // 사용자 정의 Composable
    )
}
```


<br><br>

# Dev
### 요구사항
- `Collapsed(축소)`, `HalfExpanded(중간)`, `FullyExpanded(확장)` 상태를 가지는 BottomSheetScaffold 필요
- 초기 바텀시트의 기본 상태값은 `Collapsed` 이다. 
- 바텀시트의 상태는 `DragHandle`을 통해 상태를 변경할 수 있으며 스냅을 통해 가장 가까운 상태로 이동할 수 있어야 한다.
- 바텀시트의 상태가 `Collapsed` -> `HalfExpanded`로 이동할 때 Title Section이 TopBar Section으로 올라가며 서서히 사라진다.
- Floating Action Button은 BottomSheet의 우측 상단으로에 존재하며 24.dp의 padding을 사이에 두고 있다.
- Floating Action Button은 BottomSheet의 상태에 따라 움직이며 `FullyExpanded`로 변경될 때 사라진다.

<br>

## 핵심 컴포넌트
### [CustomSheetState](https://github.com/dongykung/Compose_Component/blob/main/app/src/main/java/com/dkproject/compsoe_component/custombottomsheetscaffold/CustomSheetState.kt)
```kotlin
enum class CustomSheetValue {
    Collapsed,
    HalfExpanded,
    FullyExpanded,
}

@Stable
class CustomBottomSheetState(
    initialValue: CustomSheetValue = CustomSheetValue.Collapsed,
) {
    val currentValue: CustomSheetValue
        get() = anchoredDraggableState.currentValue

    fun requireOffset(): Float = anchoredDraggableState.requireOffset()

    fun updateAnchors(anchors: DraggableAnchors<CustomSheetValue>) {
        anchoredDraggableState.updateAnchors(anchors)
    }

    var anchoredDraggableState =
        AnchoredDraggableState(
            initialValue = initialValue,
        )
    // ... Saver
```
- [AnchoredDraggableState](https://developer.android.com/reference/kotlin/androidx/compose/foundation/gestures/AnchoredDraggableState): Anchor들 사이를 드래그하여 이동할 수 있는 UI 요소의 상태 관리자
- currentValue: 정의된 `anchoredDraggableState`의 앵커 Value
- requireOffset: 정의된 `anchoredDraggableState`의 현재 픽셀 위치

<br>

### [CustomBottomSheetScaffoldLayout](https://github.com/dongykung/Compose_Component/blob/a919d415164e6be2b3a9309556710971be62dd05/app/src/main/java/com/dkproject/compsoe_component/custombottomsheetscaffold/CustomBottomSheetScaffold.kt#L138)
```kotlin
@Composable
internal fun CustomBottomSheetScaffoldLayout(
    sheetOffset: () -> Float, // 상태 지연 읽기
) {
  Layout {
  // ...
    val collapsedSheetHeight = sheetPeekHeight.toPx()
    val halfExpandedSheetHeight = layoutHeight / 2f

    val collapsedOffset = layoutHeight - collapsedSheetHeight
    val halfExpandedOffset = layoutHeight - halfExpandedSheetHeight

    val newAnchors = DraggableAnchors {
            CustomSheetValue.Collapsed at collapsedOffset
            CustomSheetValue.HalfExpanded at halfExpandedOffset
            CustomSheetValue.FullyExpanded at topBarHeight.toFloat()
     }
     sheetState.updateAnchors(newAnchors)

     val currentOffset = sheetOffset()

     val titlePlaceable = titleMeasurable.fastMap { it.measure(looseConstraints) }
     val titleHeight = titlePlaceable.fastMaxOfOrNull { it.height } ?: 0

     val transitionRange = (collapsedOffset - halfExpandedOffset).coerceAtLeast(1f)

     val titleProgress = ((collapsedOffset - currentOffset) / transitionRange).coerceIn(0f, 1f)
     val titleAlpha = 1f- titleProgress

     val titleY = topBarHeight - (titleHeight * titleProgress).roundToInt()
     val mapY = topBarHeight + titleHeight - (titleHeight * titleProgress).roundToInt()
  // ... layout { ... }
  }
}
```
- Layout 단계에서 각 상태가 어느 Offset에 위치하는지 정의
- currentOffset을 람다로 전달받아 `상태 지연 읽기`를 통해 Recomposition을 방지합니다(람다의 참조값은 변경되지 않기 때문).
- Layout 단계에서 측정 -> 배치 과정이 이루어지는데 측정단계에서 해당 Composition의 Width와 Height 정보를 알 수 있고 이 정보를 토대로 각 Composable의 배치될 좌표들을 계산할 수 있음
- offset 수식으로 현재 offset일 때 애니메이션 범위를 계산할 수 있음

  - ex) 
    - layoutHeight = 1400px
    - topBarHeight = 80px
    - titleHeight = 60px
    - collapsedSheetHeight = 250px
```kotlin
collapsedOffset = 1400 - 250 = 1150px
halfExpandedOffset = 1400 - 700 = 700px

transitionRange = 1150 - 700 = 450px // "Title이 애니메이션되는 구간의 길이"
```

<br>

```kotlin
currentOffset = 1150px  // BottomSheet의 초기 상태 = Collapsed

titleProgress = (1150 - 1150) / 450 = 0 / 450 = 0.0
// Progress 0% = 애니메이션 시작 지점

titleAlpha = 1 - 0 = 1.0
// 완전히 보임 (불투명)

titleY = 80 - (60 * 0) = 80px
// TopBar 바로 아래에 위치
```
이런식으로 Progress를 계산할 수 있고 이 값을 이용하여 다른 Composable의 Y값을 Layout 단계에서 변경하거나 `alpha` 값 또한 변경가능


<br>

### [CustomStandardBottomSheet](https://github.com/dongykung/Compose_Component/blob/a919d415164e6be2b3a9309556710971be62dd05/app/src/main/java/com/dkproject/compsoe_component/custombottomsheetscaffold/CustomBottomSheetScaffold.kt#L90)
`DragHandle` 과 `BottomsheetContent`가 들어갈 Composable

- DragHandle 영역의 Modifier에 .`anchoredDraggable` 추가
- flingBEhavior 정의로 snap이 동작하도록
```kotlin
Box(
      Modifier
        .align(Alignment.CenterHorizontally)
        .anchoredDraggable(
             state = state.anchoredDraggableState,
             orientation = orientation,
             enabled = true,
             flingBehavior = AnchoredDraggableDefaults.flingBehavior(state.anchoredDraggableState)
             )
         ) {
              dragHandle()
            }
```
