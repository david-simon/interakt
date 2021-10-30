package xyz.davidsimon.interakt.test

import org.junit.jupiter.api.Test
import xyz.davidsimon.interakt.*
import xyz.davidsimon.interakt.field.*
import kotlin.test.assertEquals
import kotlin.test.assertFalse

internal class BasicFieldTest {

    @Test
    fun testTextField(): Unit = scenario {
        inputBuilder
            .append("baz")
            .newLine()

        addAssertedField(prompt.text("Foo:", default = null), "baz")
    }

    @Test
    fun testIntField(): Unit = scenario {
        inputBuilder
            .append("42")
            .newLine()

        addAssertedField(prompt.integer("Foo:", default = null), 42)
    }

    @Test
    fun testListField(): Unit = scenario {
        inputBuilder
            .space()
            .keyDown(terminal)
            .space()
            .carriageReturn()
            .newLine()

        addAssertedField(prompt.list(
            "Foo:",
            (1..10).map { ListField.Choice("Choice-$it", it) }
        ), listOf(1, 2))
    }

    @Test
    fun testSingleValueListField(): Unit = scenario {
        inputBuilder
            .keyDown(terminal)
            .carriageReturn()
            .newLine()

        addAssertedField(prompt.singleList(
            "Foo:",
            (1..10).map { ListField.Choice("Choice-$it", it) }
        ), listOf(2))
    }

    @Test
    fun testTextListField() = scenario {
        inputBuilder
            .keyDown(terminal)
            .carriageReturn()
            .newLine()

        addAssertedField(prompt.singleList("Foo:", listOf(
            "one", "two", "three"
        )), listOf("two"))
    }

    @Test
    fun testPasswordField() = scenario {
        inputBuilder
            .append("baz")
            .newLine()

        addAssertedField(prompt.password("Foo:", default = defaultNull()), "baz")
    }

    @Test
    fun testTextFieldDefault() = scenario {
        inputBuilder.newLine()

        addAssertedField(prompt.text("Foo:", default = wrapDefault("bar")), "bar")
    }

    @Test
    fun testIntFieldDefault() = scenario {
        inputBuilder.newLine()

        addAssertedField(prompt.integer("Foo:", default = wrapDefault(42)), 42)
    }

    @Test
    fun testListFieldDefault() = scenario {
        inputBuilder
            .carriageReturn()
            .newLine()

        addAssertedField(prompt.list(
            "Foo:",
            (1..10).map { ListField.Choice("Choice-$it", it) },
            default = wrapDefault(listOf(2))
        ), listOf(2))
    }

    @Test
    fun testTextListFieldDefault() = scenario {
        inputBuilder
            .carriageReturn()
            .newLine()

        addAssertedField(prompt.singleList(
            "Foo:",
            listOf("one", "two", "three"),
            default = wrapDefault("two")
        ), listOf("two"))
    }

    @Test
    fun testPasswordFieldOutput() = scenario {
        inputBuilder
            .append("baz")
            .newLine()

        prompt.password("Foo:", default = defaultNull())

        assertResult {
            val output = outputStream.toString().substringAfterLast("Foo:", "")
            val startCharCount = output.filter { it == '*' }.length

            assertFalse(output.contains("baz"))
            assertEquals(3, startCharCount)
        }
    }
}