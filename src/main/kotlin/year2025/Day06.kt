package year2025

import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(6, 2025, PartA6(), PartB6()).run()
}

open class PartA6 : Part() {
    data class Problem(
        val operation: Operation,
        val numbers: List<Long>,
    ) {
        fun compute() =
            when (operation) {
                Operation.Add -> numbers.sum()
                Operation.Multiply -> numbers.reduce { a, b -> a * b }
            }
    }

    enum class Operation {
        Add,
        Multiply,
    }

    fun Operation(s: String): Operation =
        when (s) {
            "+" -> Operation.Add
            "*" -> Operation.Multiply
            else -> throw IllegalArgumentException("Unknown operation $s")
        }

    protected lateinit var problems: List<Problem>

    override fun parse(text: String) {
        val lines = text.splitLines()
        val operations = lines.last().split("\\s+".toRegex()).map { Operation(it) }
        val numbers =
            lines
                .dropLast(
                    1,
                ).map { it.split("\\s+".toRegex()).filterNot { it.isBlank() }.map(String::toLong) }
        problems =
            numbers.first().indices.map { column ->
                Problem(operations[column], numbers.map { it[column] })
            }
    }

    override fun compute(): String = problems.sumOf { it.compute() }.toString()

    override val exampleAnswer = "4277556"
}

class PartB6 : PartA6() {
    override fun parse(text: String) {
        val lines = text.splitLines()
        val numberLines = lines.dropLast(1)
        val operations = lines.last().split("\\s+".toRegex()).map { Operation(it) }
        var opIndex = 0
        val numbers = mutableListOf<Long>()
        val p = mutableListOf<Problem>()
        for (column in lines.first().toList().indices) {
            val stringNumber =
                numberLines
                    .map { it[column] }
                    .joinToString("")
                    .trim()
            if (stringNumber.isNotBlank()) {
                numbers.add(stringNumber.toLong())
            } else {
                p.add(Problem(operations[opIndex], numbers.toList()))
                opIndex += 1
                numbers.clear()
            }
        }
        p.add(Problem(operations[opIndex], numbers.toList()))
        problems = p
    }

    override val exampleAnswer = "3263827"
}