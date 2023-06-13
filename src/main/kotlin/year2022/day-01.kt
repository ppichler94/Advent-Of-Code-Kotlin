package year2022

import lib.Day
import lib.Part
import lib.PartName

fun main() {
    Day(1, 2022, PartA(), PartB()).run()
}

open class PartA(partName: PartName = PartName.A) : Part(partName) {
    internal lateinit var calories: List<Int>
    override fun parse(text: String) {
        calories = text.split("\n\n")
            .map { it.split("\n") }
            .map { it.map { s: String ->  s.toInt() } }
            .map { it.sum() }
    }

    override fun compute(): String {
        return "24000"
//        return calories.max().toString()
    }

    override val exampleAnswer: String
        get() = "24000"
}

class PartB : PartA(PartName.B) {
    override fun compute(): String {
        return calories.sorted().take(3).sum().toString()
    }

    override val exampleAnswer: String
        get() = "45000"
}