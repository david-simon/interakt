package xyz.davidsimon.interakt

sealed class Symbols(
    val TICK: String,
    val INFO: String,
    val WARNING: String,
    val CROSS: String,
    val LOZENGE_OUTLINE: String,
    val POINTER: String,
    val ELLIPSIS: String = "…",
)

object UnixSymbols : Symbols(
    TICK = "✔",
    INFO = "ℹ",
    WARNING = "⚠",
    CROSS = "✖",
    LOZENGE_OUTLINE = "◇",
    POINTER = "❯"
)

object WindowsSymbols : Symbols(
    TICK = "√",
    INFO = "ii",
    WARNING = "!!",
    CROSS = "×",
    LOZENGE_OUTLINE = "◊",
    POINTER = ">"
)

val PlatformSymbols = when (System.getProperty("os.name").lowercase().contains("win")) {
    true -> WindowsSymbols
    false -> UnixSymbols
}