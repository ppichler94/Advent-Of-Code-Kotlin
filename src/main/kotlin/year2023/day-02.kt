package year2023

import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(2, 2023, PartA2(), PartB2()).run()
}

open class PartA2 : Part() {
    data class Game(var id: Int, var sets: List<Set>)

    data class Set(var red: Int, var green: Int, var blue: Int) {
        operator fun plus(other: Set): Set {
            return Set(red + other.red, green + other.green, blue + other.blue)
        }

        companion object {
            /**
             * Parses a string to create a set.
             * The string is of form "6 red, 1 blue, 3 green".
             * Colors may be missing and additional whitespace is trimmed.
             */
            fun fromString(text: String): Set {
                val cubes = text.trim()
                    .split(",")
                    .map { it.trim().split(" ") }
                    .associate { it.last() to it.first().toInt() }
                return Set(cubes["red"] ?: 0, cubes["green"] ?: 0, cubes["blue"] ?: 0)
            }
        }
    }

    protected lateinit var games: List<Game>

    override fun parse(text: String) {
        val regex = """Game (\d+):(.*)""".toRegex()
        games = text.splitLines()
            .map {
                val result = regex.matchEntire(it)
                val (id, rest) = result!!.destructured
                val sets = rest.split(";").map(Set::fromString)
                Game(id.toInt(), sets)
            }
    }

    override fun compute(): String {
        return games.filter {
            it.sets.all { it.red <= 12 && it.green <= 13 && it.blue <= 14 }
        }.sumOf { it.id }.toString()

    }

    override val exampleAnswer: String
        get() = "8"
}

class PartB2 : PartA2() {
    override fun compute(): String {
        return games.sumOf {
            val red = it.sets.maxOf(Set::red)
            val green = it.sets.maxOf(Set::green)
            val blue = it.sets.maxOf(Set::blue)
            red * green * blue
        }.toString()
    }

    override val exampleAnswer: String
        get() = "2286"
}
