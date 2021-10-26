package xyz.davidsimon.interakt.field

import org.jline.keymap.BindingReader
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import xyz.davidsimon.interakt.PromptResult
import xyz.davidsimon.interakt.alwaysPrompt
import xyz.davidsimon.interakt.defaultNull
import java.io.PrintWriter

fun wrapChoices(allowCustom: Boolean, original: (tr: PromptResult) -> List<ListField.Choice<String>>): (tr: PromptResult) -> List<ListField.Choice<String>> {
    return when(allowCustom) {
        false -> original
        true -> { tr ->
            original(tr) + listOf(ListField.Choice("Other...", ""))
        }
    }
}
class TextListField(
    promptMessage: String,
    choices: (tr: PromptResult) -> List<Choice<String>>,
    val allowCustom: Boolean,
    val customPromptMessage: String,
    shouldPrompt: (PromptResult, TextListField) -> Boolean,
    default: (PromptResult, TextListField) -> String?
) :
    ListField<String>(
        promptMessage, wrapChoices(allowCustom, choices), shouldPrompt as (PromptResult, ListField<String>) -> Boolean,
        default as (PromptResult, ListField<String>) -> String?
    ) {
    override suspend fun render(
        pr: PromptResult,
        terminal: Terminal,
        lineReader: LineReader,
        bindingReader: BindingReader,
        writer: PrintWriter
    ): String? {
        try {
            var retVal = super.render(pr, terminal, lineReader, bindingReader, writer)

            if(allowCustom && retVal == "") {
                retVal = TextField(customPromptMessage, alwaysPrompt, defaultNull)
                    .render(pr, terminal, lineReader, bindingReader, writer)
            }

            return retVal
        } catch (e: NoChoicesException) {
            if(allowCustom) {
                return TextField(promptMessage, alwaysPrompt, defaultNull)
                    .render(pr, terminal, lineReader, bindingReader, writer)
            }

            throw e
        }
    }
}