package xyz.davidsimon.interakt.field

import org.jline.keymap.BindingReader
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import xyz.davidsimon.interakt.PromptResult
import java.io.PrintWriter

sealed class PromptField<T>(
    val promptMessage: String,
    val shouldPrompt: (PromptResult, PromptField<T>) -> Boolean,
    val default: (PromptResult, PromptField<T>) -> T?
) {
    abstract suspend fun render(
        pr: PromptResult,
        terminal: Terminal,
        lineReader: LineReader,
        bindingReader: BindingReader,
        writer: PrintWriter
    ): T?
}