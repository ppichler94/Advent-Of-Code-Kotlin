package year2022

import lib.Day
import lib.Part

fun main() {
    Day(25, 2022, PartA25(), null).run()
}

open class PartA25 : Part() {
    private val snafuForInt = "=-012".mapIndexed { i, c -> i - 2L to c }.associate { it }
    private val intForSnafu = snafuForInt.entries.associate { it.value to it.key }
    private lateinit var lines: List<String>


    override fun parse(text: String) {
        lines = text.split("\n")
    }

    override fun compute(): String {
        val result = lines.sumOf { snafuToInt(it) }
        return intToSnafu(result)
    }

    private fun snafuToInt(snafu: String): Long {
        return snafu.reversed().fold(0L to 1L) { (acc, pow), c ->
            Pair(acc + intForSnafu.getValue(c) * pow, pow * 5L)
        }.first
    }

    private fun intToSnafu(number: Long): String {
        var tmp = number
        var result = ""
        while (tmp != 0L) {
            var newNumber = tmp / 5L
            var rest = tmp.mod(5L)
            if (rest > 2) {
                rest -= 5
                newNumber += 1L
            }
            result += snafuForInt[rest]
            tmp = newNumber
        }

        return result.reversed()
    }

    override val exampleAnswer: String
        get() = "2=-1=0"
}
