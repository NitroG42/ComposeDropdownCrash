# MotionEvents crash with a spinner-like composable

## Reported issue

## Context

Here's a custom Spinner implementation :

```kotlin

@Composable
fun Dropdown(
    modifier: Modifier = Modifier
) {
    var showDropdown by remember { mutableStateOf(false) }
    //Remove this modifier and the crash goes away
    var width by remember { mutableStateOf(0) }
    Box(modifier = modifier) {
        Column {
            Box {
                TextField(
                    value = "",
                    onValueChange = {},
                    readOnly = true,
                    label = {
                        Text(text = "Dropdown")
                    },
                    trailingIcon = {
                        Icon(imageVector = Icons.Default.ArrowDropDown, contentDescription = null, tint = MaterialTheme.colors.onSurface)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        //Remove this modifier and the crash goes away
                        .onSizeChanged {
                            width = it.width
                        }
                        .clickable {
                            showDropdown = true
                        },
                    singleLine = true
                )
                Box(modifier = Modifier
                    .matchParentSize()
                    .clickable {
                        showDropdown = true
                    }
                )
            }
            DropdownMenu(
                //Remove this modifier and the crash goes away
                modifier = Modifier.requiredWidth(if (width != 0) with(LocalDensity.current) { width.toDp() } else Dp.Unspecified),
                expanded = showDropdown,
                onDismissRequest = {
                    showDropdown = false
                }) {
                listOf("A", "B", "C", "D").forEach { value ->
                    DropdownMenuItem(
                        onClick = {
                            showDropdown = false
                        }) {
                        Text(
                            text = value,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
        }
    }
}
```

Since Compose 1.1.0-alpha05, this implementation crashes when pressing an entry of the Dropdown, with the following stacktrace :

```
E/AndroidRuntime: FATAL EXCEPTION: main
    Process: com.nitro.composedropdowncrash, PID: 24983
    java.lang.IllegalStateException: Compose assumes that all pointer ids in MotionEvents are first provided alongside ACTION_DOWN, ACTION_POINTER_DOWN, or ACTION_HOVER_ENTER.  Instead the first event was seen for ID 0 with ACTION_HOVER_EXIT
        at androidx.compose.ui.input.pointer.MotionEventAdapter.createPointerInputEventData(MotionEventAdapter.android.kt:209)
        at androidx.compose.ui.input.pointer.MotionEventAdapter.convertToPointerInputEvent$ui_release(MotionEventAdapter.android.kt:101)
        at androidx.compose.ui.platform.AndroidComposeView.handleMotionEvent-8iAsVTc(AndroidComposeView.android.kt:884)
        at androidx.compose.ui.platform.AndroidComposeView.dispatchHoverEvent(AndroidComposeView.android.kt:1032)
        at android.view.View.dispatchGenericMotionEvent(View.java:14419)
        at android.view.ViewGroup.dispatchTransformedGenericPointerEvent(ViewGroup.java:2606)
        at android.view.ViewGroup.dispatchHoverEvent(ViewGroup.java:2229)
        at android.view.ViewGroup.exitHoverTargets(ViewGroup.java:2316)
        at android.view.ViewGroup.dispatchDetachedFromWindow(ViewGroup.java:3917)
        at android.view.ViewRootImpl.dispatchDetachedFromWindow(ViewRootImpl.java:4647)
        at android.view.ViewRootImpl.doDie(ViewRootImpl.java:7691)
        at android.view.ViewRootImpl.die(ViewRootImpl.java:7668)
        at android.view.WindowManagerGlobal.removeViewLocked(WindowManagerGlobal.java:509)
        at android.view.WindowManagerGlobal.removeView(WindowManagerGlobal.java:450)
        at android.view.WindowManagerImpl.removeViewImmediate(WindowManagerImpl.java:141)
        at androidx.compose.ui.window.PopupLayout.dismiss(AndroidPopup.android.kt:593)
        at androidx.compose.ui.window.AndroidPopup_androidKt$Popup$2$invoke$$inlined$onDispose$1.dispose(Effects.kt:486)
        at androidx.compose.runtime.DisposableEffectImpl.onForgotten(Effects.kt:85)
        at androidx.compose.runtime.CompositionImpl$RememberEventDispatcher.dispatchRememberObservers(Composition.kt:793)
        at androidx.compose.runtime.CompositionImpl.applyChanges(Composition.kt:647)
        at androidx.compose.runtime.Recomposer$runRecomposeAndApplyChanges$2$2.invoke(Recomposer.kt:479)
        at androidx.compose.runtime.Recomposer$runRecomposeAndApplyChanges$2$2.invoke(Recomposer.kt:416)
        at androidx.compose.ui.platform.AndroidUiFrameClock$withFrameNanos$2$callback$1.doFrame(AndroidUiFrameClock.android.kt:34)
        at androidx.compose.ui.platform.AndroidUiDispatcher.performFrameDispatch(AndroidUiDispatcher.android.kt:109)
        at androidx.compose.ui.platform.AndroidUiDispatcher.access$performFrameDispatch(AndroidUiDispatcher.android.kt:41)
        at androidx.compose.ui.platform.AndroidUiDispatcher$dispatchCallback$1.doFrame(AndroidUiDispatcher.android.kt:69)
        at android.view.Choreographer$CallbackRecord.run(Choreographer.java:970)
        at android.view.Choreographer.doCallbacks(Choreographer.java:796)
        at android.view.Choreographer.doFrame(Choreographer.java:727)
        at android.view.Choreographer$FrameDisplayEventReceiver.run(Choreographer.java:957)
        at android.os.Handler.handleCallback(Handler.java:938)
        at android.os.Handler.dispatchMessage(Handler.java:99)
        at android.os.Looper.loop(Looper.java:223)
        at android.app.ActivityThread.main(ActivityThread.java:7656)
        at java.lang.reflect.Method.invoke(Native Method)
        at com.android.internal.os.RuntimeInit$MethodAndArgsCaller.run(RuntimeInit.java:592)
        at com.android.internal.os.ZygoteInit.main(ZygoteInit.java:947)
```

## Cause

It "seems" to be related to the following modifier in the DropdownMenu :

```
DropdownMenu(
//Remove this modifier and the crash goes away
modifier = Modifier.requiredWidth(if (width != 0) with(LocalDensity.current) { width.toDp() } else Dp.Unspecified),
```

which goal was to have the DropdownMenu equals width with the TextField

## Related Issue

https://issuetracker.google.com/issues/196857015