package xyz.davidsimon.interakt.demo

import kotlinx.coroutines.runBlocking
import xyz.davidsimon.interakt.field.integer
import xyz.davidsimon.interakt.field.list
import xyz.davidsimon.interakt.field.text
import xyz.davidsimon.interakt.prompt

fun main() {
    runBlocking {
        prompt {
            val text = text("foo:", default = "default value")
            val integer = integer("bar:", default = 42)
            val textList = list("baz:", listOf("1", "2", "3"), true)

            val res = execute()

            println("Result:")
            for((key, value) in res) {
                println("${key.promptMessage} $value")
            }
        }
    }
}