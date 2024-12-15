package year2024

import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(7, 2024, PartA7(), PartB7()).run()
}

open class PartA7 : Part() {
    private data class Equation(
        val target: Long,
        val values: List<Long>,
    )

    private lateinit var equations: List<Equation>

    override fun parse(text: String) {
        equations =
            text.splitLines().map {
                val (target, values) = it.split(": ")
                Equation(target.toLong(), values.split(" ").map(String::toLong))
            }
    }

    override fun compute(): String = equations.filter { checkEquation(0, it.target, it.values) }.sumOf { it.target }.toString()

    protected open fun checkEquation(
        acc: Long,
        target: Long,
        values: List<Long>,
    ): Boolean {
        if (values.isEmpty()) {
            return acc == target
        }
        if (acc > target) {
            return false
        }
        return checkEquation(acc + values.first(), target, values.drop(1)) || checkEquation(acc * values.first(), target, values.drop(1))
    }

    override val exampleAnswer: String
        get() = "3749"
}

class PartB7 : PartA7() {
    override fun checkEquation(
        acc: Long,
        target: Long,
        values: List<Long>,
    ): Boolean {
        if (values.isEmpty()) {
            return acc == target
        }
        if (acc > target) {
            return false
        }
        return checkEquation(acc + values.first(), target, values.drop(1)) ||
            checkEquation(acc * values.first(), target, values.drop(1)) ||
            checkEquation((acc.toString() + values.first().toString()).toLong(), target, values.drop(1))
    }

    override val exampleAnswer: String
        get() = "11387"
}
