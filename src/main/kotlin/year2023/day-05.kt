package year2023

import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(5, 2023, PartA5(), PartB5()).run()
}

open class PartA5 : Part() {
    protected data class Lut(val destinationStart: Long, val sourceStart: Long, val length: Long) {
        val sourceRange: LongRange
            get() = sourceStart until (sourceStart + length)

        val destinationRange: LongRange
            get() = destinationStart until (destinationStart + length)
    }

    protected data class Converter(val luts: List<Lut>, val destination: String) {
        fun convert(source: Long): Long {
            val lut = luts.firstOrNull { source in it.sourceRange } ?: return source
            return lut.destinationStart + (source - lut.sourceStart)
        }

        companion object {
            fun parse(text: String): Pair<String, Converter> {
                val numRegex = """\d+""".toRegex()
                val lines = text.splitLines()
                val (source, destination) = """(\w+)-to-(\w+).*""".toRegex().find(lines[0])!!.destructured
                val luts = lines
                    .drop(1)
                    .map {
                        val numbers = numRegex.findAll(it).map { it.value.toLong() }.toList()
                        Lut(numbers[0], numbers[1], numbers[2])
                    }
                return Pair(source, Converter(luts, destination))
            }
        }
    }

    protected lateinit var converters: Map<String, Converter>
    protected lateinit var seeds: List<Long>

    override fun parse(text: String) {
        val numRegex = """\d+""".toRegex()
        val parts = text.split("\n\n")

        seeds = numRegex.findAll(parts[0]).map { it.value.toLong() }.toList()
        converters = parts.drop(1)
            .associate { Converter.parse(it) }
    }

    override fun compute(): String {
        return seeds.minOf(::convertToLocation).toString()

    }

    protected fun convertToLocation(seed: Long): Long {
        var category = "seed"
        var number = seed
        while (category != "location") {
            number = converters.getValue(category).convert(number)
            category = converters.getValue(category).destination
        }
        return number
    }

    override val exampleAnswer: String
        get() = "35"
}

class PartB5 : PartA5() {

    override fun compute(): String {
        return seeds
            .chunked(2)
            .map { it[0] until (it[0] + it[1]) }
            .map { it.minOf(::convertToLocation) }
            .min()
            .toString()
    }

    override val exampleAnswer: String
        get() = "46"
}
