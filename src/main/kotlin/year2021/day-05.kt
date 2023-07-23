package year2021

import lib.aoc.Day
import lib.aoc.Part
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.*
import org.jetbrains.kotlinx.multik.ndarray.operations.filter
import org.jetbrains.kotlinx.multik.ndarray.operations.max
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day(5, 2021, PartA5(), PartB5()).run()
}

open class PartA5 : Part() {
    data class Field(
        val startCoordinates: D2Array<Int>,
        val endCoordinates: D2Array<Int>,
        val considerDiagonals: Boolean,
    ) {
        val data: D2Array<Int>

        val overlaps: Int
            get() = data.filter { it > 1 }.size

        init {
            val width = 1 + max(startCoordinates.maxOfColumn(0), endCoordinates.maxOfColumn(0))
            val height = 1 + max(startCoordinates.maxOfColumn(1), endCoordinates.maxOfColumn(1))
            data = mk.zeros(height, width)
            addLines()
        }

        private fun addLines() {
            for (index in 0..<startCoordinates.shape[0]) {
                val start = startCoordinates[index]
                val end = endCoordinates[index]
                when {
                    start[1] == end[1] -> addHorizontalLine(start[1], start[0], end[0])
                    start[0] == end[0] -> addVerticalLine(start[0], start[1], end[1])
                    considerDiagonals -> addDiagonalLine(start, end)
                }
            }
        }

        private fun addHorizontalLine(y: Int, x1: Int, x2: Int) {
            val start = min(x1, x2)
            val end = max(x1, x2)
            for (x in start..end) {
                data[y, x] += 1
            }
        }

        private fun addVerticalLine(x: Int, y1: Int, y2: Int) {
            val start = min(y1, y2)
            val end = max(y1, y2)
            for (y in start..end) {
                data[y, x] += 1
            }
        }

        private fun addDiagonalLine(start: MultiArray<Int, D1>, end: MultiArray<Int, D1>) {
            val dirX = if (start[0] < end[0]) 1 else -1
            val dirY = if (start[1] < end[1]) 1 else -1
            var x1 = start[0]
            val x2 = end[0]
            var y1 = start[1]
            while (true) {
                data[y1, x1] += 1
                x1 += dirX
                y1 += dirY
                if (x1 == x2 + dirX) {
                    break
                }
            }
        }

        companion object {
            fun fromString(text: List<String>, considerDiagonals: Boolean): Field {
                val startCoordinates = mk.zeros<Int>(text.size, 2)
                val endCoordinates = mk.zeros<Int>(text.size, 2)
                text.forEachIndexed { i, line ->
                    val coords = line.split(" -> ")
                    val startCoords = coords[0].split(",")
                    val endCoords = coords[1].split(",")
                    startCoordinates[i, 0] = startCoords[0].toInt()
                    startCoordinates[i, 1] = startCoords[1].toInt()
                    endCoordinates[i, 0] = endCoords[0].toInt()
                    endCoordinates[i, 1] = endCoords[1].toInt()
                }
                return Field(startCoordinates, endCoordinates, considerDiagonals)
            }
        }

        private fun D2Array<Int>.maxOfColumn(column: Int): Int {
            return this.transpose()[column].max() ?: -1
        }
    }

    protected lateinit var field: Field

    override fun parse(text: String) {
        field = Field.fromString(text.split("\n"), false)
    }

    override fun compute(): String {
        return field.overlaps.toString()
    }

    override val exampleAnswer: String
        get() = "5"
}

class PartB5 : PartA5() {
    override fun parse(text: String) {
        field = Field.fromString(text.split("\n"), true)
    }

    override fun compute(): String {
        return field.overlaps.toString()
    }

    override val exampleAnswer: String
        get() = "12"
}
