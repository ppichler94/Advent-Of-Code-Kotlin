package year2021

import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(3, 2021, PartA3(), PartB3()).run()
}


open class PartA3 : Part() {
    protected lateinit var numbers: List<String>

    override fun parse(text: String) {
        numbers = text.split("\n")
    }

    override fun compute(): String {
        val gammaRate = numbers[0].indices.map { numbers.mostCommonBitAt(it) }.joinToString("")
        val converter = mapOf('0' to '1', '1' to '0')
        val epsilonRate = gammaRate.map { converter[it]!! }.joinToString("")
        return (gammaRate.toInt(2) * epsilonRate.toInt(2)).toString()
    }

    protected fun List<String>.mostCommonBitAt(pos: Int): Char {
        val numberOf0s = count { it[pos] == '0' }
        val numberOf1s = count { it[pos] == '1' }
        return if (numberOf1s >= numberOf0s) '1' else '0'
    }

    override val exampleAnswer: String
        get() = "198"
}

class PartB3 : PartA3() {
    override fun compute(): String {
        val oxygenNumber = filter(mapOf('0' to '0', '1' to '1'))
        val scrubberNumber = filter(mapOf('0' to '1', '1' to '0'))

        return (oxygenNumber.toInt(2) * scrubberNumber.toInt(2)).toString()
    }

    private fun filter(converter: Map<Char, Char>): String {
        var mutableNumbers = numbers
        mutableNumbers[0].indices.map { i: Int ->
            val bit = mutableNumbers.mostCommonBitAt(i)
            mutableNumbers = mutableNumbers.filter { it[i] == converter[bit] }
            if (mutableNumbers.size == 1) {
                return mutableNumbers.first()
            }
        }
        return mutableNumbers.first()
    }

    override val exampleAnswer: String
        get() = "230"
}
