package year2021

import lib.Day
import lib.Part

fun main() {
    Day(1, 2021, PartA1(), PartB1()).run()
}


open class PartA1 : Part() {
    protected lateinit var numbers: List<Int>

    override fun parse(text: String) {
        numbers = text.split("\n").map(String::toInt)
    }

    override fun compute(): String {
        return numbers
            .windowed(2)
            .count { (a, b) -> b > a }
            .toString()
    }

    override val exampleAnswer: String
        get() = "7"
}

class PartB1 : PartA1() {
    override fun compute(): String {
        return numbers
            .windowed(3)
            .map{ it.sum() }
            .windowed(2)
            .count { (a, b) -> b > a }
            .toString()
    }

    override val exampleAnswer: String
        get() = "5"
}
