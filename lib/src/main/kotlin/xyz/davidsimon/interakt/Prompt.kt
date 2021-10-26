package xyz.davidsimon.interakt

import kotlinx.coroutines.coroutineScope
import org.jline.keymap.BindingReader
import org.jline.reader.LineReaderBuilder
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import xyz.davidsimon.interakt.field.*


fun <T> promptIfNull(): (PromptResult, PromptField<T>) -> Boolean {
    return { pr: PromptResult, pf: PromptField<T> -> pr[pf] == null }
}

fun <T> default(vararg defaults: T): (Any, Any) -> T? {
    return { _: Any, _: Any -> defaults.find { it != null } }
}

val alwaysPrompt = { _: Any, _: Any -> true }
val defaultNull = { _: Any, _: Any -> null }

open class Prompt(
    private val terminal: Terminal = TerminalBuilder.terminal(),
) {
    private val lineReader = LineReaderBuilder.builder()
        .terminal(terminal)
        .build()

    private val writer = terminal.writer()!!
    private val reader = terminal.reader()!!
    private val bindingReader = BindingReader(reader)
    private val fields = mutableListOf<PromptField<out Any>>()

    fun <T : Any, F : PromptField<T>> addField(field: F): F {
        fields.add(field)
        return field
    }

    suspend fun execute(buildInitial: PromptResultBuilder.() -> Unit = {}): PromptResult = coroutineScope {
        val resultBuilder = PromptResultBuilder()
        buildInitial(resultBuilder)

        val results: MutableMap<PromptField<out Any>, Any?> = mutableMapOf()
        results.putAll(resultBuilder.results)

        for (field in fields) {
            field as PromptField<Any>

            val partialResults = PromptResult(results)
            if (!field.shouldPrompt(partialResults, field)) continue

            results[field] = field.render(partialResults, terminal, lineReader, bindingReader, writer)
        }

        return@coroutineScope PromptResult(results)
    }
}

suspend fun prompt(terminal: Terminal, body: suspend Prompt.() -> Unit): Prompt {
    return Prompt(terminal).apply {
        body()
    }
}

suspend fun prompt(body: suspend Prompt.() -> Unit): Prompt {
    return prompt(TerminalBuilder.terminal(), body)
}

data class PromptResult(private val results: Map<PromptField<out Any>, Any?>)
    : Iterable<Map.Entry<PromptField<out Any>, Any?>> {

    operator fun <T> get(key: PromptField<T>): T? {
        return (results as Map<PromptField<T>, T?>)[key]
    }

    override fun iterator(): Iterator<Map.Entry<PromptField<out Any>, Any?>> {
        return results.iterator()
    }
}

class PromptResultBuilder {
    val results = mutableMapOf<PromptField<out Any>, Any>()

    infix fun <T : Any> PromptField<T>.init(value: T?) {
        if (value != null) results[this] = value
    }
}

open class PromptException(message: String) : RuntimeException(message)