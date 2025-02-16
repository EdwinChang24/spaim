package dev.edwinchang

import kotlin.system.exitProcess

val syscalls = mapOf<UInt, ProgramState.() -> ProgramState>(
    // print int
    1u to { apply { print(registers[4]?.toInt() ?: 0) } },
    // print string
    4u to {
        apply {
            print(data.subList((((registers[4] ?: 0u) - 0x10000000u) / 4u).toInt(), data.size).asSequence().map { u ->
                listOf(u shl 24, u shl 16, u shl 8, u).map { it shr 24 }
            }.flatten().takeWhile { it != 0u }.map { it.toInt().toChar() }.joinToString(""))
        }
    },
    // read int
    5u to { copy(registers = registers.toMutableList().apply { set(2, readln().toUInt()) }) },
    // read string
    8u to {
        readln().take(registers[5]?.toInt() ?: 0).map { it.code.toUInt() }.plus(0u).chunked(4).map { w ->
            w.getOrElse(0) { 0u } + (w.getOrElse(1) { 0u } shl 8) + (w.getOrElse(2) { 0u } shl 16) + (w.getOrElse(3) { 0u } shl 24)
        }.let { new ->
            copy(data = data.mapIndexed { index, u ->
                if (index in ((registers[4]?.minus(0x10000000u)?.toInt()) ?: 0)..<((registers[4]?.minus(0x10000000u)
                        ?.toInt() ?: 0) + new.size)
                ) new[index - (registers[4]?.minus(0x10000000u)?.toInt() ?: 0)]
                else u
            })
        }
    },
    // exit
    10u to { exitProcess(0) },
)
