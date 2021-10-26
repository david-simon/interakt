package xyz.davidsimon.interakt

import org.jline.keymap.KeyMap
import org.jline.terminal.Attributes
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp
import java.io.*

fun createTerminal(): Pair<Terminal, Writer> {
    val outputPipe = PipedOutputStream()
    val pipeWriter = OutputStreamWriter(outputPipe)
    val inputStream = PipedInputStream(outputPipe)
    val outputStream = ByteArrayOutputStream()

    val term = TerminalBuilder.builder()
    .streams(inputStream, outputStream)
    .build()

    term.attributes = term.attributes.apply { setInputFlag(Attributes.InputFlag.ICRNL, false) }

    return Pair(term, pipeWriter)
    }

fun StringBuilder.newLine() = this.appendLine("\n")
fun StringBuilder.carriageReturn() = this.appendLine("\r")
fun StringBuilder.keyDown(terminal: Terminal) = this.append(KeyMap.key(terminal, InfoCmp.Capability.key_down))