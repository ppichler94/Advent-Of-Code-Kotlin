package year2022

import lib.Day
import lib.Part

fun main() {
    Day(3, 2022, PartA3(), PartB3()).run()
}

open class PartA3 : Part() {
    internal lateinit var lines: List<String>

    override fun parse(text: String) {
        lines = text.split("\n")
    }

    override fun compute(): String {
        var sum = 0
        lines.forEach {
            val compartmentSize = it.length / 2
            val firstPart = it.slice(0 until compartmentSize)
            val secondPart = it.slice(compartmentSize until it.length)
            val common = findCommonItem(listOf(firstPart, secondPart))
            sum += priorityOf(common)
        }
        return sum.toString()
    }

    internal fun findCommonItem(lists: List<String>): Char {
        return lists.map(String::toSet).fold(lists.first().toSet()) {
            acc, part ->
            acc.toSet() intersect part.toSet()
        }.elementAtOrElse(0) { ' ' }
    }

    internal fun priorityOf(item: Char): Int {
        val charCode = item.code
        if (charCode in 'a'.code .. 'z'.code) {
            return 1 + charCode - 'a'.code
        }
        if (charCode in 'A'.code .. 'Z'.code) {
            return 27 + charCode - 'A'.code
        }
        return 0
    }

    override val exampleAnswer: String
        get() = "157"
}

class PartB3 : PartA3() {
    override fun compute(): String {
        var sum = 0
        lines.chunked(3).forEach {
            val badge = findCommonItem(it)
            sum += priorityOf(badge)
        }
        return sum.toString()
    }

    override val exampleAnswer: String
        get() = "70"

}
