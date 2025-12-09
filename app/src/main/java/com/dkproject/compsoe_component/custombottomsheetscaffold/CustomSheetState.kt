import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.runtime.Stable
import androidx.compose.runtime.saveable.Saver

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

enum class CustomSheetValue {
    Collapsed,
    HalfExpanded,
    FullyExpanded,
}