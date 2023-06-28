package year2022

import lib.Day
import lib.Part
import lib.math.product
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarrayOf
import org.jetbrains.kotlinx.multik.ndarray.data.D1Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.operations.plus

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
        val visible = mutableSetOf<D1Array<Int>>()
        limits.forEach {
            visible.addAll(walkLine(mk.ndarrayOf(0, it), mk.ndarrayOf(1, 0)))
            visible.addAll(walkLine(mk.ndarrayOf(trees.size - 1, it), mk.ndarrayOf(-1, 0)))
            visible.addAll(walkLine(mk.ndarrayOf(it, 0), mk.ndarrayOf(0, 1)))
            visible.addAll(walkLine(mk.ndarrayOf(it, trees.size - 1), mk.ndarrayOf(0, -1)))
        }
        return visible.size.toString()
    }

    private fun walkLine(startPos: D1Array<Int>, direction: D1Array<Int>): MutableSet<D1Array<Int>> {
        var currentPos = startPos
        var currentHeight = -1
        val visible = mutableSetOf<D1Array<Int>>()
        while (currentPos[0] in limits && currentPos[1] in limits) {
            if (trees[currentPos[1]][currentPos[0]] > currentHeight) {
                visible.add(currentPos)
                currentHeight = trees[currentPos[1]][currentPos[0]]
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
        val position = mk.ndarrayOf(value.first, value.second)
        val directions = listOf(mk.ndarrayOf(-1, 0), mk.ndarrayOf(1, 0), mk.ndarrayOf(0, -1), mk.ndarrayOf(0, 1))
        return directions.map { calculateScoreOfLine(position, it) }.reduce(Int::times)
    }

    private fun calculateScoreOfLine(position: D1Array<Int>, direction: D1Array<Int>): Int {
        var currentPos = position + direction
        var score = 0
        while (currentPos[0] in limits && currentPos[1] in limits) {
            score += 1
            if (trees[currentPos[1]][currentPos[0]] >= trees[position[1]][position[0]]) {
                break
            }
            currentPos += direction
        }
        return score
    }

    override val exampleAnswer: String
        get() = "8"
}
