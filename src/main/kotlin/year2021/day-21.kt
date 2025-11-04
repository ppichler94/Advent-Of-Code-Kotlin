package year2021

import lib.aoc.Day
import lib.aoc.Part
import lib.memoize
import lib.splitLines
import kotlin.math.max

fun main() {
    Day(21, 2021, PartA21(), PartB21()).run()
}

open class PartA21 : Part() {
    protected class DeterministicDice {
        var counter = 0

        fun roll(): Int {
            val number = 1 + counter % 100
            counter++
            return number
        }
    }

    protected class Player(var position: Int, var points: Int) {
        fun move(dice: DeterministicDice) {
            val movement = (0..<3).sumOf { dice.roll() }
            position = (position + movement - 1) % 10 + 1
            points += position
        }
    }

    protected lateinit var playerA: Player
    protected lateinit var playerB: Player

    override fun parse(text: String) {
        val lines = text.splitLines()
        val positionA = lines[0].split(": ")[1].toInt()
        val positionB = lines[1].split(": ")[1].toInt()

        playerA = Player(positionA, 0)
        playerB = Player(positionB, 0)
    }

    override fun compute(): String {
        val dice = DeterministicDice()
        val winningPoints = 1000

        while (true) {
            playerA.move(dice)
            if (playerA.points >= winningPoints) {
                return (playerB.points * dice.counter).toString()
            }
            playerB.move(dice)
            if (playerB.points >= winningPoints) {
                return (playerA.points * dice.counter).toString()
            }
        }
    }

    override val exampleAnswer: String
        get() = "739785"
}

class PartB21 : PartA21() {
    private var doQuantumTurnMemoized = ::doQuantumTurn.memoize()

    override fun compute(): String {
        val wins = doQuantumTurnMemoized(playerA.position, playerB.position, 0, 0)
        return max(wins.first, wins.second).toString()
    }

    private fun doQuantumTurn(pos1: Int, pos2: Int, points1: Int, points2: Int): Pair<Long, Long> {
        val possibilities = mapOf(3 to 1, 4 to 3, 5 to 6, 6 to 7, 7 to 6, 8 to 3, 9 to 1)
        var winsA = 0L
        var winsB = 0L

        for ((movement, splits) in possibilities) {
            val pos = (pos1 + movement - 1) % 10 + 1
            val points = points1 + pos
            if (points >= 21) {
                winsA += splits
                continue
            }
            val (nextWinsB, nextWinsA) = doQuantumTurnMemoized(pos2, pos, points2, points)
            winsA += nextWinsA * splits
            winsB += nextWinsB * splits
        }
        return Pair(winsA, winsB)
    }

    override val exampleAnswer: String
        get() = "444356092776315"
}
