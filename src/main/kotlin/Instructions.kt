package dev.edwinchang

val rInstructions = mapOf<UInt, ProgramState.(rs: UInt, rt: UInt, rd: UInt, shamt: UInt) -> ProgramState>(
    // add
    0x20u to { rs, rt, rd, _ ->
        copy(
            registers = registers.toMutableList()
                .apply { if (rd != 0u) set(rd, (registers[rs] ?: 0u) + (registers[rt] ?: 0u)) })
    },
    // addu
    0x21u to { rs, rt, rd, _ ->
        copy(
            registers = registers.toMutableList()
                .apply { if (rd != 0u) set(rd, (registers[rs] ?: 0u) + (registers[rt] ?: 0u)) })
    },
    // and
    0x24u to { rs, rt, rd, _ ->
        copy(
            registers = registers.toMutableList()
                .apply { if (rd != 0u) set(rd, (registers[rs] ?: 0u) and (registers[rt] ?: 0u)) })
    },
    // jr
    0x8u to { rs, _, _, _ -> copy(programCounter = rs) },
    // nor
    0x27u to { rs, rt, rd, _ ->
        copy(
            registers = registers.toMutableList()
                .apply { if (rd != 0u) set(rd, ((registers[rs] ?: 0u) or (registers[rt] ?: 0u)).inv()) })
    },
    // or
    0x25u to { rs, rt, rd, _ ->
        copy(
            registers = registers.toMutableList()
                .apply { if (rd != 0u) set(rd, (registers[rs] ?: 0u) or (registers[rt] ?: 0u)) })
    },
    // slt
    0x2au to { rs, rt, rd, _ ->
        copy(
            registers = registers.toMutableList().apply {
                if (rd != 0u) set(
                    rd, if ((registers[rs] ?: 0u).toInt() < (registers[rt] ?: 0u).toInt()) 1u else 0u
                )
            })
    },
    // sltu
    0x2bu to { rs, rt, rd, _ ->
        copy(
            registers = registers.toMutableList()
                .apply { if (rd != 0u) set(rd, if ((registers[rs] ?: 0u) < (registers[rt] ?: 0u)) 1u else 0u) })
    },
    // sll
    0x0u to { _, rt, rd, shamt ->
        copy(
            registers = registers.toMutableList()
                .apply { if (rd != 0u) set(rd, (registers[rt] ?: 0u) shl shamt.toInt()) })
    },
    // srl
    0x2u to { _, rt, rd, shamt ->
        copy(
            registers = registers.toMutableList()
                .apply { if (rd != 0u) set(rd, (registers[rt] ?: 0u) shr shamt.toInt()) })
    },
    // sub
    0x20u to { rs, rt, rd, _ ->
        copy(
            registers = registers.toMutableList()
                .apply { if (rd != 0u) set(rd, (registers[rs] ?: 0u) - (registers[rt] ?: 0u)) })
    },
    // subu
    0x23u to { rs, rt, rd, _ ->
        copy(
            registers = registers.toMutableList()
                .apply { if (rd != 0u) set(rd, (registers[rs] ?: 0u) - (registers[rt] ?: 0u)) })
    })

@OptIn(ExperimentalStdlibApi::class)
val iInstructions = mapOf<UInt, ProgramState.(rs: UInt, rt: UInt, immediate: UInt) -> ProgramState>(
    // addi
    0x8u to { rs, rt, immediate ->
        copy(
            registers = registers.toMutableList()
                .apply { if (rt != 0u) set(rt, (registers[rs] ?: 0u) + (immediate.toInt() shl 16 shr 16).toUInt()) })
    },
    // addiu
    0x9u to { rs, rt, immediate ->
        copy(
            registers = registers.toMutableList()
                .apply { if (rt != 0u) set(rt, (registers[rs] ?: 0u) + (immediate.toInt() shl 16 shr 16).toUInt()) })
    },
    // andi
    0xcu to { rs, rt, immediate ->
        copy(
            registers = registers.toMutableList().apply { if (rt != 0u) set(rt, (registers[rs] ?: 0u) and immediate) })
    },
    // beq
    0x4u to { rs, rt, immediate ->
        copy(
            programCounter = if (registers[rs] == registers[rt]) programCounter - 4u + (immediate.toInt() shl 16 shr 14).toUInt() else programCounter
        )
    },
    // bne
    0x5u to { rs, rt, immediate ->
        copy(
            programCounter = if (registers[rs] != registers[rt]) programCounter - 4u + (immediate.toInt() shl 16 shr 14).toUInt() else programCounter
        )
    },
    // ll
    0x30u to { rs, rt, immediate ->
        copy(
            registers = registers.toMutableList().apply {
                if (rt != 0u) set(
                    rt, data[((registers[rs] ?: 0u) + (immediate.toInt() shl 16 shr 16).toUInt()) / 4u]
                )
            })
    },
    // lui
    0xfu to { _, rt, immediate ->
        copy(registers = registers.toMutableList().apply { if (rt != 0u) set(rt, immediate shl 16) })
    },
    // lw
    0x23u to { rs, rt, immediate ->
        copy(
            registers = registers.toMutableList().apply {
                if (rt != 0u) set(
                    rt, data[((registers[rs] ?: 0u) + (immediate.toInt() shl 16 shr 16).toUInt()) / 4u]
                )
            })
    },
    // ori
    0xdu to { rs, rt, immediate ->
        copy(
            registers = registers.toMutableList().apply { if (rt != 0u) set(rt, (registers[rs] ?: 0u) or immediate) })
    },
    // slti
    0xau to { rs, rt, immediate ->
        copy(
            registers = registers.toMutableList().apply {
                if (rt != 0u) set(
                    rt, if ((registers[rs] ?: 0u).toInt() < (immediate.toInt() shl 16 shr 16)) 1u else 0u
                )
            })
    },
    // sltiu
    0xbu to { rs, rt, immediate ->
        copy(
            registers = registers.toMutableList().apply {
                if (rt != 0u) set(
                    rt, if ((registers[rs] ?: 0u) < (immediate.toInt() shl 16 shr 16).toUInt()) 1u else 0u
                )
            })
    },
    // sw
    0x2bu to { rs, rt, immediate ->
        copy(
            data = data.toMutableList().apply {
                set(((registers[rs] ?: 0u) + (immediate.toInt() shl 16 shr 16).toUInt()) / 4u, (registers[rt] ?: 0u))
            })
    })

val jInstructions = mapOf<UInt, ProgramState.(address: UInt) -> ProgramState>(
    // j
    0x2u to { address -> copy(programCounter = ((programCounter + 4u) shr 28 shl 28) + (address shl 2) - 4u) },
    // jal
    0x3u to { address ->
        copy(
            programCounter = ((programCounter + 4u) shr 28 shl 28) + (address shl 2) - 4u,
            registers = registers.toMutableList().apply { set(31, programCounter + 4u) })
    })
