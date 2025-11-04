package year2022

import lib.aoc.Day
import lib.aoc.Part
import java.math.BigInteger
import java.util.*
import kotlin.collections.ArrayDeque

fun main() {
    Day(20, 2022, PartA20(), PartB20()).run()
}


open class PartA20 : Part() {
    private lateinit var rawNumbers: List<Int>
    private var zeroIndex = 0

    override fun parse(text: String) {
        rawNumbers = text.split("\n").map(String::toInt)
        zeroIndex = rawNumbers.indexOf(0)
    }

    override fun compute(): String {
        return doRounds(BigInteger.ONE, 1).toString()
    }

    protected fun doRounds(key: BigInteger, rounds: Int): Long {
        val originalNumbers = rawNumbers.mapIndexed { i, x -> Pair(i, BigInteger.valueOf(x.toLong()) * key) }
        val numbers = ArrayDeque(originalNumbers)

        repeat(rounds) { mix(originalNumbers, numbers) }

        val newZeroIndex = numbers.indexOf(Pair(zeroIndex, BigInteger.ZERO))
        val number1 = numbers[(newZeroIndex + 1000) % numbers.size].second
        val number2 = numbers[(newZeroIndex + 2000) % numbers.size].second
        val number3 = numbers[(newZeroIndex + 3000) % numbers.size].second
        return (number1 + number2 + number3).longValueExact()
    }

    private fun mix(originalNumbers: List<Pair<Int, BigInteger>>, numbers: ArrayDeque<Pair<Int, BigInteger>>) {
        originalNumbers.forEach {
            val (_, number) = it
            val index = numbers.indexOf(it)
            Collections.rotate(numbers, -index)
            numbers.removeFirst()
            val rotation = (number.longValueExact() % numbers.size.toLong()).toInt()
            Collections.rotate(numbers, -rotation)
            numbers.addFirst(it)
        }
    }

    override val exampleAnswer: String
        get() = "3"
}

class PartB20 : PartA20() {
    override fun compute(): String {
        return doRounds(BigInteger.valueOf(811_589_153), 10).toString()
    }

    override val exampleAnswer: String
        get() = "1623178306"
}
