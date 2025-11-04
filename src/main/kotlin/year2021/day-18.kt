package year2021

import lib.aoc.Day
import lib.aoc.Part
import lib.math.product
import kotlin.math.roundToInt

fun main() {
    Day(18, 2021, PartA18(), PartB18()).run()
}

open class PartA18 : Part() {
    protected lateinit var numbers: MutableList<String>

    override fun parse(text: String) {
        numbers = text.split("\n").toMutableList()
    }

    override fun compute(): String {
        var number = numbers.removeFirst()
        while (numbers.isNotEmpty()) {
            val nextNumber = numbers.removeFirst()
            number = "[${number},${nextNumber}]"
            number = reduce(number)
        }
        return magnitude(number).toString()
    }

    protected fun reduce(number: String): String {
        val exploded = explode(number)
        if (exploded != number) {
            return reduce(exploded)
        } else {
            val splitted = split(number)
            if (splitted != number) {
                return reduce(splitted)
            }
            return splitted
        }
    }

    private fun explode(number: String): String {
        var offset = 0
        for (num in """\[\d+,\d+]""".toRegex().findAll(number)) {
            val pair = num.value.toRegex(RegexOption.LITERAL).find(number.drop(offset))!!
            val leftBracketsCount = number.take(pair.range.first + offset).count { it == '[' }
            val rightBracketsCount = number.take(pair.range.first + offset).count { it == ']' }
            if (leftBracketsCount - rightBracketsCount >= 4) {
                val (left, right) = pair.value.drop(1).dropLast(1).split(",")
                var leftPart = number.take(pair.range.first + offset).reversed()
                var rightPart = number.drop(pair.range.last + offset + 1)
                """\d+""".toRegex().find(leftPart)?.let {
                    val x = (leftPart.slice(it.range).reversed().toInt() + left.toInt()).toString().reversed()
                    leftPart = "${leftPart.take(it.range.first)}$x${leftPart.drop(it.range.last + 1)}"
                }
                """\d+""".toRegex().find(rightPart)?.let {
                    val x = rightPart.slice(it.range).toInt() + right.toInt()
                    rightPart = "${rightPart.take(it.range.first)}$x${rightPart.drop(it.range.last + 1)}"
                }
                return "${leftPart.reversed()}0$rightPart"
            } else {
                offset += pair.range.last + 1
            }
        }
        return number
    }

    private fun split(number: String): String {
        """\d\d""".toRegex().find(number)?.let {
            val leftPart = number.take(it.range.first)
            val rightPart = number.drop(it.range.last + 1)
            val newLeftDigit = it.value.toInt() / 2
            val newRightDigit = (it.value.toInt() / 2f).roundToInt()
            return "$leftPart[$newLeftDigit,$newRightDigit]$rightPart"
        }
        return number
    }

    protected fun magnitude(data: String): Int {
        var number = data
        while (number.count { it == ',' } > 1) {
            for (num in """\[\d+,\d+]""".toRegex().findAll(number)) {
                val pair = num.value.toRegex(RegexOption.LITERAL).find(number)!!
                val (left, right) = pair.value.drop(1).dropLast(1).split(",")
                val newNumber = left.toInt() * 3 + right.toInt() * 2
                number = "${number.take(pair.range.first)}$newNumber${number.drop(pair.range.last + 1)}"
            }
        }
        val (left, right) = number.drop(1).dropLast(1).split(",")
        return left.toInt() * 3 + right.toInt() * 2
    }

    override val exampleAnswer: String
        get() = "4230"
}

class PartB18 : PartA18() {
    override fun compute(): String {
        val pairs = numbers.permutations()
        return pairs.maxOf { pair -> magnitudeOfPair(pair) }.toString()
    }

    private fun <T> Iterable<T>.permutations(): List<Pair<T, T>> {
        return this.product(this).filter { it.first != it.second }
    }

    private fun magnitudeOfPair(pair: Pair<String, String>): Int {
        var number = "[${pair.first},${pair.second}]"
        number = reduce(number)
        return magnitude(number)
    }

    override val exampleAnswer: String
        get() = "4647"
}
