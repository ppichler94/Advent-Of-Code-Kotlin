package year2022

import lib.aoc.Day
import lib.aoc.Part
import lib.aoc.TestCase

fun main() {
    Day(6, 2022, PartA6(4), PartB6()).run()
}


open class PartA6(private var packetLength: Int) : Part() {
    internal lateinit var text: String
    override fun parse(text: String) {
        this.text = text
    }

    override fun compute(): String {
        (packetLength until text.length).forEach {
            if (allDifferent(text.slice(it - packetLength until it))) {
                return it.toString()
            }
        }
        return "Packet not found"
    }

    private fun allDifferent(text: String): Boolean {
        return text.toSet().size == text.length
    }

    override val exampleAnswer: String
        get() = "7"

    override val testCases = sequence {
        yield(TestCase("Example2", "bvwbjplbgvbhsrlpgdmjqwftvncz", "5"))
        yield(TestCase("Example3", "nppdvjthqldpwncqszvftbrmjlhg", "6"))
        yield(TestCase("Example4", "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", "10"))
        yield(TestCase("Example5", "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", "11"))
    }
}

class PartB6 : PartA6(14) {
    override val exampleAnswer: String
        get() = "19"

    override val testCases = sequence {
        yield(TestCase("Example2", "bvwbjplbgvbhsrlpgdmjqwftvncz", "23"))
        yield(TestCase("Example3", "nppdvjthqldpwncqszvftbrmjlhg", "23"))
        yield(TestCase("Example4", "nznrnfrfntjfmvfwmzdfjlvtqnbhcprsg", "29"))
        yield(TestCase("Example5", "zcfzfwzzqfrljwzlrfnpqdbhtmscgvjw", "26"))
    }
}
