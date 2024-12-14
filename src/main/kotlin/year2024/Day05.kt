package year2024

import lib.aoc.Day
import lib.aoc.Part
import lib.splitLines

fun main() {
    Day(5, 2024, PartA5(), PartB5()).run()
}

open class PartA5 : Part() {
    protected data class Rule(
        val first: Int,
        val second: Int,
    )

    protected data class Rules(
        val rules: List<Rule>,
    ) {
        fun check(update: List<Int>): Boolean {
            val pagesBefore = mutableSetOf<Int>()

            for (page in update) {
                if (page in pagesBefore) {
                    return false
                }
                pagesBefore.addAll(rules.filter { it.second == page }.map { it.first })
            }

            return true
        }

        fun fix(update: List<Int>): List<Int> {
            var fixedUpdate = update.toMutableList()
            val pagesBefore = mutableSetOf<Int>()

            update.forEachIndexed { index, page ->
                if (page in pagesBefore) {
                    val tmp = fixedUpdate[index - 1]
                    fixedUpdate[index - 1] = page
                    fixedUpdate[index] = tmp
                    return fix(fixedUpdate)
                }
                pagesBefore.addAll(rules.filter { it.second == page }.map { it.first })
            }

            return fixedUpdate
        }
    }

    protected lateinit var rules: Rules
    protected lateinit var updates: List<List<Int>>

    override fun parse(text: String) {
        val (rulesText, updatesText) = text.split("\n\n")

        rules =
            rulesText
                .splitLines()
                .map { line ->
                    val (first, second) = line.split("|")
                    Rule(first.toInt(), second.toInt())
                }.let { Rules(it) }

        updates =
            updatesText.splitLines().map { line ->
                line.split(",").map(String::toInt)
            }
    }

    override fun compute(): String = updates.filter { rules.check(it) }.sumOf { it[it.size / 2] }.toString()

    override val exampleAnswer: String
        get() = "143"
}

class PartB5 : PartA5() {
    override fun compute(): String =
        updates
            .filter { !rules.check(it) }
            .map { rules.fix(it) }
            .sumOf { it[it.size / 2] }
            .toString()

    override val exampleAnswer: String
        get() = "123"
}
