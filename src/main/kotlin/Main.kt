package dev.edwinchang

import kotlinx.serialization.json.Json
import java.io.File

fun main(args: Array<String>): Unit =
    Json.decodeFromString<Config>(File("spaim.config.json").readText()).let { config ->
        File(args.getOrElse(0) { error("No input file specified") }).readLines().asSequence()
            // remove comments
            .map { if (it.indexOf('#') != -1) it.removeRange(it.indexOf('#'), it.lastIndex) else it }
            // remove unnecessary whitespace
            .map { it.trimStart().trimEnd() }.filter { it.isNotBlank() }
            // add section markers
            .map { "" to it }.runningFold("" to "") { (section, _), (_, line) ->
                when {
                    line.startsWith(".text") -> ".text" to ""
                    line.startsWith(".data") -> ".data" to ""
                    else -> section to line.removePrefix(".word").trimStart()
                }
            }.filter { (section, line) -> section.isNotBlank() && line.isNotBlank() }
            // initialize program state
            .fold(
                initial = ProgramState(0x400024u, List(32) { null }, emptyList(), emptyList())
            ) { state: ProgramState, (section, line) ->
                state.copy(
                    instructions = state.instructions + if (section == ".text") line.split(", ")
                        .filter { it.isNotBlank() }
                        .map { it.removePrefix("0x").removeSuffix(",").toUInt(16) } else emptyList(),
                    data = state.data + if (section == ".data") line.split(", ").filter { it.isNotBlank() }
                        .map { it.removePrefix("0x").removeSuffix(",").toUInt(16) } else emptyList())
            }
            // run the loop
            .run { execLoop(config) }
    }

tailrec fun ProgramState.execLoop(config: Config): ProgramState =
    instructions[((programCounter - 0x400024u) / 4u).toInt()].let { instruction ->
        when {
            // nop
            instruction == 0u -> this
            // syscall
            instruction == 0xcu -> syscalls[registers[2] ?: 0u]?.invoke(this, config) ?: also {
                config.syscalls.firstOrNull { it.code.toUInt() == (registers[2] ?: 0u) }
                    ?.let { ProcessBuilder(listOf(it.run) + it.args).start().waitFor() }
                    ?: error("unrecognized syscall")
            }
            // R
            instruction shr 26 == 0u -> rInstructions.getOrElse(instruction and 0x3fu) { error("unrecognized R instruction") }(
                instruction shr 21 and 0x1f.toUInt(),
                instruction shr 16 and 0x1f.toUInt(),
                instruction shr 11 and 0x1f.toUInt(),
                instruction shr 6 and 0x1f.toUInt()
            )
            // J
            instruction shr 26 in setOf(2u, 3u) -> {
                jInstructions.getOrElse(instruction shr 26) { error("unrecognized J instruction") }(
                    instruction and 0x3ffffffu
                )
            }
            // I
            else -> iInstructions.getOrElse(instruction shr 26) { error("unrecognized I instruction") }(
                instruction shr 21 and 0x1fu, instruction shr 16 and 0x1fu, instruction and 0xffffu
            )
        }.let { new -> new.copy(programCounter = new.programCounter + 4u) }
    }.execLoop(config)
