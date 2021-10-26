package xyz.davidsimon.interakt.test

import org.junit.jupiter.api.Test
import xyz.davidsimon.interakt.*
import xyz.davidsimon.interakt.field.ListField
import xyz.davidsimon.interakt.field.integer
import xyz.davidsimon.interakt.field.list
import xyz.davidsimon.interakt.field.text

internal class BasicFieldTest {

    @Test
    fun testTextField(): Unit = scenario {
        inputBuilder
            .append("baz")
            .newLine()

        addAssertedField(prompt.text("Foo:"), "baz")
    }

    @Test
    fun testIntField(): Unit = scenario {
        inputBuilder
            .append("42")
            .newLine()

        addAssertedField(prompt.integer("Foo:"), 42)
    }

    @Test
    fun testListField(): Unit = scenario {
        inputBuilder
            .keyDown(terminal)
            .carriageReturn()
            .newLine()

        addAssertedField(prompt.list(
            "Foo:",
            (1..10).map { ListField.Choice("Choice-$it", it) }
        ), 2)
    }

    @Test
    fun testTextListField() = scenario {
        inputBuilder
            .keyDown(terminal)
            .carriageReturn()
            .newLine()

        addAssertedField(prompt.list("Foo:", listOf(
            "one", "two", "three"
        )), "two")
    }

    @Test
    fun testTextFieldDefault() = scenario {
        inputBuilder.newLine()

        addAssertedField(prompt.text("Foo:", default = default("bar")), "bar")
    }

    @Test
    fun testIntFieldDefault() = scenario {
        inputBuilder.newLine()

        addAssertedField(prompt.integer("Foo:", default = default(42)), 42)
    }

    @Test
    fun testListFieldDefault() = scenario {
        inputBuilder
            .carriageReturn()
            .newLine()

        addAssertedField(prompt.list(
            "Foo:",
            (1..10).map { ListField.Choice("Choice-$it", it) },
            default = default(2)
        ), 2)
    }

    @Test
    fun testTextListFieldDefault() = scenario {
        inputBuilder
            .carriageReturn()
            .newLine()

        addAssertedField(prompt.list(
            "Foo:",
            listOf("one", "two", "three"),
            default = default("two")
        ), "two")
    }
}