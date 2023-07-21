package year2022

import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(10, 2022, PartA10(), PartB10()).run()
}


open class PartA10 : Part() {
    internal lateinit var sequence: Sequence<Int>

    override fun parse(text: String) {
        val lines = text.split("\n")
        sequence = sequence {
            var x = 1
            lines.forEach {
                val parts = it.split(" ")
                when (parts[0]) {
                    "noop" -> yield(x)
                    "addx" -> {
                        yield(x)
                        yield(x)
                        x += parts[1].toInt()
                    }
                }
            }
        }
    }

    override fun compute(): String {
        var result = 0
        val interestingCycles = listOf(20, 60, 100, 140, 180, 220)
        sequence.forEachIndexed { i, x ->
            val cycle = i + 1
            if (cycle in interestingCycles) {
                result += cycle * x
            }
        }
        return result.toString()
    }

    override val exampleAnswer: String
        get() = "13140"

    override val customExampleData: String?
        get() = day10example

}

class PartB10 : PartA10() {
    override fun compute(): String {
        sequence.forEachIndexed { i, x ->
            val col = i % 40
            print(if (col in (x - 1)..(x + 1)) "#" else ".")
            if (col == 39) {
                println()
            }
        }
        return "No automatic answer"
    }

    override val exampleAnswer: String
        get() = ""
}


private val day10example = """
addx 15
addx -11
addx 6
addx -3
addx 5
addx -1
addx -8
addx 13
addx 4
noop
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx 5
addx -1
addx -35
addx 1
addx 24
addx -19
addx 1
addx 16
addx -11
noop
noop
addx 21
addx -15
noop
noop
addx -3
addx 9
addx 1
addx -3
addx 8
addx 1
addx 5
noop
noop
noop
noop
noop
addx -36
noop
addx 1
addx 7
noop
noop
noop
addx 2
addx 6
noop
noop
noop
noop
noop
addx 1
noop
noop
addx 7
addx 1
noop
addx -13
addx 13
addx 7
noop
addx 1
addx -33
noop
noop
noop
addx 2
noop
noop
noop
addx 8
noop
addx -1
addx 2
addx 1
noop
addx 17
addx -9
addx 1
addx 1
addx -3
addx 11
noop
noop
addx 1
noop
addx 1
noop
noop
addx -13
addx -19
addx 1
addx 3
addx 26
addx -30
addx 12
addx -1
addx 3
addx 1
noop
noop
noop
addx -9
addx 18
addx 1
addx 2
noop
noop
addx 9
noop
noop
noop
addx -1
addx 2
addx -37
addx 1
addx 3
noop
addx 15
addx -21
addx 22
addx -6
addx 1
noop
addx 2
addx 1
noop
addx -10
noop
noop
addx 20
addx 1
addx 2
addx 2
addx -6
addx -11
noop
noop
noop
""".trimIndent()