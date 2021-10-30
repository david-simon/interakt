package xyz.davidsimon.interakt

import org.jline.keymap.KeyMap
import org.jline.terminal.Attributes
import org.jline.terminal.Terminal
import org.jline.terminal.TerminalBuilder
import org.jline.utils.InfoCmp
import java.io.*

fun createTerminal(): Triple<Terminal, OutputStreamWriter, ByteArrayOutputStream> {
    val outputPipe = PipedOutputStream()
    val pipeWriter = OutputStreamWriter(outputPipe)
    val inputStream = PipedInputStream(outputPipe)
    val outputStream = ByteArrayOutputStream()

    val term = TerminalBuilder.builder()
    .streams(inputStream, outputStream)
    .build()

    term.attributes = term.attributes.apply { setInputFlag(Attributes.InputFlag.ICRNL, false) }

    return Triple(term, pipeWriter, outputStream)
}

fun StringBuilder.newLine() = this.append("\n")
fun StringBuilder.carriageReturn() = this.append("\r")
fun StringBuilder.space() = this.append(" ")
fun StringBuilder.keyDown(terminal: Terminal) = this.append(KeyMap.key(terminal, InfoCmp.Capability.key_down))