import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableDefaults
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxOfOrNull
import com.dkproject.compsoe_component.custombottomsheetscaffold.CustomBottomSheetDefault
import kotlin.math.roundToInt

@Composable
fun CustomBottomSheetScaffold(
    modifier: Modifier = Modifier,
    scaffoldState: CustomBottomSheetScaffoldState = rememberCustomBottomSheetScaffoldState(),
    sheetDragHandle: @Composable () -> Unit = { CustomBottomSheetDefault.DragHandle() },
    topBar: @Composable () -> Unit,
    titleContent: @Composable () -> Unit,
    mapContent: @Composable () -> Unit,
    sheetContent: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    snackbarHost: @Composable (SnackbarHostState) -> Unit = { SnackbarHost(it) },
    sheetPeekHeight: Dp = CustomBottomSheetDefault.SheetPeekHeight,
    containerColor: Color = Color.White,
    contentColor: Color = contentColorFor(containerColor)
) {
    CustomBottomSheetScaffoldLayout(
        modifier = modifier,
        topBar = topBar,
        titleContent = titleContent,
        mapContent = mapContent,
        bottomSheet = {
            CustomStandardBottomSheet(
                state = scaffoldState.bottomSheetState,
                containerColor = containerColor,
                contentColor = contentColor,
                dragHandle = sheetDragHandle,
                content = sheetContent,
            )
        },
        sheetOffset = { scaffoldState.bottomSheetState.requireOffset() },
        snackbarHost = { snackbarHost(scaffoldState.snackbarHostState) },
        floatingActionButton = floatingActionButton,
        sheetPeekHeight = sheetPeekHeight,
        sheetState = scaffoldState.bottomSheetState
    )
}

@Composable
internal fun CustomStandardBottomSheet(
    state: CustomBottomSheetState,
    containerColor: Color,
    contentColor: Color,
    dragHandle: @Composable () -> Unit,
    content: @Composable () -> Unit
) {
    val orientation = Orientation.Vertical

    val cornerRadius by animateDpAsState(
        targetValue = if (state.currentValue == CustomSheetValue.FullyExpanded) 0.dp else 24.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "cornerRadius"
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clipToBounds(),
        color = containerColor,
        contentColor = contentColor,
        shape = RoundedCornerShape(topStart = cornerRadius, topEnd = cornerRadius),
        shadowElevation = 2.dp
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
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
            Box(modifier = Modifier.weight(1f)) {
                content()
            }
        }
    }
}

@Composable
internal fun CustomBottomSheetScaffoldLayout(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit,
    titleContent: @Composable () -> Unit,
    mapContent: @Composable () -> Unit,
    bottomSheet: @Composable () -> Unit,
    floatingActionButton: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit,
    sheetPeekHeight: Dp,
    sheetOffset: () -> Float,
    sheetState: CustomBottomSheetState,
) {
    Layout(
        modifier = modifier.background(Color.White),
        contents =
            listOf<@Composable () -> Unit>(
                topBar,
                titleContent,
                mapContent,
                bottomSheet,
                floatingActionButton,
                snackbarHost
            )
    ) { measurable, constraints ->
        val topBarMeasurable = measurable[0]
        val titleMeasurable = measurable[1]
        val mapMeasurable = measurable[2]
        val sheetMeasurable = measurable[3]
        val fabMeasurable = measurable[4]
        val snackbarMeasurable = measurable[5]

        val layoutWidth = constraints.maxWidth
        val layoutHeight = constraints.maxHeight
        val looseConstraints = constraints.copy(minWidth = 0, minHeight = 0)

        val topBarPlaceable = topBarMeasurable.fastMap { it.measure(looseConstraints) }
        val topBarHeight = topBarPlaceable.fastMaxOfOrNull { it.height } ?: 0

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

        val sheetMaxHeight = (layoutHeight - currentOffset).coerceAtLeast(0f).toInt()
        val sheetConstraints = looseConstraints.copy(maxHeight = sheetMaxHeight)
        val sheetPlaceable = sheetMeasurable.fastMap { it.measure(sheetConstraints) }

        val titlePlaceable = titleMeasurable.fastMap { it.measure(looseConstraints) }
        val titleHeight = titlePlaceable.fastMaxOfOrNull { it.height } ?: 0

        val transitionRange = (collapsedOffset - halfExpandedOffset).coerceAtLeast(1f)

        val titleProgress =
            ((collapsedOffset - currentOffset) / transitionRange).coerceIn(0f, 1f)

        val titleY = topBarHeight - (titleHeight * titleProgress).roundToInt()
        val mapY = topBarHeight + titleHeight - (titleHeight * titleProgress).roundToInt()

        val mapPlaceable = mapMeasurable.fastMap {
            it.measure(
                looseConstraints.copy(
                    maxHeight = layoutHeight - collapsedSheetHeight.toInt() - topBarHeight - titleHeight + 24.dp.toPx()
                        .toInt()
                )
            )
        }

        val fabPlaceable = fabMeasurable.fastMap { it.measure(looseConstraints) }
        val fabHeight = fabPlaceable.fastMaxOfOrNull { it.height } ?: 0
        val fabWidth = fabPlaceable.fastMaxOfOrNull { it.width } ?: 0
        val fabMarginBottom = 24.dp.toPx()
        val fabFadeEndPoint = topBarHeight + fabHeight + fabMarginBottom
        val halfToFabHiddenRange = (halfExpandedOffset - fabFadeEndPoint).coerceAtLeast(1f)
        val fabExpansionProgress =
            ((halfExpandedOffset - currentOffset) / halfToFabHiddenRange).coerceIn(0f, 1f)
        val fabAlpha = 1f - fabExpansionProgress
        val fabX = (layoutWidth - fabWidth - 16.dp.toPx()).roundToInt()
        val fabY = (currentOffset - fabHeight - fabMarginBottom).roundToInt()

        val snackbarPlaceable = snackbarMeasurable.fastMap { it.measure(looseConstraints) }

        layout(layoutWidth, layoutHeight) {
            val sheetWidth = sheetPlaceable.fastMaxOfOrNull { it.width } ?: 0
            val sheetOffsetX = Integer.max(0, (layoutWidth - sheetWidth) / 2)

            mapPlaceable.fastForEach {
                it.placeRelative(0, mapY)
            }

            titlePlaceable.fastForEach {
                it.placeRelativeWithLayer(
                    x = 0,
                    y = titleY
                ) {
                    alpha = 1f - titleProgress
                }
            }

            sheetPlaceable.fastForEach {
                it.placeRelative(sheetOffsetX, currentOffset.roundToInt())
            }

            topBarPlaceable.fastForEach { it.placeRelative(0, 0) }

            fabPlaceable.fastForEach {
                it.placeRelativeWithLayer(fabX, fabY) {
                    alpha = fabAlpha
                }
            }

            val snackbarWidth = snackbarPlaceable.fastMaxOfOrNull { it.width } ?: 0
            val snackbarHeight = snackbarPlaceable.fastMaxOfOrNull { it.height } ?: 0
            val snackbarOffsetX = (layoutWidth - snackbarWidth) / 2
            val snackbarOffsetY = currentOffset.roundToInt() - snackbarHeight
            snackbarPlaceable.fastForEach {
                it.placeRelative(snackbarOffsetX, snackbarOffsetY)
            }
        }
    }
}

@Stable
class CustomBottomSheetScaffoldState(
    val bottomSheetState: CustomBottomSheetState,
    val snackbarHostState: SnackbarHostState
)

@Composable
fun rememberCustomBottomSheetScaffoldState(
    bottomSheetState: CustomBottomSheetState = rememberCustomSheetState(),
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
): CustomBottomSheetScaffoldState {
    return remember(bottomSheetState, snackbarHostState) {
        CustomBottomSheetScaffoldState(
            bottomSheetState = bottomSheetState,
            snackbarHostState = snackbarHostState
        )
    }
}

@Composable
internal fun rememberCustomSheetState(
    initialValue: CustomSheetValue = CustomSheetValue.Collapsed,
): CustomBottomSheetState {
    return rememberSaveable(saver = CustomBottomSheetState.Saver()) {
        CustomBottomSheetState(initialValue = initialValue)
    }
}