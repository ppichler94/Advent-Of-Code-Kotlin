package year2021

import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(2, 2021, PartA2(), PartB2()).run()
}


open class PartA2 : Part() {
    protected lateinit var instructions: List<Pair<String, Int>>

    override fun parse(text: String) {
        instructions = text.split("\n").map {
            val (command, count) = it.split(" ")
            Pair(command, count.toInt())
        }
    }

    override fun compute(): String {
        var horizontalPos = 0
        var depth = 0

        instructions.forEach { (command, count) ->
            when (command) {
                "forward" -> horizontalPos += count
                "down" -> depth += count
                "up" -> depth -= count
            }
        }

        return (horizontalPos * depth).toString()
    }

    override val exampleAnswer: String
        get() = "150"
}

class PartB2 : PartA2() {
    override fun compute(): String {
        var horizontalPos = 0
        var depth = 0
        var aim = 0

        instructions.forEach { (command, count) ->
            when (command) {
                "forward" -> {
                    horizontalPos += count
                    depth += aim * count
                }

                "down" -> aim += count
                "up" -> aim -= count
            }
        }

        return (horizontalPos * depth).toString()
    }

    override val exampleAnswer: String
        get() = "900"
}
