package year2024

import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(11, 2024, PartA11(), PartB11()).run()
}

open class PartA11 : Part() {
    protected var stoneMap = mutableMapOf<Long, Long>()

    override fun parse(text: String) {
        stoneMap.clear()
        text.split(" ").forEach {
            stoneMap[it.toLong()] = stoneMap.getOrDefault(it.toLong(), 0) + 1
        }
    }

    override fun compute(): String {
        repeat(25) {
            stoneMap = stoneMap.tick()
        }
        return stoneMap.values.sum().toString()
    }

    protected fun MutableMap<Long, Long>.tick(): MutableMap<Long, Long> {
        val newStones = mutableMapOf<Long, Long>()
        forEach { (number, count) ->
            val digits = number.toString().length
            when {
                number == 0L -> newStones[1L] = newStones.getOrDefault(1L, 0L) + count
                digits % 2 == 0 -> {
                    val first = number.toString().take(digits / 2).toLong()
                    val second = number.toString().takeLast(digits / 2).toLong()
                    newStones[first] = newStones.getOrDefault(first, 0L) + count
                    newStones[second] = newStones.getOrDefault(second, 0L) + count
                }
                else -> newStones[number * 2024L] = newStones.getOrDefault(number * 2024L, 0L) + count
            }
        }
        return newStones
    }

    override val exampleAnswer = "55312"

    override val customExampleData = "125 17"
}

class PartB11 : PartA11() {
    override fun compute(): String {
        repeat(75) {
            stoneMap = stoneMap.tick()
        }
        return stoneMap.values.sum().toString()
    }

    override val exampleAnswer = "65601038650482"
}