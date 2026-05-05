package org.vishaldhakad.tarang

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Tarang",
    ) {
        App()
    }
}