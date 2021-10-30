package xyz.davidsimon.interakt.field

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jline.keymap.BindingReader
import org.jline.reader.LineReader
import org.jline.terminal.Terminal
import org.jline.utils.InfoCmp
import xyz.davidsimon.interakt.*
import xyz.davidsimon.interakt.util.cyan
import xyz.davidsimon.interakt.util.deleteLinesAbove
import xyz.davidsimon.interakt.util.formatPromptMessage
import java.io.PrintWriter
import kotlin.math.absoluteValue

private const val DEL_CHAR = 127.toChar()

class PasswordField(
    override val promptMessage: String,
    override val shouldPrompt: (PromptResult, String?) -> Boolean,
    override val default: (PromptResult, String?) -> String?
) : StatefulField<String, PasswordField.RenderState>{


    class RenderState(
        var currentInput: String = "",
        override var isSubmitted: Boolean = false
    ) : StatefulField.RenderState {
        var maskLength: Int = 0
    }

    fun onInput(state: RenderState, char: Char) {
        when {
            char == '\r' || char == '\n' -> onSubmit(state)
            char == '\b' || char == DEL_CHAR -> state.currentInput = state.currentInput.dropLast(1)
            char.isISOControl() -> { }
            else -> state.currentInput = state.currentInput + char
        }

    }

    override suspend fun render(pr: PromptResult,
                                terminal: Terminal,
                                lineReader: LineReader,
                                bindingReader: BindingReader,
                                writer: PrintWriter): String? {
        return withContext(Dispatchers.IO) {
            val state = initState(pr)
            val reader = terminal.reader()

            while(!state.isSubmitted) {
                terminal.puts(InfoCmp.Capability.save_cursor)

                writer.write("${formatPromptMessage(promptMessage)} ${"*".repeat(state.currentInput.length)}")

                val originalTermAttrs = terminal.enterRawMode()
                terminal.flush()

                val char = reader.read().toChar()
                onInput(state, char)

                terminal.puts(InfoCmp.Capability.parm_delete_line, 1)
                terminal.puts(InfoCmp.Capability.restore_cursor)

                terminal.attributes = originalTermAttrs
                terminal.flush()
            }

            return@withContext state.currentInput
        }.ifEmpty { null }
    }

    override fun initState(pr: PromptResult): RenderState {
        return RenderState()
    }
}

fun Prompt.password(
    message: String,
    shouldPrompt: ((PromptResult, String?) -> Boolean) = promptIfNull(),
    default: ((PromptResult, String?) -> String?) = defaultNull()
): PasswordField = addField(PasswordField(message, shouldPrompt, default))

fun Prompt.password(
    message: String,
    shouldPrompt: ((PromptResult, String?) -> Boolean) = promptIfNull(),
    default: String? = null
): PasswordField = addField(PasswordField(message, shouldPrompt, wrapDefault(default)))