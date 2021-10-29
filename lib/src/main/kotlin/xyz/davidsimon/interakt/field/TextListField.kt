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
    private val allowCustom: Boolean,
    private val customPromptMessage: String,
    shouldPrompt: (PromptResult, String?) -> Boolean,
    default: (PromptResult, String?) -> String?
) :
    SingleValueListField<String>(
        promptMessage,
        wrapChoices(allowCustom, choices),
        shouldPrompt,
        default
    ) {

    override fun initState(pr: PromptResult): RenderState<String> {
        val state = super.initState(pr)

        val defaults = default(pr, pr[this])
        state.choices.sortBy { defaults?.contains(it.value) == false }

        state.selectedRows.clear()
        val defaultIndexes = state.choices.withIndex().filter { defaults?.contains(it.value.value) == true}.map { it.index }
        for(index in defaultIndexes) {
            state.selectedRows.set(index, true)
        }

        return state
    }

    override suspend fun render(
        pr: PromptResult,
        terminal: Terminal,
        lineReader: LineReader,
        bindingReader: BindingReader,
        writer: PrintWriter
    ): List<String> {
        try {
            var retVal = super.render(pr, terminal, lineReader, bindingReader, writer)

            println("allowCustom: $allowCustom | isEmpty: ${retVal.firstOrNull() == ""}")
            if(allowCustom && retVal.firstOrNull() == "") {
                retVal = renderCustomField(pr, terminal, lineReader, bindingReader, writer)
            }

            return retVal
        } catch (e: NoChoicesException) {
            if(allowCustom) {
                return renderCustomField(pr, terminal, lineReader, bindingReader, writer)
            }

            throw e
        }
    }

    suspend fun renderCustomField(pr: PromptResult, terminal: Terminal, lineReader: LineReader, bindingReader: BindingReader, writer: PrintWriter): List<String> {
        return listOf(
            TextField(customPromptMessage, alwaysPrompt(), defaultNull())
                .render(pr, terminal, lineReader, bindingReader, writer)
        ).mapNotNull { it }
    }
}