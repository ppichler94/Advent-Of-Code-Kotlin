package year2023

import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(7, 2023, PartA7(), PartB7()).run()
}

open class PartA7 : Part() {
    protected data class Hand(val cards: String, val bid: Int, val strength: Int)

    protected open fun getStrength(cards: String): Int {
        val counts = cards.groupingBy { it }.eachCount().toMutableMap()
        val highCard = counts.maxByOrNull { it.value }?.key
        val maxCount = counts.remove(highCard) ?: 0
        return when (maxCount) {
            5 -> 7
            4 -> 6
            3 -> if (counts.values.contains(2)) 5 else 4
            2 -> if (counts.values.contains(2)) 3 else 2
            else -> 1
        }
    }

    protected val cardValues = mutableMapOf(
        'A' to 14,
        'K' to 13,
        'Q' to 12,
        'J' to 11,
        'T' to 10,
        '9' to 9,
        '8' to 8,
        '7' to 7,
        '6' to 6,
        '5' to 5,
        '4' to 4,
        '3' to 3,
        '2' to 2,
    )

    private val handComparator = Comparator<Hand> { p1, p2 ->
        if (p1.strength != p2.strength) {
            return@Comparator p1.strength - p2.strength
        }

        val difference = (p1.cards zip p2.cards).firstOrNull { it.first != it.second }
        if (difference != null) {
            return@Comparator cardValues[difference.first]!! - cardValues[difference.second]!!
        }

        0
    }

    private lateinit var hands: List<Hand>

    override fun parse(text: String) {
        val lines = text.splitLines()
        hands = lines.map {
            val (cards, bid) = it.split(" ")
            Hand(cards, bid.toInt(), getStrength(cards))
        }
    }

    override fun compute(): String {
        val sortedHands = hands.sortedWith(handComparator)
        return sortedHands
            .mapIndexed { i, hand -> (i + 1) * hand.bid }
            .sum()
            .toString()
    }

    override val exampleAnswer: String
        get() = "6440"
}

class PartB7 : PartA7() {
    override fun getStrength(cards: String): Int {
        val counts = cards.groupingBy { it }.eachCount().toMutableMap()
        val jokers = counts.remove('J') ?: 0
        val highCard = counts.maxByOrNull { it.value }?.key
        val maxCount = counts.remove(highCard) ?: 0
        return when (maxCount + jokers) {
            5 -> 7
            4 -> 6
            3 -> if (counts.values.contains(2)) 5 else 4
            2 -> if (counts.values.contains(2)) 3 else 2
            else -> 1
        }
    }

    override fun compute(): String {
        cardValues['J'] = 1
        return super.compute()
    }

    override val exampleAnswer: String
        get() = "5905"
}
