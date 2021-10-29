package xyz.davidsimon.interakt.field

import org.jline.keymap.BindingReader
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import xyz.davidsimon.interakt.PromptResult
import java.io.PrintWriter

interface PromptField<T> {
    val promptMessage: String
    val shouldPrompt: (PromptResult, T?) -> Boolean
    val default: (PromptResult, T?) -> T?

    suspend fun render(
        pr: PromptResult,
        terminal: Terminal,
        lineReader: LineReader,
        bindingReader: BindingReader,
        writer: PrintWriter
    ): T?
}