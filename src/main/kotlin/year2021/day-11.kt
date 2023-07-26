package year2021

import lib.MutableGrid2d
import lib.Position
import lib.aoc.Day
import lib.aoc.Part
import lib.math.Itertools

fun main() {
    Day(11, 2021, PartA11(), PartB11()).run()
}

open class PartA11 : Part() {
    protected lateinit var energyLevels: MutableGrid2d<Int>

    override fun parse(text: String) {
        val content = text.split("\n").map {
            it.map { c -> c.digitToInt() }.toMutableList()
        }.toMutableList()
        energyLevels = MutableGrid2d(content)
    }

    override fun compute(): String {
        return (0..<100).sumOf { step() }.toString()
    }

    protected fun step(): Int {
        val moves = Position.moves(diagonals = true)
        val limits = energyLevels.limits
        val flashed = mutableSetOf<Position>()
        energyLevels.inc()
        var flashing = energyLevels.findAll { it > 9 }
        while (flashing.isNotEmpty()) {
            flashing = energyLevels.findAll { it > 9 }.filter { it !in flashed }
            flashed.addAll(flashing)
            val increasing = flashing.map { it.neighbours(moves, limits) }.flatten()
            increasing.forEach { energyLevels[it] += 1 }
        }
        flashed.forEach { energyLevels[it] = 0 }
        return flashed.size
    }

    private fun MutableGrid2d<Int>.inc() {
        content.forEach {
            for (i in it.indices) {
                it[i] += 1
            }
        }
    }

    override val exampleAnswer: String
        get() = "1656"
}

class PartB11 : PartA11() {
    override fun compute(): String {
        for (steps in Itertools.count(1)) {
            step()
            if (energyLevels.all { it == 0 }) {
                return steps.toString()
            }
        }
        error("cannot be reached")
    }

    private fun MutableGrid2d<Int>.all(predicate: (Int) -> Boolean): Boolean {
        return content.flatten().all(predicate)
    }

    override val exampleAnswer: String
        get() = "195"
}
