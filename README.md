<!--suppress HtmlDeprecatedAttribute -->
<h1 align="center">

SpAIm

[![MIT License](https://img.shields.io/badge/License-MIT-blue)](https://github.com/EdwinChang24/spaim/blob/main/LICENSE)
[![volkswagen status](https://auchenberg.github.io/volkswagen/volkswargen_ci.svg?v=1)](https://github.com/auchenberg/volkswagen)

</h1>

A cutting-edge MIPS assembly simulator built on progressive 🚀, forward-thinking 🔥 technology.

## Usage

Run the SpAIm executable and provide an assembled MIPS assembly program file:

```sh
java -jar ./spaim.jar program.asm.out
```

SpAIm will read the file and execute its instructions.

> [!TIP]
> You can generate the assembled output of a MIPS assembly program using a tool
> like [Spim](https://spimsimulator.sourceforge.net/) (`spim -assemble`).

## Configuration

SpAIm can be configured using a config file named `spaim.config.json` in the working directory.

The following keys can be used in the config file:

- `syscalls`: an array of custom `syscall` commands
- `ollama`: configuration for SpAIm's [AI integration 🔥🔥🔥](#ai-integration-)

Each custom `syscall` command should be an object with these keys:

- `code`: the integer value in `$v0` corresponding to this `syscall`
- `run`: the path to the executable to run
- `args`: an array of arguments to be passed

Here's an example of a complete config file:

```json
{
    "syscalls": [
        {
            "code": 100,
            "run": "shutdown",
            "args": [
                "now"
            ]
        },
        {
            "code": 25565,
            "run": "prismlauncher",
            "args": [
                "-l",
                "Minecraft 1.8.9",
                "-s",
                "mc.hypixel.net"
            ]
        }
    ],
    "ollama": {
        "model": "deepseek-r1:671b"
    }
}
```

This config file creates the `syscall`s `100` and `25565`. `syscall` code `100` shuts down the system (on a Unix
system), and
`syscall` code `25565` launches a Minecraft instance through [Prism Launcher](https://prismlauncher.org/).

> [!WARNING]
> If you define a `syscall` command that uses the code of a built-in `syscall`, the built-in `syscall` will take
> precedence.

## Limitations

There are some features that are not currently supported, including but not limited to:

- Accessing memory that isn't word aligned
- Assembly directives other than `.data`, `.text`, and `.word`
- Floating-point arithmetic
- Hi and Lo registers
- `mult` and `multu`
- Decent performance

However, due to SpAIm's [AI integration 🔥🔥🔥](#ai-integration-), SpAIm actually doesn't have any limitations 🚀.

## AI Integration 🚀🚀🚀

SpAIm's AI 🚀 integration 🚀 requires an [Ollama](https://ollama.com/) model to be running 🔥.
You also need to specify a model in the [config](#configuring-the-ai-integration-) 🔥.

When you use `syscall` 🚀 with `$v0 == 11434` 🔥 and `$a0` 🔥 containing the address to a string in memory 🔥, SpAIm
activates its best-of-breed, bleeding-edge AI integration 🚀🚀🚀🚀 to evaluate your prompt 🔥🔥🔥🔥🔥.

For example 🔥:

```asm
.data
    buffer: .space 200
.text
main:
    la $a0, buffer
    li $v0, 8
    syscall
    li $v0, 11434 # 🔥🔥🔥🔥🔥🔥🔥
    syscall       # 🚀🚀🚀🚀🚀🚀🚀🚀
    li $v0, 1
    syscall
    li $v0, 10
    syscall
```

Run the program above 🔥🔥 and input this prompt 🚀🚀:

```
Evaluate 2+3 and put the answer in $a0.
```

This will always 🔥🔥🔥 print out the number `5` 🔥🔥🔥🔥🔥 **100% 🚀 of 🚀 the 🚀 time** some of the time 🔥.

The AI integration 🚀🚀🚀🚀🚀 can also, in theory 🔥, and theoretically 🔥🔥 in practice 🔥🔥🔥, read the register values 🚀🚀 when
evaluating your prompt 🚀🚀🚀🚀🚀:

```asm
.data
    buffer: .space 200 # 🔥
.text
main:
    la $a0, buffer # 🔥
    li $v0, 8      # 🔥
    syscall        # 🔥
    li $t0, 3      # 🔥🔥🔥
    li $t1, 4      # 🔥🔥🔥🔥
    li $t2, 5      # 🔥🔥🔥🔥🔥
    li $v0, 11434  # 🔥🔥🔥🚀🚀🚀🔥🔥🔥
    syscall        # 🚀🚀🚀🚀🚀🔥🔥🔥🔥🔥
    move $a0, $t3  # 🔥🔥🔥
    li $v0, 1      # 🔥🔥
    syscall        # 🔥
    li $v0, 10     # 🔥
    syscall        # 🔥
```

Run the program 🔥 above 🔥 with this prompt 🚀:

```
Multiply the values of $t0, $t1, and $t2 together and put the result in $t3.
```

This has a pretty good chance 🔥🔥🔥, by my standards 🔥🔥, of printing the number `60` 🚀🚀🚀🔥🔥🔥.

### Configuring the AI Integration 🚀🚀🚀

In the `ollama` object 🔥🔥 in the [config file](#configuration) 🚀, you can specify these values 🔥🔥🔥🔥🔥🔥:

- `endpoint` 🔥: the URL 🚀🚀🚀 of the Ollama 🔥 chat endpoint 🔥🔥🔥 (default 🚀: `http://localhost:11434/api/chat`)
- `model` 🔥 (required): the name of the model to use 🔥🔥🔥 (example: `deepseek-r1:671b` 🚀🚀🚀🚀🚀🚀)

> [!NOTE]
> SpAIm's AI integration 🔥🔥🔥🔥 requires a powerful 🔥 LLM 🚀 to work properly. If the LLM 🚀 you are using runs on your
> machine 🔥🔥, it is too small 🔥🔥🔥🔥.

## Building

To build the project from source, run one of the following commands.

Mac/Linux:

```sh
./gradlew assemble
```

Windows:

```bat
.\gradlew.bat assemble
```

The executable JAR should be written to `build/libs`.

## Credits

Special thanks to NVIDIA in advance for sponsoring this project 👍.

Special unthanks to MIPS Tech LLC for not sponsoring this project 👎.

## License

SpAIm is [MIT licensed](./LICENSE).
