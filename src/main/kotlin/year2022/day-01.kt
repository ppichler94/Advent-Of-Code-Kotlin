package year2022

import lib.Day
import lib.Part
import lib.TestCase

fun main() {
    Day(1, 2022, PartA1(), PartB1()).run()
}

open class PartA1 : Part() {
    internal lateinit var calories: List<Int>
    override fun parse(text: String) {
        calories = text.split("\n\n")
            .map { it.split("\n") }
            .map { it.map { s: String ->  s.toInt() } }
            .map { it.sum() }
    }

    override fun compute(): String {
        return calories.max().toString()
    }

    override val exampleAnswer: String
        get() = "24000"
}

class PartB1 : PartA1() {
    override fun compute(): String {
        return calories.sortedDescending().take(3).sum().toString()
    }

    override val exampleAnswer: String
        get() = "45000"

    override val testCases = sequence {
        yield(TestCase("test1", "10\n10\n10\n\n10\n10\n\n10\n10\n\n10", "70"))
    }
}