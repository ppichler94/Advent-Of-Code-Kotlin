package year2025

import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(5, 2025, PartA5(), PartB5()).run()
}

open class PartA5 : Part() {
    protected lateinit var goodRanges: List<LongRange>
    private lateinit var ingredients: List<Long>

    override fun parse(text: String) {
        val (rangeBlock, ingredientBlock) = text.split("\n\n")
        goodRanges = rangeBlock.lines().map { it.split("-").let { (start, end) -> start.toLong()..end.toLong() } }
        ingredients = ingredientBlock.lines().map { it.toLong() }
    }

    override fun compute(): String =
        ingredients.count { ingredient -> goodRanges.any { range -> ingredient in range } }.toString()

    override val exampleAnswer = "3"
}

class PartB5 : PartA5() {
    override fun compute(): String {
        val sorted = goodRanges.sortedBy { it.first }
        val mergedRanges = mutableListOf(sorted.first())

        sorted.drop(1).forEach { range ->
            var processed = false
            for (i in mergedRanges.indices) {
                when {
                    range.first > mergedRanges[i].last -> {
                        continue
                    }

                    range.first >= mergedRanges[i].first && range.last <= mergedRanges[i].last -> {
                        processed = true
                        break
                    }

                    range.first >= mergedRanges[i].first && range.last >= mergedRanges[i].last -> {
                        mergedRanges[i] = mergedRanges[i].first..range.last
                        processed = true
                        break
                    }
                }
            }
            if (!processed) {
                mergedRanges.add(range)
            }
        }

        return mergedRanges.sumOf { it.last - it.first + 1 }.toString()
    }

    override val exampleAnswer = "14"
}