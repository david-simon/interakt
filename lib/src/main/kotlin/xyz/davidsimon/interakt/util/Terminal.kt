package xyz.davidsimon.interakt.util

import org.jline.terminal.Terminal
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.jline.utils.InfoCmp
import xyz.davidsimon.interakt.PlatformSymbols

fun Terminal.deleteLinesAbove(n: Int) {
    puts(InfoCmp.Capability.parm_up_cursor, n)
    puts(InfoCmp.Capability.parm_delete_line, n)
}

fun formatPromptMessage(message: String): String {
    return AttributedStringBuilder().apply {
        style(AttributedStyle.BOLD.foreground(AttributedStyle.GREEN))
        append("? ")
        style(AttributedStyle.BOLD)
        append(message)
    }.toAnsi()
}

fun cyan(text: String): String {
    return AttributedStringBuilder().apply {
        style(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN))
        append(text)
    }.toAnsi()
}

fun formatListChoice(name: String, selected: Boolean): String {
    return AttributedStringBuilder().apply {
        if (selected) {
            style(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN))
            append(PlatformSymbols.POINTER)
        } else {
            append(" ")
        }
        append(" $name")
    }.toAnsi()
}