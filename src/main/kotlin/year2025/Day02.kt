package year2025

import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(2, 2025, PartA2(), PartB2()).run()
}

open class PartA2 : Part() {
    private lateinit var ranges: List<LongRange>

    override fun parse(text: String) {
        ranges =
            text
                .replace("\n", "")
                .split(",")
                .map {
                    val (start, end) = it.split("-", limit = 2).map(String::toLong)
                    start..end
                }
    }

    override fun compute(): String =
        ranges
            .flatMap { range -> range.filterNot { isValid(it) } }
            .sum()
            .toString()

    protected open fun isValid(idNumber: Long): Boolean {
        val id = idNumber.toString()
        return id.take(id.length / 2) != id.drop(id.length / 2)
    }

    override val exampleAnswer = "1227775554"
}

class PartB2 : PartA2() {
    private fun findRotation(idNumber: Long): Int {
        val id = idNumber.toString()
        val paddedId = "$id$id"
        id.indices.drop(1).forEach { i ->
            if (paddedId.substring(i, i + id.length) == id && id[i] != '0') {
                return i
            }
        }
        return 0
    }

    override fun isValid(idNumber: Long): Boolean = findRotation(idNumber) == 0

    override val exampleAnswer = "4174379265"
}