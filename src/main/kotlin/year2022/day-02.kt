package year2022

import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(2, 2022, PartA2(), PartB2()).run()
}

open class PartA2 : Part() {
    internal lateinit var lines: List<String>
    internal val winLossMatrix =
        arrayOf(
            arrayOf(3, 6, 0),
            arrayOf(0, 3, 6),
            arrayOf(6, 0, 3),
        )

    override fun parse(text: String) {
        lines = text.split("\n")
    }

    override fun compute(): String {
        var score = 0
        lines.forEach {
            val (opponentCode, playerCode) = it.split(" ")
            val playerShapeIndex = decodeShape(playerCode)
            val opponentShapeIndex = decodeShape(opponentCode)
            score += 1 + playerShapeIndex + winLossMatrix[opponentShapeIndex][playerShapeIndex]
        }
        return score.toString()
    }

    internal fun decodeShape(code: String): Int =
        when (code) {
            "A", "X" -> 0
            "B", "Y" -> 1
            "C", "Z" -> 2
            else -> throw IllegalArgumentException("Invalid code: $code")
        }

    override val exampleAnswer: String
        get() = "15"
}

class PartB2 : PartA2() {
    internal val choiceMatrix =
        arrayOf(
            arrayOf(2, 0, 1),
            arrayOf(0, 1, 2),
            arrayOf(1, 2, 0),
        )

    override fun compute(): String {
        var score = 0
        lines.forEach {
            val (outcomeCode, opponentCode) = it.split(" ")
            val outcomeIndex = decodeShape(outcomeCode)
            val opponentShapeIndex = decodeShape(opponentCode)
            val playerShapeIndex = choiceMatrix[opponentShapeIndex][outcomeIndex]
            score += 1 + playerShapeIndex + winLossMatrix[opponentShapeIndex][playerShapeIndex]
        }
        return score.toString()
    }

    override val exampleAnswer: String
        get() = "12"
}