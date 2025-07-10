package com.dkproject.compsoe_component.custombottomsheetscaffold

import android.util.Log
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.runtime.saveable.Saver
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Velocity

class CustomBottomSheetState(
    initialValue: CustomSheetValue = CustomSheetValue.Collapsed,
) {
    val currentValue: CustomSheetValue
        get() = anchoredDraggableState.currentValue

    fun requireOffset(): Float = anchoredDraggableState.requireOffset()

    internal suspend fun animateTo(
        targetValue: CustomSheetValue,
    ) {
        anchoredDraggableState.animateTo(targetValue)
    }

    fun updateAnchors(anchors: DraggableAnchors<CustomSheetValue>) {
        anchoredDraggableState.updateAnchors(anchors)
    }

    var anchoredDraggableState =
        AnchoredDraggableState(
            initialValue = initialValue,
        )

    companion object {
        fun Saver(): Saver<CustomBottomSheetState, CustomSheetValue> =
            Saver(
                save = { it.currentValue },
                restore = { savedValue ->
                    CustomBottomSheetState(
                        initialValue = savedValue,
                    )
                }
            )
    }
}

internal fun consumeSwipeNestedScrollConnection(
    sheetState: CustomBottomSheetState,
    orientation: Orientation,
    onFling: (velocity: Float) -> Unit
): NestedScrollConnection = object : NestedScrollConnection {

    override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
        Log.d("onPreScroll", "onPreScroll")
        val delta = available.toFloat()
        val currentState = sheetState.currentValue
        if (source == NestedScrollSource.UserInput) {
            return when (currentState) {
                CustomSheetValue.Collapsed -> {
                    sheetState.anchoredDraggableState.dispatchRawDelta(delta)
                        .toOffset()
                }

                CustomSheetValue.HalfExpanded -> {
                    Offset.Zero
                }

                CustomSheetValue.FullyExpanded -> {
                    Offset.Zero
                }
            }
        }
        return Offset.Zero
    }

    override fun onPostScroll(
        consumed: Offset,
        available: Offset,
        source: NestedScrollSource
    ): Offset {
        Log.d("onPostScroll", "onPostScroll")
        val currentState = sheetState.currentValue
        if (source == NestedScrollSource.UserInput) {
            return when (currentState) {
                CustomSheetValue.Collapsed -> {
                    // Collapsed 상태에서는 남은 스크롤을 바텀시트가 처리
                    sheetState.anchoredDraggableState.dispatchRawDelta(available.toFloat())
                        .toOffset()
                }
                CustomSheetValue.FullyExpanded -> {
                    Offset.Zero
                }
                CustomSheetValue.HalfExpanded -> {
                    Offset.Zero
                }
            }
        }
        return Offset.Zero
    }

    override suspend fun onPreFling(available: Velocity): Velocity {
        Log.d("onPreFling", "onPreFling")
        val toFling = available.toFloat()
        val currentState = sheetState.currentValue

        return when (currentState) {
            CustomSheetValue.Collapsed -> {
                onFling(toFling)
                available
            }
            CustomSheetValue.FullyExpanded -> {
                Velocity.Zero
            }
            CustomSheetValue.HalfExpanded -> {
                Velocity.Zero
            }
        }
    }

    private fun Float.toOffset(): Offset =
        Offset(
            x = if (orientation == Orientation.Horizontal) this else 0f,
            y = if (orientation == Orientation.Vertical) this else 0f
        )

    @JvmName("velocityToFloat")
    private fun Velocity.toFloat() = if (orientation == Orientation.Horizontal) x else y

    @JvmName("offsetToFloat")
    private fun Offset.toFloat(): Float = if (orientation == Orientation.Horizontal) x else y
}


enum class CustomSheetValue {
    Collapsed,
    HalfExpanded,
    FullyExpanded,
}