package year2022

import lib.aoc.Day
import lib.aoc.Part
import lib.math.Vector
import lib.math.plus
import lib.math.plusAssign
import lib.math.product

fun main() {
    Day(8, 2022, PartA8(), PartB8()).run()
}


open class PartA8 : Part() {
    lateinit var trees: List<List<Int>>
    lateinit var limits: Iterable<Int>

    operator fun List<List<Int>>.get(pos: Vector) = this[pos.y][pos.x]

    override fun parse(text: String) {
        trees = text
            .split("\n")
            .map { it.toList().map(Char::digitToInt) }
        limits = trees.indices
    }

    override fun compute(): String {
        val visible = mutableSetOf<Vector>()
        limits.forEach {
            visible.addAll(walkLine(Vector.at(0, it), Vector.at(1, 0)))
            visible.addAll(walkLine(Vector.at(trees.size - 1, it), Vector.at(-1, 0)))
            visible.addAll(walkLine(Vector.at(it, 0), Vector.at(0, 1)))
            visible.addAll(walkLine(Vector.at(it, trees.size - 1), Vector.at(0, -1)))
        }
        return visible.size.toString()
    }

    private fun walkLine(startPos: Vector, direction: Vector): MutableSet<Vector> {
        val currentPos = startPos.toMutableVector()
        var currentHeight = -1
        val visible = mutableSetOf<Vector>()
        while (currentPos within limits) {
            if (trees[currentPos] > currentHeight) {
                visible.add(currentPos.toVector())
                currentHeight = trees[currentPos]
            }
            currentPos += direction
        }
        return visible
    }

    override val exampleAnswer: String
        get() = "21"
}

class PartB8 : PartA8() {
    override fun compute(): String {
        return (1 until trees.size).product(1 until trees.size)
            .maxOf(::calculateScenicScore)
            .toString()
    }

    private fun calculateScenicScore(value: Pair<Int, Int>): Int {
        val position = Vector.at(value.first, value.second)
        val directions = listOf(Vector.at(-1, 0), Vector.at(1, 0), Vector.at(0, -1), Vector.at(0, 1))
        return directions.map { calculateScoreOfLine(position, it) }.reduce(Int::times)
    }

    private fun calculateScoreOfLine(position: Vector, direction: Vector): Int {
        val currentPos = (position + direction).toMutableVector()
        var score = 0
        while (currentPos within limits) {
            score += 1
            if (trees[currentPos] >= trees[position]) {
                break
            }
            currentPos += direction
        }
        return score
    }

    override val exampleAnswer: String
        get() = "8"
}
