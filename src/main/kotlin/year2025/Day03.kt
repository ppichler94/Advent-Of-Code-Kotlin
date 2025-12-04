package year2025

import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(3, 2025, PartA3(), PartB3()).run()
}

open class PartA3 : Part() {
    data class Bank(
        val batteries: List<Int>,
    )

    private lateinit var banks: List<Bank>
    protected open var k = 2

    override fun parse(text: String) {
        banks =
            text
                .splitLines()
                .map { Bank(it.toList().map(Char::digitToInt)) }
    }

    override fun compute(): String = banks.sumOf { it.joltage() }.toString()

    private fun Bank.joltage(): Long {
        val chosen = mutableListOf<Int>()
        var start = 0
        repeat(k) {
            val (index, max) = findMaxInRange(start until batteries.size - k + it + 1)
            chosen.add(max)
            start = index + 1
        }
        return chosen.joinToString("").ifEmpty { "0" }.toLong()
    }

    private fun Bank.findMaxInRange(range: IntRange): Pair<Int, Int> {
        var max = 0
        var index = 0
        for (i in range) {
            if (batteries[i] > max) {
                max = batteries[i]
                index = i
            }
        }
        return index to max
    }

    override val exampleAnswer = "357"
}

class PartB3 : PartA3() {
    override var k = 12
    override val exampleAnswer = "3121910778619"
}