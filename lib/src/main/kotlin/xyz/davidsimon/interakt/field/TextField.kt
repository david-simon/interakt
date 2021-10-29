package xyz.davidsimon.interakt.field

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jline.keymap.BindingReader
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import xyz.davidsimon.interakt.*
import xyz.davidsimon.interakt.util.cyan
import xyz.davidsimon.interakt.util.deleteLinesAbove
import xyz.davidsimon.interakt.util.formatPromptMessage
import java.io.PrintWriter

class TextField(
    override val promptMessage: String,
    override val shouldPrompt: (PromptResult, String?) -> Boolean,
    override val default: (PromptResult, String?) -> String?
) :
    PromptField<String>{

    override suspend fun render(pr: PromptResult,
                                terminal: Terminal,
                                lineReader: LineReader,
                                bindingReader: BindingReader,
                                writer: PrintWriter): String? {
        return withContext(Dispatchers.IO) {
            val retVal = lineReader.readLine("${formatPromptMessage(promptMessage)} ", null, default(pr, pr[this@TextField]))

            terminal.deleteLinesAbove(1)
            writer.println("${formatPromptMessage(promptMessage)} ${cyan(retVal)}")
            writer.flush()

            return@withContext retVal
        }.ifEmpty { null }
    }
}

fun Prompt.text(
    message: String,
    shouldPrompt: ((PromptResult, String?) -> Boolean) = promptIfNull(),
    default: ((PromptResult, String?) -> String?) = defaultNull()
): TextField = addField(TextField(message, shouldPrompt, default))

fun Prompt.text(
    message: String,
    shouldPrompt: ((PromptResult, String?) -> Boolean) = promptIfNull(),
    default: String? = null
): TextField = addField(TextField(message, shouldPrompt, wrapDefault(default)))