package com.alchitry.labs2.ui.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.ComposeDialog
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.DialogWindowScope
import com.alchitry.labs2.ui.components.WindowDecoration
import com.alchitry.labs2.ui.theme.AlchitryTheme
import com.alchitry.labs2.windows.LocalComposeWindow
import java.awt.Color
import java.awt.Dialog
import java.awt.Dimension
import java.awt.Point
import kotlin.math.roundToInt

@Composable
fun AlchitryDialog(
    visible: Boolean,
    title: String,
    onClose: () -> Unit,
    resizable: Boolean = false,
    content: @Composable DialogWindowScope.() -> Unit
) {
    val window = LocalComposeWindow.current
    val background = MaterialTheme.colorScheme.surface

    if (visible) {
        DialogWindow(visible = true, dispose = {
            it.dispose()
        }, create = {
            ComposeDialog(window, modalityType = Dialog.ModalityType.APPLICATION_MODAL).apply {
                maximumSize = window.size
                isResizable = resizable
                isUndecorated = true
                this.title = title
                this.background = Color(background.toArgb())
            }
        }) {
            val dialogWindow = this.window
            var updateMinSize by remember { mutableStateOf(true) }

            AlchitryTheme {
                Layout(
                    content = {
                        Column {
                            WindowDecoration { onClose() }
                            Surface(Modifier.fillMaxSize()) {
                                content()
                            }
                        }
                    }
                ) { measurables, constraints ->
                    val minX = measurables.maxOf { it.minIntrinsicWidth(Int.MAX_VALUE) }
                    val minY = measurables.maxOf { it.minIntrinsicHeight(minX) }

                    if (updateMinSize) {
                        updateMinSize = false
                        // some window manages seem to include the window decorations while others don't
                        // the offsets adjust for window decorations
                        val offsetX = dialogWindow.size.width - dialogWindow.rootPane.size.width
                        val offsetY = dialogWindow.size.height - dialogWindow.rootPane.size.height
                        Dimension(
                            (minX + offsetX).toDp().value.roundToInt(),
                            (minY + offsetY).toDp().value.roundToInt()
                        ).also {
                            dialogWindow.minimumSize = it
                            dialogWindow.maximumSize = it
                            dialogWindow.size = it
                            val x = window.location.x + window.width / 2 - dialogWindow.width / 2
                            val y = window.location.y + window.height / 2 - dialogWindow.height / 2
                            dialogWindow.location = Point(x, y)
                        }
                    }
                    val placeables = measurables.map { measurable ->
                        measurable.measure(constraints)
                    }

                    layout(constraints.maxWidth, constraints.maxHeight) {
                        placeables.forEach { placeable ->
                            placeable.placeRelative(x = 0, y = 0)
                        }
                    }
                }
            }
        }
    }
}