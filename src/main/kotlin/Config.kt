package dev.edwinchang

import kotlinx.serialization.Serializable

@Serializable
data class Config(val syscalls: List<Syscall>, val ollama: Ollama) {
    @Serializable
    data class Syscall(val code: Int, val run: String, val args: List<String>)

    @Serializable
    data class Ollama(val endpoint: String = "http://localhost:11434/api/chat", val model: String? = null)
}
