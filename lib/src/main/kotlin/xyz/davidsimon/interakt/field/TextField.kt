package xyz.davidsimon.interakt.field

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jline.keymap.BindingReader
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import xyz.davidsimon.interakt.Prompt
import xyz.davidsimon.interakt.PromptResult
import xyz.davidsimon.interakt.defaultNull
import xyz.davidsimon.interakt.promptIfNull
import xyz.davidsimon.interakt.util.cyan
import xyz.davidsimon.interakt.util.deleteLinesAbove
import xyz.davidsimon.interakt.util.formatPromptMessage
import java.io.PrintWriter

class TextField(
    promptMessage: String,
    shouldPrompt: (PromptResult, TextField) -> Boolean,
    default: (PromptResult, TextField) -> String?
) :
    PromptField<String>(
        promptMessage,
        shouldPrompt as (PromptResult, PromptField<String>) -> Boolean,
        default as (PromptResult, PromptField<String>) -> String?
    ) {

    override suspend fun render(pr: PromptResult,
                                terminal: Terminal,
                                lineReader: LineReader,
                                bindingReader: BindingReader,
                                writer: PrintWriter): String? {
        return withContext(Dispatchers.IO) {
            val retVal = lineReader.readLine("${formatPromptMessage(promptMessage)} ", null, default(pr, this@TextField))

            terminal.deleteLinesAbove(1)
            writer.println("${formatPromptMessage(promptMessage)} ${cyan(retVal)}")
            writer.flush()

            return@withContext retVal
        }.ifEmpty { null }
    }
}

fun Prompt.text(
    message: String,
    shouldPrompt: ((PromptResult, TextField) -> Boolean) = promptIfNull(),
    default: ((PromptResult, TextField) -> String?) = defaultNull
): TextField = addField(TextField(message, shouldPrompt, default))