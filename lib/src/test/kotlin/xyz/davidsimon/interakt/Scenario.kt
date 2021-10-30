package xyz.davidsimon.interakt

import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.jline.terminal.Terminal
import xyz.davidsimon.interakt.field.PromptField
import java.io.ByteArrayOutputStream
import java.io.Writer
import kotlin.test.assertEquals

class Scenario(
    val terminal: Terminal,
    private val writer: Writer,
    val outputStream: ByteArrayOutputStream,
    val prompt: Prompt,
) {
    var inputBuilder = StringBuilder()

    private val assertedFields = mutableListOf<Pair<PromptField<*>, *>>()
    private var result: PromptResult = PromptResult(emptyMap())
    private var executed = false
    private var resultAssertion: ((PromptResult) -> Unit)? = null

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

    fun assertResult(body: (PromptResult) -> Unit) {
        resultAssertion = body
    }

    fun runResultAssertion() {
        resultAssertion?.invoke(result)
    }
}

fun scenario(setup: suspend Scenario.() -> Unit) = runBlocking {
    val (term, termWriter, outputStream) = createTerminal()
    Scenario(
        term,
        termWriter,
        outputStream,
        prompt(term) {}
    ).run {
        setup()
        executeScenario()
        assertFields()
        runResultAssertion()
    }
}