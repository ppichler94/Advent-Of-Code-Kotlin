package year2021

import lib.aoc.Day
import lib.aoc.Part
import lib.math.product
import kotlin.math.sign
import kotlin.math.sqrt

fun main() {
    Day(17, 2021, PartA17(), PartB17()).run()
}

open class PartA17 : Part() {
    protected var xMin: Int = 0
    protected var xMax: Int = 0
    protected var yMin: Int = 0
    protected var yMax: Int = 0

    override fun parse(text: String) {
        val regex = """.*x=(-?\d+)..(-?\d+), y=(-?\d+)..(-?\d+)""".toRegex()
        val match = regex.find(text)
        check(match != null) { "Invalid input" }
        xMin = match.groupValues[1].toInt()
        xMax = match.groupValues[2].toInt()
        yMin = match.groupValues[3].toInt()
        yMax = match.groupValues[4].toInt()
    }

    override fun compute(): String {
        val maxVy = -yMin - 1
        return (maxVy * (maxVy + 1) / 2).toString()
    }

    override val exampleAnswer: String
        get() = "45"
}

class PartB17 : PartA17() {
    override fun compute(): String {
        val minVy = yMin
        val maxVy = -yMin - 1
        val minVx = (sqrt(1 + 8f * xMin) / 2f).toInt()
        val maxVx = xMax
        val hits = (minVx..maxVx).product(minVy..maxVy)
            .filter(::isHit)

        return hits.size.toString()
    }

    private fun isHit(velocity: Pair<Int, Int>): Boolean {
        var vx = velocity.first
        var vy = velocity.second
        var x = 0
        var y = 0

        while (true) {
            x += vx
            y += vy

            vx -= vx.sign
            vy--

            if (x > xMax) {
                return false
            }
            if (y < yMin) {
                return false
            }
            if (x >= xMin && y <= yMax) {
                return true
            }
        }
    }

    override val exampleAnswer: String
        get() = "112"
}
