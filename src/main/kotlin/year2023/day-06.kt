package year2023

import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(6, 2023, PartA6(), PartB6()).run()
}

open class PartA6 : Part() {
    internal lateinit var times: List<Int>
    internal lateinit var distances: List<Int>

    override fun parse(text: String) {
        val lines = text.splitLines()
        times =
            lines[0]
                .split(":")[1]
                .split(" ")
                .filter { it.isNotEmpty() }
                .map { it.toInt() }
        distances =
            lines[1]
                .split(":")[1]
                .split(" ")
                .filter { it.isNotEmpty() }
                .map { it.toInt() }
    }

    override fun compute(): String =
        (times zip distances)
            .map {
                findMaxDistanceNumber(it.first.toLong(), it.second.toLong())
            }.reduce(Int::times)
            .toString()

    internal fun findMaxDistanceNumber(
        time: Long,
        distance: Long,
    ): Int =
        (1..<time).count {
            val dist = (time - it) * it
            dist > distance
        }

    override val exampleAnswer: String
        get() = "288"
}

class PartB6 : PartA6() {
    override fun compute(): String {
        val time = times.joinToString("") { it.toString() }.toLong()
        val distance = distances.joinToString("") { it.toString() }.toLong()
        return findMaxDistanceNumber(time, distance).toString()
    }

    override val exampleAnswer: String
        get() = "71503"
}