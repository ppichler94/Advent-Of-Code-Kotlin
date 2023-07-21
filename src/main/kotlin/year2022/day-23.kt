package year2022

import lib.Grid2d
import lib.Position
import lib.aoc.Day
import lib.aoc.Part
import lib.math.Itertools
import lib.math.Vector

fun main() {
    Day(23, 2022, PartA23(), PartB23()).run()
}

open class PartA23 : Part() {
    private lateinit var elfs: MutableSet<Position>
    private lateinit var directions: MutableList<List<Vector>>

    override fun parse(text: String) {
        val grove = Grid2d.ofLines(text.split("\n"))
        elfs = grove.findAll("#".asIterable()).toMutableSet()
        directions = mutableListOf(
            listOf(Vector.at(-1, -1), Vector.at(0, -1), Vector.at(1, -1)),
            listOf(Vector.at(-1, 1), Vector.at(0, 1), Vector.at(1, 1)),
            listOf(Vector.at(-1, -1), Vector.at(-1, 0), Vector.at(-1, 1)),
            listOf(Vector.at(1, -1), Vector.at(1, 0), Vector.at(1, 1)),
        )
    }

    override fun compute(): String {
        repeat(10) { doRound() }

        val (xMin, xMax, yMin, yMax) = findMinMax()
        return ((xMax + 1 - xMin) * (yMax + 1 - yMin) - elfs.size).toString()
    }

    protected fun doRound(): Boolean {
        val propositions = mutableMapOf<Position, Position?>()

        elfs.forEach loop@{ elf ->
            if (directions.flatten().all { elf + it !in elfs }) {
                return@loop
            }
            directions.forEach { directionList ->
                if (directionList.all { elf + it !in elfs }) {
                    val newPos = elf + directionList[1]
                    if (newPos in propositions) {
                        propositions[newPos] = null
                    } else {
                        propositions[newPos] = elf
                    }
                    return@loop
                }
            }
        }

        var anyElfMoving = false
        propositions.filter { (_, elf) -> elf != null }.forEach { (proposition, elf) ->
            elfs.remove(elf)
            elfs.add(proposition)
            anyElfMoving = true
        }
        val firstDirection = directions.removeFirst()
        directions.add(firstDirection)
        return anyElfMoving
    }

    private fun findMinMax(): List<Int> {
        val yMin = elfs.minOf { it[1] }
        val yMax = elfs.maxOf { it[1] }
        val xMin = elfs.minOf { it[0] }
        val xMax = elfs.maxOf { it[0] }
        return listOf(xMin, xMax, yMin, yMax)
    }

    override val exampleAnswer: String
        get() = "110"
}

class PartB23 : PartA23() {
    override fun compute(): String {
        for (roundNumber in Itertools.count(1)) {
            val moving = doRound()
            if (!moving) {
                return roundNumber.toString()
            }
        }
        error("should not be reached")
    }

    override val exampleAnswer: String
        get() = "20"
}
