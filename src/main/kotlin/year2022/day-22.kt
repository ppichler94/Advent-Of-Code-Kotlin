package year2022

import lib.Day
import lib.Grid2d
import lib.Part
import lib.Position
import lib.math.Vector
import lib.math.unaryMinus

fun main() {
    Day(22, 2022, PartA22(), PartB22()).run()
}

open class PartA22 : Part() {
    private lateinit var instructions: List<String>
    protected lateinit var map: Grid2d<Char>
    protected lateinit var position: Position
    protected var directionIndex: Int = 0
    protected val directions = listOf(
        Vector.at(1, 0), // R
        Vector.at(0, 1), // D
        Vector.at(-1, 0), // L
        Vector.at(0, -1), // U
    )

    override fun parse(text: String) {
        val (mapText, instructionsText) = text.split("\n\n")
        val mapLines = mapText.split("\n")
        val xMax = mapLines.maxOf { it.length }
        val emptyLine = "".padEnd(xMax + 2)
        val mapLinesFilled = buildList {
            add(emptyLine)
            addAll(mapLines.map { " $it".padEnd(xMax + 2) })
            add(emptyLine)
        }
        map = Grid2d.ofLines(mapLinesFilled)

        instructions = """(\d+|[RL])""".toRegex().findAll(instructionsText).map { it.value }.toList()
        val xStart = mapLinesFilled[1].indexOf('.')
        position = Position.at(xStart, 1)
        config(text.length == 188)
    }

    protected open fun config(isExample: Boolean) {
        /* additional configuration after parsing used for part b */
    }

    override fun compute(): String {
        directionIndex = 0
        instructions.forEach {
            when (it) {
                "R" -> directionIndex = (directionIndex + 1).mod(4)
                "L" -> directionIndex = (directionIndex - 1).mod(4)
                else -> moveSteps(it.toInt())
            }
        }
        return (1000 * position[1] + 4 * position[0] + directionIndex).toString()
    }

    private fun moveSteps(steps: Int) {
        repeat(steps) {
            val stuck = move()
            if (stuck) {
                return
            }
        }
    }

    protected open fun move(): Boolean {
        val direction = directions[directionIndex]
        var nextPosition = position + direction
        val nextChar = map[nextPosition]
        if (nextChar == ' ') {
            val backDirection = -direction
            while (map[nextPosition + backDirection] != ' ') {
                nextPosition += backDirection
            }
        }
        if (nextChar == '#') {
            return true
        }
        position = nextPosition
        return false
    }

    override val exampleAnswer: String
        get() = "6032"
}

class PartB22 : PartA22() {
    private var edgeLength = 0
    private lateinit var wrapping: Map<Triple<Int, Int, Int>, Triple<Int, Int, Int>>

    override fun config(isExample: Boolean) {
        val maxMapLength = map.limits.maxOf { it.last() }
        edgeLength = maxMapLength / 4

        // Define wrapping per move from one "open" edge to the other.
        // Elements: mapY, mapX, direction -> mapY, mapX, direction
        if (isExample) {
            wrapping = buildMap {
                // 90 degrees
                put(Triple(1, 2, 0), Triple(2, 3, 1))
                put(Triple(0, 2, 2), Triple(1, 1, 1))
                put(Triple(2, 3, 1), Triple(1, 0, 0))
                put(Triple(1, 1, 1), Triple(2, 2, 0))
                // 180 degrees
                put(Triple(0, 2, 3), Triple(1, 0, 1))
                put(Triple(0, 2, 0), Triple(2, 3, 2))
                put(Triple(2, 2, 1), Triple(1, 0, 3))
                // add symmetric connections (reversed direction)
                this.forEach { (fromMapY, fromMapX, fromDir), (toMapY, toMapX, toDir) ->
                    put(Triple(toMapY, toMapX, (toDir + 2).mod(4)), Triple(fromMapY, fromMapX, (fromDir + 2).mod(4)))
                }
            }
        } else {
            wrapping = buildMap {
                // 0 degrees
                put(Triple(0, 2, 3), Triple(3, 0, 3))
                // 90 degrees
                put(Triple(0, 1, 3), Triple(3, 0, 0))
                put(Triple(2, 0, 3), Triple(1, 1, 0))
                put(Triple(0, 2, 1), Triple(1, 1, 2))
                put(Triple(2, 1, 1), Triple(3, 0, 2))
                // 180 degrees
                put(Triple(2, 0, 2), Triple(0, 1, 0))
                put(Triple(0, 2, 0), Triple(2, 1, 2))
                // add symmetric connections (reversed direction)
                this.forEach { (fromMapY, fromMapX, fromDir), (toMapY, toMapX, toDir) ->
                    put(Triple(toMapY, toMapX, (toDir + 2).mod(4)), Triple(fromMapY, fromMapX, (fromDir + 2).mod(4)))
                }
            }
        }
    }

    override fun move(): Boolean {
        val direction = directions[directionIndex]
        var nextPosition = position + direction
        var nextChar = map[nextPosition]
        var nextDirectionIndex = directionIndex
        if (nextChar == ' ') {
            val faceX = (position[0] - 1) / edgeLength
            val faceY = (position[1] - 1) / edgeLength
            val (newFaceY, newFaceX, newDirIndex) = wrapping.getValue(Triple(faceY, faceX, directionIndex))
            nextDirectionIndex = newDirIndex
            var xOnFace = (nextPosition[0] - 1).mod(edgeLength)
            var yOnFace = (nextPosition[1] - 1).mod(edgeLength)
            val count =
                if (directionIndex <= newDirIndex) newDirIndex - directionIndex else newDirIndex + 4 - directionIndex
            repeat(count) {
                yOnFace = xOnFace.also { xOnFace = yOnFace }
                xOnFace = edgeLength - 1 - xOnFace
            }
            nextPosition = Position.at(newFaceX * edgeLength + xOnFace + 1, newFaceY * edgeLength + yOnFace + 1)
            nextChar = map[nextPosition]
        }
        if (nextChar == '#') {
            return true
        }
        position = nextPosition
        directionIndex = nextDirectionIndex
        return false
    }

    override val exampleAnswer: String
        get() = "5031"
}
