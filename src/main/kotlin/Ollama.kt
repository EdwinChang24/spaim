package dev.edwinchang

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

@OptIn(ExperimentalStdlibApi::class)
fun ProgramState.ollama(config: Config) = runBlocking {
    HttpClient {
        install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        install(HttpTimeout) { requestTimeoutMillis = Long.MAX_VALUE }
    }.post(config.ollama.endpoint) {
        contentType(ContentType.Application.Json)
        @Suppress("DuplicatedCode") setBody(
            OllamaRequest(
                model = config.ollama.model ?: error("no ollama model specified"),
                messages = listOf(
                    OllamaRequest.Message(role = "system", content = systemPrompt),
                    OllamaRequest.Message(
                        role = "system",
                        content = "These are the current register values:\n" + registers.mapIndexed { index, value -> "Register $index: ${value ?: 0} (0x${value?.toHexString() ?: 0})" }
                            .joinToString("\n")),
                    OllamaRequest.Message(
                        role = "user",
                        content = data.subList((((registers[4] ?: 0u) - 0x10000000u) / 4u).toInt(), data.size)
                            .asSequence().map { u ->
                                listOf(u shl 24, u shl 16, u shl 8, u).map { it shr 24 }
                            }.flatten().takeWhile { it != 0u }.map { it.toInt().toChar() }.joinToString("")
                    )
                )
            )
        )
    }.body<OllamaResponse>().let { Json.decodeFromString<OllamaResponse.Content>(it.message.content).updates }
        .let { updates ->
            copy(registers = registers.mapIndexed { index, value ->
                updates.firstOrNull { it.register == index }?.newValue?.toUInt() ?: value
            })
        }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
data class OllamaRequest(
    val model: String,
    val messages: List<Message>,
    @EncodeDefault val format: JsonObject = responseSchema,
    @EncodeDefault val stream: Boolean = false
) {
    @Serializable
    data class Message(val role: String, val content: String)
}

@Serializable
data class OllamaResponse(val message: Message) {
    @Serializable
    data class Message(val content: String)

    @Serializable
    data class Content(val updates: List<Update>) {
        @Serializable
        data class Update(val register: Int, val newValue: Int)
    }
}

val responseSchema by lazy {
    Json.decodeFromString<JsonObject>(
        """{
  "type": "object",
  "properties": {
    "updates": {
      "type": "array",
      "items": {
        "type": "object",
        "properties": {
          "register": {
            "type": "integer",
            "minimum": 1,
            "maximum": 31
          },
          "newValue": {
            "type": "integer",
            "minimum": 0,
            "maximum": 2147483647
          }
        },
        "required": [
          "register",
          "newValue"
        ]
      }
    }
  },
  "required": [
    "updates"
  ]
}"""
    )
}

const val systemPrompt = $$"""
You are a MIPS assembly simulator that can read arbitrary English instructions and update the registers accordingly.
In the next message, you will receive a list of the current register values.
Then, the user will send an instruction in English.
Respond with a JSON object that indicates which registers need to be updated, and what the new value is for each of these registers.
This JSON object must have an array of objects with keys "register" to indicate a register number between 1 and 31 (0 cannot be updated) and "newValue" to indicate the new value of the register.
You do not have access to the instruction or data memory, so if the user's instruction requires them, do not update anything.
For convenience, here is the list of register names and uses:
- Register 0 is $zero, the constant value 0.
- Register 1 is $at, the assembler temporary.
- Registers 2 and 3 are $v0 and $v1, the values for function results and expression evaluation.
- Registers 4 through 7 are $a0 through $a3, the arguments.
- Registers 8 through 15 are $t0 through $t7, the temporaries.
- Registers 16 through 23 are $s0 through $s7, the saved temporaries.
- Registers 24 and 25 are $t8 and $t9, the temporaries.
- Registers 26 and 27 are $k0 and $k1, reserved for the OS kernel.
- Register 28 is $gp, the global pointer.
- Register 29 is $sp, the stack pointer.
- Register 30 is $fp, the frame pointer.
- Register 31 is $ra, the return address.
"""
