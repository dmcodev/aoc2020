package day08

import util.loadInput

fun main() {
    val commands = loadInput(8)
    val instructions = Parser.parse(commands)

    val loopedProgram = Program(instructions)
    println(loopedProgram.run().output)

    val fixedProgramResult = instructions.asSequence()
        .mapIndexed { offset, instruction ->
            when (instruction) {
                is Jmp -> offset to Nop(instruction.argument)
                is Nop -> offset to Jmp(instruction.argument)
                else -> null
            }
        }
        .filterNotNull()
        .map { instructions.toMutableList().apply { set(it.first, it.second) } }
        .map { Program(it).run() }
        .first { it.status == ProgramStatus.COMPLETED }

    println(fixedProgramResult.output)
}

class Program(
    private val instructions: List<Instruction>
) {
    private val memory = Memory()
    private val trace = mutableSetOf<Int>()

    private var status = ProgramStatus.NOT_STARTED

    private var offset by memory::offset
    private var accumulator by memory::accumulator

    fun run(): ProgramResult {
        updateStatus()
        while (status == ProgramStatus.RUNNING) {
            executeInstruction()
            updateStatus()
        }
        return ProgramResult(status, accumulator)
    }

    private fun executeInstruction() {
        trace.add(offset)
        currentInstruction().run(memory)
        offset++
    }

    private fun updateStatus() {
        status = when {
            isCompleted() -> ProgramStatus.COMPLETED
            isOffsetInvalid() -> ProgramStatus.ERROR_ILLEGAL_OFFSET
            isLoopDetected() -> ProgramStatus.ERROR_INFINITE_LOOP
            else -> ProgramStatus.RUNNING
        }
    }

    private fun currentInstruction(): Instruction = instructions[offset]

    private fun isCompleted() = offset == instructions.size

    private fun isOffsetInvalid() = offset < 0 || offset > instructions.size

    private fun isLoopDetected() = trace.contains(offset)
}

data class Memory(
    var offset: Int = 0,
    var accumulator: Int = 0
)

enum class ProgramStatus {
    NOT_STARTED,
    RUNNING,
    COMPLETED,
    ERROR_ILLEGAL_OFFSET,
    ERROR_INFINITE_LOOP
}

data class ProgramResult(
    val status: ProgramStatus,
    val output: Int
)

object Parser {

    private val pattern = Regex("(\\w+)\\s+([-+]\\d+)")

    fun parse(commands: List<String>): List<Instruction> =
        commands.map { parse(it) }

    private fun parse(command: String): Instruction {
        val match = requireNotNull(pattern.find(command))
        val type = match.group(1)
        val argument = match.group(2).toInt()
        return when (type) {
            "acc" -> Acc(argument)
            "jmp" -> Jmp(argument)
            "nop" -> Nop(argument)
            else -> error("Unsupported command type: $type")
        }
    }
}

sealed class Instruction(val argument: Int) {
    abstract fun run(memory: Memory)
}

class Acc(argument: Int) : Instruction(argument) {
    override fun run(memory: Memory) {
        memory.accumulator += argument
    }
}

class Jmp(argument: Int) : Instruction(argument) {
    override fun run(memory: Memory) {
        memory.offset += argument - 1
    }
}

class Nop(argument: Int) : Instruction(argument) {
    override fun run(memory: Memory) {}
}

private fun MatchResult.group(index: Int): String =
    requireNotNull(groups[index]?.value)