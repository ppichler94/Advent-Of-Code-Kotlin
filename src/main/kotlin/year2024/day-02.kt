package year2024

import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(2, 2024, PartA2(), PartB2()).run()
}

private data class Report(val levels: List<Int>) {
    fun safe(): Boolean {
        val differences = levels.zipWithNext { a, b -> b - a }
        return differences.all { it in -3..-1 } || differences.all { it in 1..3 }
    }

    fun tolerated(): Boolean {
        if (safe()) return true
        return levels.indices.any { Report(levels.filterIndexed { i, _ -> i != it}).safe()}
    }
}

open class PartA2 : Part() {
    protected lateinit var reports: List<List<Int>>

    override fun parse(text: String) {
        reports = text.splitLines().map { it.split(" ").map(String::toInt) }
    }

    override fun compute(): String {
        return reports.count { Report(it).safe() }.toString()
    }

    override val exampleAnswer: String
        get() = "2"
}

class PartB2 : PartA2() {
    override fun compute(): String {
        return reports.count { Report(it).tolerated() }.toString()
    }

    override val exampleAnswer: String
        get() = "4"
}
