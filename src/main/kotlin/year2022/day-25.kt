package year2022

import lib.Day
import lib.Part
import java.math.BigInteger

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

    private fun snafuToInt(snafu: String): BigInteger {
        return snafu.reversed().fold(Pair(BigInteger.ZERO, BigInteger.ONE)) { (acc, pow), c ->
            Pair(acc + intForSnafu.getValue(c).toBigInteger() * pow, pow * BigInteger.valueOf(5))
        }.first
    }

    private fun intToSnafu(number: BigInteger): String {
        var tmp = number
        var result = ""
        while (tmp != BigInteger.ZERO) {
            var newNumber = tmp / BigInteger.valueOf(5)
            var rest = tmp.mod(BigInteger.valueOf(5)).intValueExact()
            if (rest > 2) {
                rest -= 5
                newNumber += BigInteger.ONE
            }
            result += snafuForInt[rest.toLong()]
            tmp = newNumber
        }

        return result.reversed()
    }

    override val exampleAnswer: String
        get() = "2=-1=0"
}
