package com.dkproject.compsoe_component.snackbar

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.coroutines.resume

enum class CustomSnackbarResult {
    Dismissed,
    ActionPerformed,
}

@Stable
class CustomSnackbarHostState {
    private val mutex = Mutex()

    var currentSnackbarData by mutableStateOf<CustomSnackbarData?>(null)
        private set

    /**
     * 커스텀 스낵바를 보여줍니다.
     * @param visuals 보여질 스낵바 종류를 전달합니다. 종류로는 ActionSnackbar, BaseSnackbar가 있습니다.
     * @return [CustomSnackbarResult]
     */
    suspend fun showSnackbar(visuals: CustomSnackbarVisuals): CustomSnackbarResult {
        mutex.withLock {
            try {
                return suspendCancellableCoroutine { continuation ->
                    currentSnackbarData = CustomSnackbarDataImpl(visuals, continuation)
                }
            } finally {
                currentSnackbarData = null
            }
        }
    }

    private class CustomSnackbarDataImpl(
        override val visuals: CustomSnackbarVisuals,
        private val continuation: CancellableContinuation<CustomSnackbarResult>
    ): CustomSnackbarData {
        override fun dismiss() {
            if (continuation.isActive) continuation.resume(CustomSnackbarResult.Dismissed)
        }

        override fun performAction() {
            if (continuation.isActive) continuation.resume(CustomSnackbarResult.ActionPerformed)
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (other == null || this::class != other::class) return false

            other as CustomSnackbarDataImpl

            if (visuals != other.visuals) return false
            if (continuation != other.continuation) return false

            return true
        }

        override fun hashCode(): Int {
            var result = visuals.hashCode()
            result = 31 * result + continuation.hashCode()
            return result
        }
    }
}

@Stable
interface CustomSnackbarData {
    val visuals: CustomSnackbarVisuals
    fun dismiss()
    fun performAction()
}

@Stable
sealed interface CustomSnackbarVisuals {
    val message: String
    val duration: Long

    /**
     * 액션 버튼이 있는 스낵바 입니다.
     * @param message 스낵바에 보여질 메시지 입니다..
     * @param duration 스낵바가 보여질 시간입니다. 기본값은 4초 입니다.
     * @param actionLabel 액션 버튼의 제목입니다.
     * @param action 액션 버튼이 눌리고 나서 호출되는 람다 입니다.
     */
    data class ActionSnackbar(
        override val message: String,
        override val duration: Long = 4000L,
        val actionLabel: String,
        val action: () -> Unit,
    ) : CustomSnackbarVisuals


    /**
     * 기본 스낵바 입니다.
     * @param message 스낵바에 보여질 메시지 입니다..
     * @param duration 스낵바가 보여질 시간입니다. 기본값은 4초 입니다.
     */
    data class BaseSnackbar(
        override val message: String,
        override val duration: Long = 4000L,
    ) : CustomSnackbarVisuals
}