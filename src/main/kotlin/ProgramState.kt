package dev.edwinchang

data class ProgramState(
    val programCounter: UInt,
    val registers: List<UInt?>,
    val instructions: List<UInt>,
    val data: List<UInt>
)
