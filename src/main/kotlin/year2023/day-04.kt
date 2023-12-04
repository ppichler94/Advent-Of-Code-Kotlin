package year2023

import lib.aoc.Day
import lib.aoc.Part
import lib.memoize
import lib.splitLines
import kotlin.math.pow

fun main() {
    Day(4, 2023, PartA4(), PartB4()).run()
}

open class PartA4 : Part() {
    protected data class Card(val id: Int, val winningNumbers: List<Int>, val yourNumbers: List<Int>) {
        val points: Int
            get() = yourNumbers.count { it in winningNumbers }

        companion object {
            fun ofLine(line: String): Card {
                val (cardText, numbers) = line.split(":")
                val id = """\d+""".toRegex().find(cardText)!!.value.toInt()
                val (winningText, yourText) = numbers.split(" | ")
                val winningNumbers = """\d+""".toRegex().findAll(winningText).map { it.value.toInt() }.toList()
                val yourNumbers = """\d+""".toRegex().findAll(yourText).map { it.value.toInt() }.toList()
                return Card(id, winningNumbers, yourNumbers)
            }
        }
    }

    protected lateinit var cards: List<Card>

    override fun parse(text: String) {
        cards = text.splitLines().map(Card::ofLine)
    }

    override fun compute(): String {
        return cards
            .map { it.points }
            .filter { it > 0 }
            .map { it - 1 }
            .sumOf { 2.0.pow(it.toDouble()).toInt() }
            .toString()
    }

    override val exampleAnswer: String
        get() = "13"
}

class PartB4 : PartA4() {
    private val pointsFn = ::points.memoize()
    override fun compute(): String {
        val cardMap = cards.indices
            .associate { index ->
                index + 1 to cards
                    .drop(index + 1)
                    .take(cards[index].points)
                    .map { it.id }
            }

        return cards.indices
            .sumOf { pointsFn(cardMap, it + 1) }
            .toString()
    }

    private fun points(cardMap: Map<Int, List<Int>>, cardId: Int): Int {
        if (cardMap[cardId] == null || cardMap[cardId]?.isEmpty() == true) {
            return 1
        }
        return cardMap[cardId]!!.sumOf { pointsFn(cardMap, it) } + 1
    }

    override val exampleAnswer: String
        get() = "30"
}
