package dev.edwinchang

import kotlinx.serialization.Serializable

@Serializable
data class Config(val syscalls: List<Syscall>) {
    @Serializable
    data class Syscall(val code: Int, val run: String, val args: List<String>)
}
