package xyz.davidsimon.interakt

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.jline.terminal.Terminal
import xyz.davidsimon.interakt.field.PromptField
import java.io.Writer
import kotlin.test.assertEquals

class Scenario(
    val terminal: Terminal,
    private val writer: Writer,
    val prompt: Prompt,
) {
    var inputBuilder = StringBuilder()
    private val assertedFields = mutableListOf<Pair<PromptField<*>, *>>()
    private var result: PromptResult = PromptResult(emptyMap())
    private var executed = false

    suspend fun executeScenario() = coroutineScope {
        if (executed) return@coroutineScope result
        executed = true

        val executeJob = async { prompt.execute() }

        writer.write(inputBuilder.toString())
        writer.close()

        result = executeJob.await()
        return@coroutineScope result
    }

    fun <T> addAssertedField(promptField: PromptField<T>, value: T) {
        assertedFields.add(promptField to value)
    }

    fun assertFields() {
        for((field, value) in assertedFields) {
            assertEquals(value, result[field])
        }
    }
}

fun scenario(setup: suspend Scenario.() -> Unit) = runBlocking {
    val (term, termWriter) = createTerminal()
    Scenario(
        term,
        termWriter,
        prompt(term) {}
    ).run {
        setup()
        executeScenario()
        assertFields()
    }
}