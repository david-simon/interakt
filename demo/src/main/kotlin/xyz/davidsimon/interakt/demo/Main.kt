package xyz.davidsimon.interakt.demo

import kotlinx.coroutines.runBlocking
import xyz.davidsimon.interakt.field.*
import xyz.davidsimon.interakt.prompt

fun main() {
    runBlocking {
        prompt {
            text("text:", default = "default value")
            integer("integer:", default = 42)
            singleList("text list:", listOf("1", "2", "3"), true)
            list("multi list:", listOf(
                ListField.Choice("one", 1),
                ListField.Choice("two", 2),
                ListField.Choice("three", 3)
            ))
            password("password:", default = null)

            val res = execute()

            println("Result:")
            for((key, value) in res) {
                println("${key.promptMessage} $value")
            }
        }
    }
}