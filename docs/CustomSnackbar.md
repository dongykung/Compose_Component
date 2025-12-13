# CustomSnackbar

### 실행화면
<table>
<tr>
<td width="50%">

[Screen_recording_20251214_014529.webm](https://github.com/user-attachments/assets/2a33a55a-5442-4c29-b479-f67ef7aeec45)

</td>

</table>
<br>


### [사용법](https://github.com/dongykung/Compose_Component/blob/main/app/src/main/java/com/dkproject/compsoe_component/snackbar/CustomSnackbarEx.kt)
- 기본 사용방법이며 자세한 사항은 아래 Dev 섹션을 참고해 주세요.
```kotlin
   val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { CustomSnackbarHostState() }

    Scaffold(
        snackbarHost = { CustomSnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        // 액션 스낵바 
        Button(onClick = {
            scope.launch {
                // 스낵바 광클 시 이전 스낵바가 바로 사라지고 새 스낵바가 나타나도록 dismiss()를 수행합니다.
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    CustomSnackbarVisuals.ActionSnackbar(
                        message = context.getString(R.string.action_snackbar),
                        actionLabel = context.getString(R.string.action_snackbar_label),
                        action = { snackbarResultText = "액션 스낵바 버튼1 ActionPerformed" }
                    )
                )
            }
        }) {
            Text(text = stringResource(R.string.action_snackbar_button))
        }

        // 기본 스낵바
        Button(
            modifier = Modifier.padding(innerPadding),
            onClick = {
                scope.launch {
                  // 스낵바 광클 시 이전 스낵바가 바로 사라지고 새 스낵바가 나타나도록 dismiss()를 수행합니다.
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(
                        CustomSnackbarVisuals.BaseSnackbar(
                            message = context.getString(R.string.base_snackbar),
                            duration = 3000L
                        )
                    )
                }
            }) {
            Text(text = stringResource(R.string.base_snackbar_button))
        }
    }
```

<br>
<br>

# Dev
### 요구사항
- 스낵바가 띄어지는 시간을 조절할 수 있어야한다.
- 스낵바의 결과를 알 수 있어야 한다.
- 액션 스낵바, 기본 스낵바로 나뉘며 액션 스낵바는 액션 버튼이 존재한다.
- 액션 스낵바의 버튼을 누를 시 수행되어야 할 동작을 명시할 수 있어야 한다.

Material3에서 제공하는 Snackbar는 duration을 조절할 수 없는 문제가 있습니다. <br>
이를 해결하고자 커스텀 스낵바를 만들었습니다.



## 핵심 컴포넌트

### [CustomSnackbarHostState](https://github.com/dongykung/Compose_Component/blob/3c2ddf4eb5304cd3e0092992b157b29b87c2345e/app/src/main/java/com/dkproject/compsoe_component/snackbar/CustomSnackData.kt#L19)
커스텀 스낵바의 상태를 관리하고 제어하는 컨트롤러
```kotlin
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
    // ...
    }
}
```

<br>

### [CustomSnackbarData](https://github.com/dongykung/Compose_Component/blob/3c2ddf4eb5304cd3e0092992b157b29b87c2345e/app/src/main/java/com/dkproject/compsoe_component/snackbar/CustomSnackData.kt#L75)
스낵바 인스턴스 인터페이스 
- 커스텀 스낵바 Visuals를 가진다
- 스낵바는 dismiss()와 performAction 함수를 정의 해야 한다.
```kotlin
@Stable
interface CustomSnackbarData {
    val visuals: CustomSnackbarVisuals
    fun dismiss()
    fun performAction()
}
```

<br>

### [CustomSnackbarVisuals](https://github.com/dongykung/Compose_Component/blob/3c2ddf4eb5304cd3e0092992b157b29b87c2345e/app/src/main/java/com/dkproject/compsoe_component/snackbar/CustomSnackData.kt#L82)
스낵바의 종류입니다. 기본적으로 `Action`, `Basic` 버튼으로 나누어져 있습니다. <br>
(위 스낵바 컨트롤러의 showSnackbar 함수가 해당 해당 타입을 인자로 받습니다)
```kotlin
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
```

<br>

### [CustomSnackbarHost](https://github.com/dongykung/Compose_Component/blob/3c2ddf4eb5304cd3e0092992b157b29b87c2345e/app/src/main/java/com/dkproject/compsoe_component/snackbar/CustomSnackbarHost.kt#L34)
스낵바가 실제로 화면에 나타나고 사라지는 것을 시각적으로 처리하는 UI 컨테이너 역할<br>
기본 UI `CustomSnackbar`를 제공합니다. 스낵바의 모양을 커스텀하고 싶다면 스낵바 `Composable`을 작성하여 전달 가능합니다.
```kotlin
@Composable
fun CustomSnackbarHost(
    hostState: CustomSnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (snackbarData: CustomSnackbarData) -> Unit = {
      CustomSnackbar(it, modifier)
    }
) {

    val currentSnackbarData = hostState.currentSnackbarData
    LaunchedEffect(currentSnackbarData) {
        if (currentSnackbarData != null) {
            delay(currentSnackbarData.visuals.duration)
            currentSnackbarData.dismiss()
        }
    }

    Crossfade(
        targetState = currentSnackbarData,
        modifier = modifier
    ) { snackbarData ->
        snackbarData?.let { snackbar(it) }
    }
}
```

<br>

### [CustomSnackbar](https://github.com/dongykung/Compose_Component/blob/3c2ddf4eb5304cd3e0092992b157b29b87c2345e/app/src/main/java/com/dkproject/compsoe_component/snackbar/CustomSnackbarHost.kt#L62)
커스텀 스낵바에서 제공하는 기본 UI Composable 입니다. <br>
모양을 커스텀하고 싶다면 아래와 같이 `@Composable`을 직접 작성하여 전달하세요.
```kotlin
@Composable
private fun CustomSnackbar(
    snackbarData: CustomSnackbarData,
    modifier: Modifier = Modifier,
) {
    when (val visuals = snackbarData.visuals) {
        is CustomSnackbarVisuals.ActionSnackbar -> {
            CustomActionSnackbar(
                modifier = modifier,
                actionSnackbar = visuals,
                action = {
                    snackbarData.performAction()
                    visuals.action()
                },
                dismiss = { snackbarData.dismiss() }
            )
        }

        is CustomSnackbarVisuals.BaseSnackbar -> {
            BaseSnackbar(
                modifier = modifier,
                baseSnackbar = visuals,
                dismiss = { snackbarData.dismiss() }
            )
        }
    }
}
```

<br>

### 결과 반환
스낵바가 사라지고 난 뒤 수행되어야 할 작업이 있다면 아래와 같은 방법을 사용하세요
```kotlin
scope.launch {
    snackbarHostState.currentSnackbarData?.dismiss()
    val result = snackbarHostState.showSnackbar(
          CustomSnackbarVisuals.ActionSnackbar(
             message = context.getString(R.string.action_snackbar),
             actionLabel = context.getString(R.string.action_snackbar_label),
             action = {}
             )
     )
     snackbarResultText = when (result) {
             CustomSnackbarResult.Dismissed -> {
                  "액션 스낵바 버튼2 dismiss"
              }

               CustomSnackbarResult.ActionPerformed -> {
                   "액션 스낵바 버튼2 ActionPerformed"
                }
          }
}
```
