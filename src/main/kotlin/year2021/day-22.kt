package year2021

import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day(22, 2021, PartA22(), PartB22()).run()
}

open class PartA22(private val limit: Int = 50) : Part() {
    private enum class Command { On, Off }
    private data class Step(val command: Command, val cube: Cube)
    private data class Cube(
        val xMin: Long,
        val xMax: Long,
        val yMin: Long,
        val yMax: Long,
        val zMin: Long,
        val zMax: Long
    ) {
        val volume get() = (xMax - xMin + 1) * (yMax - yMin + 1) * (zMax - zMin + 1)

        fun intersections(cubes: List<Cube>): List<Cube> {
            return cubes.mapNotNull {
                val xMinIntersect = max(xMin, it.xMin)
                val xMaxIntersect = min(xMax, it.xMax)
                val yMinIntersect = max(yMin, it.yMin)
                val yMaxIntersect = min(yMax, it.yMax)
                val zMinIntersect = max(zMin, it.zMin)
                val zMaxIntersect = min(zMax, it.zMax)
                if (xMinIntersect <= xMaxIntersect && yMinIntersect <= yMaxIntersect && zMinIntersect <= zMaxIntersect) {
                    Cube(xMinIntersect, xMaxIntersect, yMinIntersect, yMaxIntersect, zMinIntersect, zMaxIntersect)
                } else {
                    null
                }
            }
        }

        fun withinLimit(limit: Int): Boolean {
            return xMin > -limit
                    && yMin > -limit
                    && zMin > -limit
                    && xMax < limit
                    && yMax < limit
                    && zMax < limit
        }
    }

    private fun Command(text: String): Command {
        return when (text) {
            "on " -> Command.On
            "off" -> Command.Off
            else -> error("Unknown command $text")
        }
    }

    private lateinit var steps: List<Step>

    override fun parse(text: String) {
        steps = text.splitLines()
            .map {
                val command = Command(it.take(3))
                val values = """-?\d+""".toRegex().findAll(it).map { v -> v.value.toLong() }.toList()
                val cube = Cube(values[0], values[1], values[2], values[3], values[4], values[5])
                Step(command, cube)
            }
    }

    override fun compute(): String {
        val cubes = mutableListOf<Cube>()
        val cubesNegative = mutableListOf<Cube>()

        for ((command, cube) in steps) {
            if (limit > 0 && !cube.withinLimit(limit)) {
                continue
            }

            val newCubesNegative = cube.intersections(cubes)
            val newCubes = cube.intersections(cubesNegative)
            if (command == Command.On) {
                cubes.add(cube)
            }
            cubes.addAll(newCubes)
            cubesNegative.addAll(newCubesNegative)
        }

        return (cubes.sumOf { it.volume } - cubesNegative.sumOf { it.volume }).toString()
    }

    override val exampleAnswer: String
        get() = "39"
}

class PartB22 : PartA22(0)
