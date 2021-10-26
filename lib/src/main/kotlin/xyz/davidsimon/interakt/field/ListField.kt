package xyz.davidsimon.interakt.field

import org.jline.keymap.BindingReader
import org.jline.keymap.KeyMap
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.jline.utils.InfoCmp
import xyz.davidsimon.interakt.*
import xyz.davidsimon.interakt.util.*
import java.io.PrintWriter
import kotlin.math.max

open class ListField<T>(
    promptMessage: String,
    val choices: (PromptResult) -> List<Choice<T>>,
    shouldPrompt: (PromptResult, ListField<T>) -> Boolean,
    default: (PromptResult, ListField<T>) -> T?
) :
    PromptField<T>(
        promptMessage,
        shouldPrompt as (PromptResult, PromptField<T>) -> Boolean,
        default as (PromptResult, PromptField<T>) -> T?
    ) {
    
    data class Choice<T>(val name: String, val value: T)
    class NoChoicesException(message: String) : PromptException(message)

    enum class ListOperations {
        UP,
        DOWN,
        SELECT
    }

    override suspend fun render(pr: PromptResult, terminal: Terminal, lineReader: LineReader, bindingReader: BindingReader, writer: PrintWriter): T? {
        val keymap = KeyMap<ListOperations>().apply {
            bind(ListOperations.UP, KeyMap.key(terminal, InfoCmp.Capability.key_up))
            bind(ListOperations.DOWN, KeyMap.key(terminal, InfoCmp.Capability.key_down))
            bind(ListOperations.SELECT, "\r")
        }
        
        writer.println(formatPromptMessage(promptMessage))

        val originalTermAttrs = terminal.enterRawMode()
        terminal.puts(InfoCmp.Capability.keypad_xmit)
        writer.flush()

        val choices = choices(pr)
        if (choices.isEmpty()) {
            throw NoChoicesException("no choices supplied for field")
        }


        val defaultIndex = choices.indexOfFirst {
            val default = default(pr, this)
            default != null && it.value == default
        }

        var selectedRow = max(0, defaultIndex)
        var pressedEnter = false

        while (!pressedEnter) {
            for ((index, choice) in choices.withIndex()) {
                writer.println(formatListChoice(choice.name, index == selectedRow))
            }
            writer.flush()

            val binding = bindingReader.readBinding(keymap)
            when (binding) {
                ListOperations.UP -> selectedRow = (selectedRow - 1).wrapAround(0, choices.lastIndex)
                ListOperations.DOWN -> selectedRow = (selectedRow + 1).wrapAround(0, choices.lastIndex)
                ListOperations.SELECT -> pressedEnter = true
            }

            terminal.puts(InfoCmp.Capability.parm_up_cursor, choices.size)
            terminal.puts(InfoCmp.Capability.parm_delete_line, choices.size)
        }
        terminal.attributes = originalTermAttrs
        terminal.puts(InfoCmp.Capability.keypad_local)
        terminal.flush()

        val retVal = choices.getOrNull(selectedRow)?.value
        val retName = choices.getOrNull(selectedRow)?.name

        terminal.deleteLinesAbove(1)
        writer.println("${formatPromptMessage(promptMessage)} ${cyan(retName ?: "")}")

        terminal.deleteLinesAbove(1)
        writer.println("${formatPromptMessage(promptMessage)} ${cyan(retName ?: "")}")

        return retVal
    }
}

fun <T : Any> Prompt.list(
    message: String,
    choices: (PromptResult) -> List<ListField.Choice<T>>,
    shouldPrompt: ((PromptResult, ListField<T>) -> Boolean) = promptIfNull(),
    default: ((PromptResult, ListField<T>) -> T?) = defaultNull
): ListField<T> =
    addField(ListField(message, choices, shouldPrompt, default))

fun Prompt.list(
    message: String,
    choices: (PromptResult) -> List<ListField.Choice<String>>,
    allowCustom: Boolean = false,
    customPromptMessage: String = message,
    shouldPrompt: ((PromptResult, TextListField) -> Boolean) = promptIfNull(),
    default: ((PromptResult, TextListField) -> String?) = defaultNull
): TextListField =
    addField(TextListField(message, choices, allowCustom, customPromptMessage, shouldPrompt, default))

fun <T : Any> Prompt.list(
    message: String,
    choices: List<ListField.Choice<T>>,
    shouldPrompt: ((PromptResult, ListField<T>) -> Boolean) = promptIfNull(),
    default: ((PromptResult, ListField<T>) -> T?) = defaultNull
): ListField<T> =
    addField(ListField(message, { choices }, shouldPrompt, default))

fun Prompt.list(
    message: String,
    choices: List<String>,
    allowCustom: Boolean = false,
    customPromptMessage: String = message,
    shouldPrompt: ((PromptResult, TextListField) -> Boolean) = promptIfNull(),
    default: ((PromptResult, TextListField) -> String?) = defaultNull
): TextListField =
    addField(
        TextListField(
            message,
            { choices.map { ListField.Choice(it, it) } },
            allowCustom,
            customPromptMessage,
            shouldPrompt,
            default
        )
    )