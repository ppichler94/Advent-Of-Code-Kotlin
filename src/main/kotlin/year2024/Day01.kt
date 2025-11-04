package year2024

import lib.aoc.Day
import lib.aoc.Part
import kotlin.math.abs

fun main() {
    Day(1, 2024, PartA1(), PartB1()).run()
}

open class PartA1 : Part() {
    protected lateinit var left: List<Int>
    protected lateinit var right: List<Int>

    override fun parse(text: String) {
        left = text.lines().map { it.split(" ").first().toInt() }.sorted()
        right = text.lines().map { it.split(" ").last().toInt() }.sorted()
    }

    override fun compute(): String = left.zip(right).sumOf { (a, b) -> abs(a - b) }.toString()

    override val exampleAnswer: String
        get() = "11"
}

class PartB1 : PartA1() {
    override fun compute(): String {
        val occurances = right.groupingBy { it }.eachCount()
        return left.sumOf { (occurances[it] ?: 0) * it }.toString()
    }

    override val exampleAnswer: String
        get() = "31"
}