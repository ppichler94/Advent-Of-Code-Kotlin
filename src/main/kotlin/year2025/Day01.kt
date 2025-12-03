package year2025

import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(1, 2025, PartA1(), PartB1()).run()
}

open class PartA1 : Part() {
    data class Rotation(
        val clicks: Int,
        val direction: String,
    )

    protected lateinit var rotations: List<Rotation>

    override fun parse(text: String) {
        rotations =
            text
                .splitLines()
                .map {
                    val direction = it.take(1)
                    val clicks = it.drop(1).toInt()
                    Rotation(clicks, direction)
                }
    }

    override fun compute(): String {
        var dial = 50
        var password = 0
        rotations.forEach { rotation ->
            if (rotation.direction == "L") {
                dial = (dial - rotation.clicks).mod(100)
            } else {
                dial = (dial + rotation.clicks).mod(100)
            }
            if (dial == 0) {
                password++
            }
        }
        return password.toString()
    }

    override val exampleAnswer = "3"
}

class PartB1 : PartA1() {
    override fun compute(): String {
        var dial = 50
        var password = 0
        rotations.forEach { rotation ->
            var k0 = if (rotation.direction == "L") dial.mod(100) else (100 - dial).mod(100)
            if (k0 == 0) {
                k0 = 100
            }

            val zeroCrossings =
                if (rotation.clicks < k0) {
                    0
                } else {
                    1 + (rotation.clicks - k0) / 100
                }
            dial =
                if (rotation.direction == "L") {
                    (dial - rotation.clicks).mod(100)
                } else {
                    (dial + rotation.clicks).mod(100)
                }
            password += zeroCrossings
        }
        return password.toString()
    }

    override val exampleAnswer = "6"
}