package xyz.davidsimon.interakt.field

import org.jline.keymap.BindingReader
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import xyz.davidsimon.interakt.*
import xyz.davidsimon.interakt.util.cyan
import xyz.davidsimon.interakt.util.deleteLinesAbove
import xyz.davidsimon.interakt.util.formatPromptMessage
import java.io.PrintWriter

class IntegerField(
    override val promptMessage: String,
    override val shouldPrompt: (PromptResult, Int?) -> Boolean,
    override val default: (PromptResult, Int?) -> Int?
) : PromptField<Int> {
    override suspend fun render(
        pr: PromptResult,
        terminal: Terminal,
        lineReader: LineReader,
        bindingReader: BindingReader,
        writer: PrintWriter
    ): Int? {
        var text: String? = default(pr, pr[this])?.toString()
        var retVal: Int?
        var firstTry = true
        val invalidNumberPrompt = AttributedStringBuilder()
            .style(AttributedStyle.BOLD.foreground(AttributedStyle.RED))
            .append("(Invalid number) ")
            .toAnsi()

        do {
            val rightPrompt = if (!firstTry) invalidNumberPrompt else ""

            text = lineReader.readLine(
                "${formatPromptMessage(promptMessage)} $rightPrompt",
                null,
                text
            )

            terminal.deleteLinesAbove(1)

            retVal = text.trim().toIntOrNull()
            firstTry = false


        } while (!text.isNullOrBlank() && retVal == null)


        writer.println("${formatPromptMessage(promptMessage)} ${cyan(text ?: "")}")
        writer.flush()


        return retVal
    }
}

fun Prompt.integer(
    message: String,
    shouldPrompt: ((PromptResult, Int?) -> Boolean) = promptIfNull(),
    default: ((PromptResult, Int?) -> Int?) = defaultNull()
): IntegerField = addField(IntegerField(message, shouldPrompt, default))

fun Prompt.integer(
    message: String,
    shouldPrompt: ((PromptResult, Int?) -> Boolean) = promptIfNull(),
    default: Int? = null
): IntegerField = addField(IntegerField(message, shouldPrompt, wrapDefault(default)))