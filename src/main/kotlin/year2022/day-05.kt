package year2022

import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(5, 2022, PartA5(), PartB5()).run()
}


open class PartA5 : Part() {
    data class Command(val origin: Int, val destination: Int, val count: Int)

    internal lateinit var stacks: MutableList<String>
    internal var commands: MutableList<Command> = mutableListOf()

    override fun parse(text: String) {
        val (stackText, movesText) = text.split("\n\n")
        val stackLines = stackText.split("\n")
        val numberOfStacks = """\d+""".toRegex().findAll(stackLines.last()).count()
        stacks = MutableList(numberOfStacks) { "" }
        stackLines.forEach {
            it.forEachIndexed { i, char ->
                if (char in 'A'..'Z') {
                    val pos = (i - 1) / 4
                    stacks[pos] += char.toString()
                }
            }
        }
        stacks = stacks.map { it.reversed() }.toMutableList()

        commands = mutableListOf()
        val moveMatcher = """move (\d+) from (\d+) to (\d+)""".toRegex()
        movesText.split("\n").forEach {
            val matches = moveMatcher.matchEntire(it)
            matches?.let {
                val (count, origin, destination) = matches.destructured
                commands.add(Command(origin.toInt() - 1, destination.toInt() - 1, count.toInt()))
            }
        }
    }

    override fun compute(): String {
        crateMover()
        return stacks.map { it.last() }.joinToString("")
    }

    internal open fun crateMover() {
        commands.forEach { command ->
            for (i in 0 until command.count) {
                stacks[command.destination] += stacks[command.origin].last().toString()
                stacks[command.origin] = stacks[command.origin].dropLast(1)
            }
        }
    }

    override val exampleAnswer: String
        get() = "CMZ"

}

class PartB5 : PartA5() {
    override fun crateMover() {
        commands.forEach {
            stacks[it.destination] += stacks[it.origin].takeLast(it.count)
            stacks[it.origin] = stacks[it.origin].dropLast(it.count)
        }
    }

    override val exampleAnswer: String
        get() = "MCD"
}