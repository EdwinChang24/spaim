package dev.edwinchang

import kotlinx.serialization.Serializable

@Serializable
data class Config(val syscalls: List<Syscall> = emptyList(), val ollama: Ollama = Ollama()) {
    @Serializable
    data class Syscall(val code: Int, val run: String, val args: List<String> = emptyList())

    @Serializable
    data class Ollama(val endpoint: String = "http://localhost:11434/api/chat", val model: String? = null)
}
