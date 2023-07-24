package year2021

import lib.aoc.Day
import lib.aoc.Part
import kotlin.math.abs
import kotlin.math.min

fun main() {
    Day(7, 2021, PartA7(), PartB7()).run()
}

open class PartA7 : Part() {
    protected lateinit var positions: List<Int>

    override fun parse(text: String) {
        positions = text.split(",").map(String::toInt)
    }

    override fun compute(): String {
        var minCost = costFunction(positions.min())
        for (target in positions.min()..positions.max()) {
            val cost = costFunction(target)
            minCost = min(cost, minCost)
        }
        return minCost.toString()
    }

    protected open fun costFunction(target: Int): Int {
        return positions.sumOf { abs(it - target) }
    }

    override val exampleAnswer: String
        get() = "37"
}

class PartB7 : PartA7() {
    override fun costFunction(target: Int): Int {
        return positions
            .map { abs(it - target) }
            .sumOf { it * (it + 1) / 2 }
    }

    override val exampleAnswer: String
        get() = "168"
}
