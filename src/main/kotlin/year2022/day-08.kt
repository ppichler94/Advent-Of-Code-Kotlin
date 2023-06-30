package year2022

import lib.Day
import lib.Part
import lib.math.Vector2i
import lib.math.product

fun main() {
    Day(8, 2022, PartA8(), PartB8()).run()
}


open class PartA8 : Part() {
    lateinit var trees: List<List<Int>>
    lateinit var limits: Iterable<Int>
    override fun parse(text: String) {
        trees = text
            .split("\n")
            .map { it.toList().map(Char::digitToInt) }
        limits = trees.indices
    }

    override fun compute(): String {
        val visible = mutableSetOf<Vector2i>()
        limits.forEach {
            visible.addAll(walkLine(Vector2i(0, it), Vector2i(1, 0)))
            visible.addAll(walkLine(Vector2i(trees.size - 1, it), Vector2i(-1, 0)))
            visible.addAll(walkLine(Vector2i(it, 0), Vector2i(0, 1)))
            visible.addAll(walkLine(Vector2i(it, trees.size - 1), Vector2i(0, -1)))
        }
        return visible.size.toString()
    }

    private fun walkLine(startPos: Vector2i, direction: Vector2i): MutableSet<Vector2i> {
        val currentPos = startPos.toMutableVector()
        var currentHeight = -1
        val visible = mutableSetOf<Vector2i>()
        while (currentPos within limits) {
            if (trees[currentPos.y][currentPos.x] > currentHeight) {
                visible.add(currentPos.toVector())
                currentHeight = trees[currentPos.y][currentPos.x]
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
        val position = Vector2i(value.first, value.second)
        val directions = listOf(Vector2i(-1, 0), Vector2i(1, 0), Vector2i(0, -1), Vector2i(0, 1))
        return directions.map { calculateScoreOfLine(position, it) }.reduce(Int::times)
    }

    private fun calculateScoreOfLine(position: Vector2i, direction: Vector2i): Int {
        val currentPos = (position + direction).toMutableVector()
        var score = 0
        while (currentPos within limits) {
            score += 1
            if (trees[currentPos.y][currentPos.x] >= trees[position.y][position.x]) {
                break
            }
            currentPos += direction
        }
        return score
    }

    override val exampleAnswer: String
        get() = "8"
}
