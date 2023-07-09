package year2022

import lib.Day
import lib.Part
import lib.math.Vector
import kotlin.math.abs
import kotlin.math.max

fun main() {
    Day(15, 2022, PartA15(), PartB15()).run()
}


open class PartA15 : Part() {
    data class Sensor(val position: Vector, val closestBeacon: Vector) {
        private val beaconRadius = abs(position.x - closestBeacon.x) + abs(position.y - closestBeacon.y)

        fun coverageAtLevel(yLevel: Int): Pair<Int, Int>? {
            val coverage = beaconRadius - abs(position.y - yLevel)
            if (coverage > 0) {
                return Pair(position.x - coverage, position.x + coverage)
            }
            return null
        }

        companion object {
            fun ofText(text: String): Sensor {
                val matcher = """Sensor at x=(.*), y=(.*): closest beacon is at x=(.*), y=(.*)""".toRegex().matchEntire(text)
                val (sensorX, sensorY, beaconX, beaconY) = matcher!!.destructured
                return Sensor(Vector.at(sensorX.toInt(), sensorY.toInt()), Vector.at(beaconX.toInt(), beaconY.toInt()))
            }
        }
    }

    internal lateinit var sensors: List<Sensor>
    internal  var isExample: Boolean = false

    override fun parse(text: String) {
        sensors = text.split("\n").map(Sensor::ofText)
        isExample = sensors.size <= 14
    }

    override fun compute(): String {
        val yLevel = if (isExample) 20 else 2_000_000
        val coverages = sensors.mapNotNull { it.coverageAtLevel(yLevel) }
        return countCoveredPositions(coverages).toString()
    }

    private fun countCoveredPositions(coverages: List<Pair<Int, Int>>): Int {
        val covered = mutableSetOf<Int>()
        coverages.forEach { (start, end) -> covered.addAll(start until end) }
        return covered.size
    }

    override val exampleAnswer: String
        get() = "26"
}

class PartB15 : PartA15() {
    override fun compute(): String {
        val maxPosition = if (isExample) 20 else 4_000_000
        (0 until maxPosition).forEach { yLevel ->
            val coverages = sensors.mapNotNull { it.coverageAtLevel(yLevel) }
            val mergedCoverages = mergeCoverages(coverages)
            mergedCoverages.forEach { (start, end) ->
                if (start > 0) {
                    return (yLevel.toBigInteger() + (start - 1).toBigInteger() * 4_000_000.toBigInteger()).toString()
                }
                if (end < maxPosition) {
                    return (yLevel.toBigInteger() + (end + 1).toBigInteger() * 4_000_000.toBigInteger()).toString()
                }
            }
        }
        return "no solution found"
    }

    private fun mergeCoverages(coverages: List<Pair<Int, Int>>): List<Pair<Int, Int>> {
        if (coverages.size <= 1) {
            return coverages
        }
        val sortedCoverages = coverages.sortedBy { it.first }
        val result = mutableListOf(sortedCoverages.first())
        sortedCoverages.drop(1).forEach {
            if (it.first <= result.last().second) {
                result[result.size - 1] = Pair(result.last().first, max(result.last().second, it.second))
            } else {
                result.add(it)
            }
        }
        return result.toList()
    }

    override val exampleAnswer: String
        get() = "56000011"
}
