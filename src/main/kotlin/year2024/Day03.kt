package year2024

import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(3, 2024, PartA3(), PartB3()).run()
}

open class PartA3 : Part() {
    data class State(
        var enabled: Boolean,
        var value: Int,
    )

    sealed interface Instruction {
        fun execute(state: State)
    }

    protected data class Multiplication(
        val left: Int,
        val right: Int,
    ) : Instruction {
        override fun execute(state: State) {
            if (state.enabled) {
                state.value += left * right
            }
        }
    }

    protected class Do : Instruction {
        override fun execute(state: State) {
            state.enabled = true
        }
    }

    protected class Dont : Instruction {
        override fun execute(state: State) {
            state.enabled = false
        }
    }

    protected lateinit var instructions: List<Instruction>

    override fun parse(text: String) {
        val re = """mul\((\d+),(\d+)\)|do\(\)|don't\(\)""".toRegex()
        instructions =
            re
                .findAll(text)
                .map {
                    when (it.groupValues[0].substringBefore("(")) {
                        "mul" -> Multiplication(it.groupValues[1].toInt(), it.groupValues[2].toInt())
                        "do" -> Do()
                        "don't" -> Dont()
                        else -> error("Unknown instruction")
                    }
                }.toList()
    }

    override fun compute(): String {
        val state = State(true, 0)
        instructions.filterIsInstance<Multiplication>().forEach { it.execute(state) }
        return state.value.toString()
    }

    override val exampleAnswer: String
        get() = "161"
}

class PartB3 : PartA3() {
    override fun compute(): String {
        val state = State(true, 0)
        instructions.forEach { it.execute(state) }
        return state.value.toString()
    }

    override val customExampleData: String?
        get() = "xmul(2,4)&mul[3,7]!^don't()_mul(5,5)+mul(32,64](mul(11,8)undo()?mul(8,5))"

    override val exampleAnswer: String
        get() = "48"
}
