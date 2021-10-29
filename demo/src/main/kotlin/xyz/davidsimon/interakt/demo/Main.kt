package xyz.davidsimon.interakt.demo

import kotlinx.coroutines.runBlocking
import xyz.davidsimon.interakt.field.*
import xyz.davidsimon.interakt.prompt

fun main() {
    runBlocking {
        prompt {
            text("foo:", default = "default value")
            integer("bar:", default = 42)
            singleList("baz:", listOf("1", "2", "3"), true)
            list("multi:", listOf(
                ListField.Choice("one", 1),
                ListField.Choice("two", 2),
                ListField.Choice("three", 3)
            ))

            println("Result:")
            for((key, value) in execute()) {
                println("${key.promptMessage} $value")
            }
        }
    }
}