package year2021

import lib.aoc.Day
import lib.aoc.Part
import lib.memoize

fun main() {
    Day(14, 2021, PartA14(), PartB14()).run()
}

open class PartA14(private val steps: Long = 10) : Part() {
    private data class Counter(val data: MutableMap<Char, Long>) {
        constructor(text: String) : this(mutableMapOf()) {
            text.forEach { addOrSet(it, 1) }
        }

        fun update(counter: Counter) {
            counter.data.forEach { (k, v) -> addOrSet(k, v) }
        }

        private fun addOrSet(k: Char, v: Long) {
            val myValue = data[k]
            if (myValue != null) {
                data[k] = myValue + v
            } else {
                data[k] = v
            }
        }
    }

    private lateinit var template: String
    private lateinit var rules: Map<String, String>
    private lateinit var countMemoized: (String, Long) -> Counter

    override fun parse(text: String) {
        val (templateText, rulesText) = text.split("\n\n")
        template = templateText
        rules = rulesText.split("\n").map { it.split(" -> ") }.associate { it[0] to it[1] }
    }

    override fun compute(): String {
        countMemoized = { pair: String, step: Long -> count(pair, step) }.memoize()
        val counter = Counter(template)
        template.windowed(2).forEach {
            counter.update(countMemoized(it, 0L))
        }
        return (counter.data.values.max() - counter.data.values.min()).toString()
    }


    private fun count(pair: String, step: Long): Counter {
        if (step == steps || pair !in rules.keys) {
            return Counter("")
        }

        val insertion = rules.getValue(pair)
        val counter = Counter(insertion)
        counter.update(countMemoized(pair[0] + insertion, step + 1))
        counter.update(countMemoized(insertion + pair[1], step + 1))
        return counter
    }

    override val exampleAnswer: String
        get() = "1588"
}

class PartB14 : PartA14(40) {
    override val exampleAnswer: String
        get() = "2188189693529"
}
