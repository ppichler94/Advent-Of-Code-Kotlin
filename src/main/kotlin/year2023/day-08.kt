package year2023

import lib.aoc.Day
import lib.aoc.Part
import lib.aoc.TestCase
import lib.math.Itertools
import lib.splitLines

fun main() {
    Day(8, 2023, PartA8(), PartB8()).run()
}

open class PartA8 : Part() {
    protected lateinit var network: Map<String, Node>
    protected lateinit var instructions: String

    protected data class Node(
        val left: String,
        val right: String,
    )

    protected fun <T> Sequence<T>.repeat() = sequence { while (true) yieldAll(this@repeat) }

    override fun parse(text: String) {
        val (inst, maps) = text.split("\n\n")
        val re = """(\w*) = \((\w*), (\w*)\)""".toRegex()
        network =
            maps
                .splitLines()
                .associate {
                    val match = re.matchEntire(it)!!
                    val start = match.groupValues[1]
                    val left = match.groupValues[2]
                    val right = match.groupValues[3]
                    start to Node(left, right)
                }
        instructions = inst
    }

    override fun compute(): String = goToEnd().toString()

    protected open fun goToEnd(): Int {
        var position = "AAA"
        val instructionStream = instructions.asSequence().repeat().iterator()
        for (steps in Itertools.count(0)) {
            if (position == "ZZZ") {
                return steps
            }
            val instruction = instructionStream.next()
            when (instruction) {
                'L' -> position = network[position]!!.left
                'R' -> position = network[position]!!.right
            }
        }
        error("cannot be reached")
    }

    override val exampleAnswer: String
        get() = "2"

    override val testCases =
        sequence {
            yield(
                TestCase(
                    "Example2",
                    """
                    LLR

                    AAA = (BBB, BBB)
                    BBB = (AAA, ZZZ)
                    ZZZ = (ZZZ, ZZZ)
                    """.trimIndent(),
                    "6",
                ),
            )
        }
}

class PartB8 : PartA8() {
    override fun goToEnd(): Int {
        var positions = network.keys.filter { it.last() == 'A' }
        val instructionStream = instructions.asSequence().repeat().iterator()
        for (steps in Itertools.count(0)) {
            if (positions.all { it.last() == 'Z' }) {
                return steps
            }
            val instruction = instructionStream.next()
            when (instruction) {
                'L' -> positions = positions.map { network[it]!!.left }
                'R' -> positions = positions.map { network[it]!!.right }
            }
        }
        error("cannot be reached")
    }

    override val customExampleData: String
        get() =
            """
            LR

            11A = (11B, XXX)
            11B = (XXX, 11Z)
            11Z = (11B, XXX)
            22A = (22B, XXX)
            22B = (22C, 22C)
            22C = (22Z, 22Z)
            22Z = (22B, 22B)
            XXX = (XXX, XXX)
            """.trimIndent()

    override val exampleAnswer: String
        get() = "6"

    override val testCases = sequence<TestCase> {}
}