package xyz.davidsimon.interakt.util

fun Int.wrapAround(min: Int, max: Int): Int {
    return when {
        this < min -> max
        this > max -> min
        else -> this
    }
}