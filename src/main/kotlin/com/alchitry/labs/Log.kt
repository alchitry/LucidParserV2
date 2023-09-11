package com.alchitry.labs

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import com.alchitry.labs.ui.main.Console
import com.alchitry.labs.ui.theme.AlchitryColors
import me.tongfei.progressbar.*
import java.io.FileDescriptor
import java.io.FileOutputStream
import java.io.PrintStream
import kotlin.math.roundToInt

object Log {
    fun exception(e: Throwable) {
        printlnError("", e)
    }

    fun showError(message: Any?, throwable: Throwable? = null) {
        printlnError(message, throwable)
        // TODO: Make this show a dialog
    }

    fun println(message: Any?, color: Color) {
        println(message, SpanStyle(color = color))
    }

    fun print(message: Any?, color: Color) {
        print(message, SpanStyle(color = color))
    }

    fun println(message: Any?, style: SpanStyle? = null) {
        print(message.toString() + "\n", style)
    }

    fun print(message: Any?, style: SpanStyle? = null) {
        Console.append(message.toString(), style)
        if (style?.color != null) {
            val codes = mutableListOf<String>()
            codes.add(style.color.ansiCode())

            if (style.fontStyle == FontStyle.Italic) {
                codes.add("3")
            }

            if (style.textDecoration?.contains(TextDecoration.Underline) == true) {
                codes.add("4")
            }

            if (style.textDecoration?.contains(TextDecoration.LineThrough) == true) {
                codes.add("9")
            }

            if (style.fontWeight?.weight?.let { it > 400 } == true) {
                codes.add("1")
            }

            val prefix = "\u001B[${codes.joinToString(";")}m"
            val suffix = "\u001B[0m"
            kotlin.io.print(prefix + message + suffix)
        } else {
            kotlin.io.print(message)
        }
    }

    fun printlnError(message: Any?, throwable: Throwable? = null) {
        println(message, AlchitryColors.Error)
        if (throwable != null) {
            throwable.message?.let { println(it, AlchitryColors.Error) }
            throwable.printStackTrace()
        }
    }

    val barStyle: ProgressBarStyle = if (Env.isWindows) {
        ProgressBarStyle.ASCII
    } else {
        ProgressBarStyleBuilder().apply {
            leftBracket("\u001b[${AlchitryColors.ProgressBar.ansiCode()}m│")
            rightBracket("│\u001b[0m")
            block('█')
            fractionSymbols(" ▏▎▍▌▋▊▉")
            rightSideFractionSymbol(' ')
        }.build()
    }

    inline fun progressBar(name: String, max: Long, block: (ProgressBar) -> Unit) {
        val progressBar = ProgressBarBuilder().apply {
            setStyle(barStyle)
            setTaskName(name)
            setInitialMax(max)
            setUpdateIntervalMillis(250)
            setConsumer(ConsoleProgressBarConsumer(PrintStream(FileOutputStream(FileDescriptor.out))))
        }.build()
        progressBar.use(block)
    }

    private fun Color.ansiCode(): String {
        val r = (red * 255).roundToInt()
        val g = (green * 255).roundToInt()
        val b = (blue * 255).roundToInt()
        return "38;2;$r;$g;$b"
    }
}