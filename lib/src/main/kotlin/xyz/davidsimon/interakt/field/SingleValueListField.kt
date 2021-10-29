package xyz.davidsimon.interakt.field

import org.jline.utils.AttributedStringBuilder
import org.jline.utils.AttributedStyle
import xyz.davidsimon.interakt.*

open class SingleValueListField<T>(
    promptMessage: String,
    choices: (PromptResult) -> List<Choice<T>>,
    shouldPrompt: (PromptResult, T?) -> Boolean,
    default: (PromptResult, T?) -> T?
) :
    ListField<T>(
        promptMessage,
        choices,
        { pr, value -> shouldPrompt(pr, value?.firstOrNull()) },
        { pr, value -> listOf(default(pr, value?.firstOrNull())).mapNotNull { it } }
    ) {


    override fun onSelect(state: RenderState<T>) { }

    override fun onSubmit(state: RenderState<T>) {
        state.selectedRows.set(state.currentRow, true)
        super.onSubmit(state)
    }

    override fun formatListChoice(index: Int, state: RenderState<T>): String {
        val highlighted = state.currentRow == index
        val name = state.choices[index].name

        return AttributedStringBuilder().apply {
            if (highlighted) {
                style(AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN))
                append(PlatformSymbols.POINTER)
            } else {
                append(" ")
            }

            append(" $name")
        }.toAnsi()
    }
}

fun <T : Any> Prompt.singleList(
    message: String,
    choices: (PromptResult) -> List<ListField.Choice<T>>,
    shouldPrompt: ((PromptResult, T?) -> Boolean) = promptIfNull(),
    default: ((PromptResult, T?) -> T?) = defaultNull()
): SingleValueListField<T> =
    addField(SingleValueListField(message, choices, shouldPrompt, default))

fun Prompt.singleList(
    message: String,
    choices: (PromptResult) -> List<ListField.Choice<String>>,
    allowCustom: Boolean = false,
    customPromptMessage: String = message,
    shouldPrompt: ((PromptResult, String?) -> Boolean) = promptIfNull(),
    default: ((PromptResult, String?) -> String?) = defaultNull()
): TextListField =
    addField(TextListField(message, choices, allowCustom, customPromptMessage, shouldPrompt, default))

fun <T : Any> Prompt.singleList(
    message: String,
    choices: List<ListField.Choice<T>>,
    shouldPrompt: ((PromptResult, T?) -> Boolean) = promptIfNull(),
    default: ((PromptResult, T?) -> T?) = defaultNull()
): SingleValueListField<T> =
    addField(SingleValueListField(message, { choices }, shouldPrompt, default))

fun Prompt.singleList(
    message: String,
    choices: List<String>,
    allowCustom: Boolean = false,
    customPromptMessage: String = message,
    shouldPrompt: ((PromptResult, String?) -> Boolean) = promptIfNull(),
    default: ((PromptResult, String?) -> String?) = defaultNull()
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