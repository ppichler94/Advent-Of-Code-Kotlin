package year2022

import lib.Day
import lib.Part
import lib.math.Vector2i
import kotlin.math.max
import kotlin.math.min

fun main() {
    Day(14, 2022, PartA14(), PartB14()).run()
}


open class PartA14 : Part() {
    internal lateinit var cave: MutableSet<Vector2i>
    internal lateinit var sand: MutableSet<Vector2i>
    internal var maxY = 0

    override fun parse(text: String) {
        cave = mutableSetOf()
        maxY = 0
        text.split("\n")
            .forEach { line ->
                val coordinates = line.split(" -> ")
                (0 until coordinates.size - 1).forEach {
                    val (x1, y1) = coordinates[it].split(",")
                    val (x2, y2) = coordinates[it + 1].split(",")

                    val xStart = min(x1.toInt(), x2.toInt())
                    val xEnd = max(x1.toInt(), x2.toInt())
                    val yStart = min(y1.toInt(), y2.toInt())
                    val yEnd = max(y1.toInt(), y2.toInt())

                    maxY = max(maxY, yEnd)
                    (yStart .. yEnd).forEach { y ->
                        (xStart..xEnd).forEach { x -> cave.add(Vector2i(x, y)) }
                    }
                }
            }
    }

    override fun compute(): String {
        sand = mutableSetOf()
        while (true) {
            val sandResting = addSand()
            if (!sandResting) {
                return sand.size.toString()
            }
        }
    }

    var vecLeft = Vector2i(-1, 0)
    var vecRight = Vector2i(1, 0)
    var vecDown = Vector2i(0, 1)

    internal fun addSand(): Boolean {
        var pos = Vector2i(500, 0)
        while (true) {
            if (pos.y > maxY) {
                return false
            }
            pos += vecDown
            if (collides(pos)) {
                if (!collides(pos + vecLeft)) {
                    pos += vecLeft
                } else if (!collides(pos + vecRight)) {
                    pos += vecRight
                } else {
                    sand.add(pos - vecDown)
                    return true
                }
            }
        }
    }

    private fun collides(pos: Vector2i) = pos in cave || pos in sand

    override val exampleAnswer: String
        get() = "24"
}

class PartB14 : PartA14() {
    override fun compute(): String {
        sand = mutableSetOf()
        (0 until 1000).forEach { cave.add(Vector2i(it, maxY + 2)) }
        maxY += 3
        while (true) {
            addSand()
            if (Vector2i(500, 0) in sand) {
                return sand.size.toString()
            }
        }
    }

    override val exampleAnswer: String
        get() = "93"
}
