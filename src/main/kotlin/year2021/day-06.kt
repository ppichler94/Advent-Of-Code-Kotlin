package year2021

import lib.aoc.Day
import lib.aoc.Part
import java.util.*

fun main() {
    Day(6, 2021, PartA6(), PartB6()).run()
}

open class PartA6(private val days: Int = 80) : Part() {
    private lateinit var bins: MutableList<Long>

    override fun parse(text: String) {
        val population = text.split(",").map(String::toInt)
        bins = (0..9).map { i -> population.count { it == i }.toLong() }.toMutableList()
    }

    override fun compute(): String {
        repeat(days) {
            spawnNew()
            Collections.rotate(bins, -1)
        }
        return bins.sum().toString()
    }

    private fun spawnNew() {
        val numberOfZeros = bins.first()
        bins[0] = 0L
        bins[7] += numberOfZeros
        bins[9] += numberOfZeros
    }

    override val exampleAnswer: String
        get() = "5934"
}

class PartB6 : PartA6(256) {
    override val exampleAnswer: String
        get() = "26984457539"
}
