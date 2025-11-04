package year2023

import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(1, 2023, PartA1(), PartB1()).run()
}

open class PartA1 : Part() {
    private lateinit var lines: List<String>
    override fun parse(text: String) {
        lines = text.splitLines()
    }

    override fun compute(): String {
        return lines
            .map(::findDigit)
            .map(String::toInt)
            .sum()
            .toString()
    }

    protected open fun findDigit(line: String): String {
        val numbers = """\d""".toRegex().findAll(line)
        return numbers.first().value + numbers.last().value
    }

    override val exampleAnswer: String
        get() = "142"
}

class PartB1 : PartA1() {
    override fun findDigit(line: String): String {
        val regex = """(?:one|two|three|four|five|six|seven|eight|nine|\d)""".toRegex()
        val matches = line.indices.mapNotNull {
            regex.find(line, it)?.value
        }
        val digitMap = mapOf(
            "one" to "1",
            "two" to "2",
            "three" to "3",
            "four" to "4",
            "five" to "5",
            "six" to "6",
            "seven" to "7",
            "eight" to "8",
            "nine" to "9",
        )
        val first = if (matches.first() in digitMap) digitMap[matches.first()] else matches.first()
        val second = if (matches.last() in digitMap) digitMap[matches.last()] else matches.last()
        return first + second
    }

    override val exampleAnswer: String
        get() = "281"

    override val customExampleData: String
        get() = """
            two1nine
            eightwothree
            abcone2threexyz
            xtwone3four
            4nineeightseven2
            zoneight234
            7pqrstsixteen
        """.trimIndent()
}
