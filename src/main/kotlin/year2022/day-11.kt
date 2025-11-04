package year2022

import lib.aoc.Day
import lib.aoc.Part
import java.math.BigInteger

fun main() {
    Day(11, 2022, PartA11(20, 3), PartB11(10_000, 1)).run()
}


open class PartA11(private val rounds: Int, private val worryLevelFactor: Int) : Part() {
    private lateinit var monkeys: List<Monkey>

    sealed class Operation(val action: (BigInteger) -> BigInteger)
    class MulOperation(factor: Int) : Operation({ it * factor.toBigInteger() })
    class PowOperation : Operation({ it * it })
    class AddOperation(summand: Int) : Operation({ it + summand.toBigInteger() })

    private fun Operation(text: String): Operation {
        val (_, operator, right) = text.split(" ")
        return when {
            operator == "+" -> AddOperation(right.toInt())
            operator == "*" && right == "old" -> PowOperation()
            operator == "*" -> MulOperation(right.toInt())
            else -> throw IllegalArgumentException("unknown operator $operator")
        }
    }

    data class Monkey(
        val items: MutableList<BigInteger>,
        var operation: Operation,
        val testNumer: Int,
        val trueDestination: Int,
        val falseDestination: Int
    ) {
        var itemsInspected: BigInteger = BigInteger.valueOf(0)

        fun turn(monkeys: List<Monkey>, worryLevelFactor: Int) {
            val modulo = monkeys.map(Monkey::testNumer).reduce(Int::times)
            items.forEach {
                val old = it
                var item = operation.action(old)
                item /= worryLevelFactor.toBigInteger()
                item %= modulo.toBigInteger()
                val rest = item % testNumer.toBigInteger()
                if (rest == BigInteger.ZERO) {
                    monkeys[trueDestination].items.add(item)
                } else {
                    monkeys[falseDestination].items.add(item)
                }
                itemsInspected++
            }
            items.clear()
        }
    }

    private fun Monkey(text: String): Monkey {
        val lines = text.split("\n")
        val items = lines[1].split(": ")[1].split(", ").map(String::toBigInteger)
        val operation = Operation(lines[2].split("new = ")[1])
        val testNumber = lines[3].split("by ")[1].toInt()
        val trueDestination = lines[4].split("monkey ")[1].toInt()
        val falseDestination = lines[5].split("monkey ")[1].toInt()
        return Monkey(items.toMutableList(), operation, testNumber, trueDestination, falseDestination)
    }

    override fun parse(text: String) {
        monkeys = text.split("\n\n").map(::Monkey)
    }

    override fun compute(): String {
        repeat(rounds) {
            monkeys.forEach {
                it.turn(monkeys, worryLevelFactor)
            }
        }
        return monkeys.map(Monkey::itemsInspected)
            .sorted()
            .takeLast(2)
            .reduce { acc, x -> acc * x }
            .toString()
    }

    override val exampleAnswer: String
        get() = "10605"
}

class PartB11(rounds: Int, worryLevelFactor: Int) : PartA11(rounds, worryLevelFactor) {
    override val exampleAnswer: String
        get() = "2713310158"
}
