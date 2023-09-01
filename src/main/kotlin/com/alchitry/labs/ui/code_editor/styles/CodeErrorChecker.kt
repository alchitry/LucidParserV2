package com.alchitry.labs.ui.code_editor.styles

import com.alchitry.labs.ui.code_editor.StyleToken

interface CodeErrorChecker {
    suspend fun checkText(text: String): List<StyleToken>
}