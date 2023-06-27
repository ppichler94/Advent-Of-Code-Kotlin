package year2022

import lib.Day
import lib.Part
import lib.math.MutableVector2i
import lib.math.Vector2i
import kotlin.math.abs

fun main() {
    Day(9, 2022, Part9(1, "13"), Part9(9, "1")).run()
}


class Part9(private val knotCount: Int, private val exampleData: String) : Part() {
    private lateinit var lines: List<String>
    private lateinit var visited: MutableSet<Vector2i>
    private lateinit var knots: List<MutableVector2i>

    override fun parse(text: String) {
        lines = text.split("\n")
    }

    override fun compute(): String {
        visited = mutableSetOf()
        knots = List(knotCount + 1) { MutableVector2i(0, 0) }
        lines.forEach {
            val (direction, count) = it.split(" ")
            repeat(count.toInt()) { executeStep(direction) }
        }
        return visited.size.toString()
    }

    private fun executeStep(direction: String) {
        when (direction) {
            "U" -> knots[0] += Vector2i(0, 1)
            "D" -> knots[0] -= Vector2i(0, 1)
            "R" -> knots[0] += Vector2i(1, 0)
            "L" -> knots[0] -= Vector2i(1, 0)
        }
        knots.windowed(2).forEach {
            val diff = it[0] - it[1]
            if (diff.length() >= 2) {
                it[1] += diff.direction()
            }
        }
        visited.add(knots.last().toVector())
    }

    private fun Vector2i.direction() = Vector2i(if (x != 0) x / abs(x) else 0, if (y != 0) y / abs(y) else 0)

    override val exampleAnswer: String
        get() = exampleData

    override val customExampleData
        get() = """
            R 4
            U 4
            L 3
            D 1
            R 4
            D 1
            L 5
            R 2
        """.trimIndent()
}