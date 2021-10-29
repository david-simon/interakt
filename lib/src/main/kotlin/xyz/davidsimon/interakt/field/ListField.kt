package xyz.davidsimon.interakt.field

import org.jline.keymap.BindingReader
import org.jline.keymap.KeyMap
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import org.jline.utils.InfoCmp
import xyz.davidsimon.interakt.*
import xyz.davidsimon.interakt.util.cyan
import xyz.davidsimon.interakt.util.deleteLinesAbove
import xyz.davidsimon.interakt.util.formatPromptMessage
import xyz.davidsimon.interakt.util.wrapAround
import java.io.PrintWriter
import java.util.*

open class ListField<T>(
    override val promptMessage: String,
    val choices: (PromptResult) -> List<Choice<T>>,
    override val shouldPrompt: (PromptResult, List<T>?) -> Boolean,
    override val default: (PromptResult, List<T>?) -> List<T>?
) :
    StatefulField<List<T>, ListField.RenderState<T>> {

    data class Choice<T>(val name: String, val value: T)
    class NoChoicesException(message: String) : PromptException(message)

    enum class ListOperations {
        UP,
        DOWN,
        SELECT,
        SUBMIT
    }

    class RenderState<T>(
        val choices: MutableList<Choice<T>>,
        var currentRow: Int = 0,
        val selectedRows: BitSet = BitSet(choices.size),
        override var isSubmitted: Boolean = false
    ) : StatefulField.RenderState

    protected open fun onSelect(state: RenderState<T>) {
        state.selectedRows.flip(state.currentRow)
    }

    protected open fun onKeyUp(state: RenderState<T>) {
        state.currentRow = (state.currentRow - 1).wrapAround(0, state.choices.lastIndex)
    }

    protected open fun onKeyDown(state: RenderState<T>) {
        state.currentRow = (state.currentRow + 1).wrapAround(0, state.choices.lastIndex)
    }

    override fun initState(pr: PromptResult): RenderState<T> {
        val choices = choices(pr)
        if (choices.isEmpty()) {
            throw NoChoicesException("no choices supplied for field")
        }

        val state = RenderState(choices.toMutableList())

        val defaults = default(pr, pr[this])
        val defaultIndexes = choices.withIndex().filter { defaults?.contains(it.value.value) == true}.map { it.index }
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
    ): List<T> {

        val keymap = KeyMap<ListOperations>().apply {
            bind(ListOperations.UP, KeyMap.key(terminal, InfoCmp.Capability.key_up))
            bind(ListOperations.DOWN, KeyMap.key(terminal, InfoCmp.Capability.key_down))
            bind(ListOperations.SELECT, " ")
            bind(ListOperations.SUBMIT, "\r")
        }

        writer.println(formatPromptMessage(promptMessage))

        val originalTermAttrs = terminal.enterRawMode()
        terminal.puts(InfoCmp.Capability.keypad_xmit)
        writer.flush()

        val state = initState(pr)

        while (!state.isSubmitted) {
            for (index in state.choices.indices) {
                writer.println(formatListChoice(index, state))
            }
            writer.flush()

            when (bindingReader.readBinding(keymap)) {
                ListOperations.UP -> onKeyUp(state)
                ListOperations.DOWN -> onKeyDown(state)
                ListOperations.SELECT -> onSelect(state)
                ListOperations.SUBMIT, null -> onSubmit(state)
            }

            terminal.puts(InfoCmp.Capability.parm_up_cursor, state.choices.size)
            terminal.puts(InfoCmp.Capability.parm_delete_line, state.choices.size)
        }
        terminal.attributes = originalTermAttrs
        terminal.puts(InfoCmp.Capability.keypad_local)
        terminal.flush()

        val retVal = state.choices.withIndex().filter { state.selectedRows[it.index] }.map { it.value.value }
        val retName = state.choices.getOrNull(state.currentRow)?.name

        terminal.deleteLinesAbove(1)
        writer.println("${formatPromptMessage(promptMessage)} ${cyan(retName ?: "")}")

        terminal.deleteLinesAbove(1)
        writer.println("${formatPromptMessage(promptMessage)} ${cyan(retName ?: "")}")

        return retVal
    }

    protected open fun formatListChoice(index: Int, state: RenderState<T>): String {
        val highlighted = state.currentRow == index
        val selected = state.selectedRows[index]
        val name = state.choices[index].name

        return AttributedStringBuilder().apply {
            if (highlighted) {
                style(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN))
                append(PlatformSymbols.POINTER)
            } else {
                append(" ")
            }

            append("[")
            if (selected) {
                append(PlatformSymbols.TICK)
            } else {
                append(" ")
            }
            append("]")

            append(" $name")
        }.toAnsi()
    }
}

fun <T : Any> Prompt.list(
    message: String,
    choices: (PromptResult) -> List<ListField.Choice<T>>,
    shouldPrompt: ((PromptResult, List<T>?) -> Boolean) = promptIfNull(),
    default: ((PromptResult, List<T>?) -> List<T>?) = defaultNull()
): ListField<T> =
    addField(ListField(message, choices, shouldPrompt, default))

fun <T : Any> Prompt.list(
    message: String,
    choices: List<ListField.Choice<T>>,
    shouldPrompt: ((PromptResult, List<T>?) -> Boolean) = promptIfNull(),
    default: ((PromptResult, List<T>?) -> List<T>?) = defaultNull()
): ListField<T> =
    addField(ListField(message, { choices }, shouldPrompt, default))