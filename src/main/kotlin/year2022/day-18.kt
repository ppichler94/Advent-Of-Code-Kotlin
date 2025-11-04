package year2022

import lib.TraversalBreadthFirstSearch
import lib.aoc.Day
import lib.aoc.Part
import lib.math.Vector

fun main() {
    Day(18, 2022, PartA18(), PartB18()).run()
}


open class PartA18 : Part() {
    protected lateinit var cubes: List<Vector>

    override fun parse(text: String) {
        cubes = text.split("\n")
            .map { it.split(",") }
            .map { Vector.at(it[0].toInt(), it[1].toInt(), it[2].toInt()) }
    }

    override fun compute(): String {
        return cubes.flatMap { it.neighbours() }
            .count { it !in cubes }
            .toString()
    }


    protected fun Vector.neighbours(): List<Vector> {
        return listOf(
            Vector.at(this.x + 1, this.y, this.z),
            Vector.at(this.x - 1, this.y, this.z),
            Vector.at(this.x, this.y + 1, this.z),
            Vector.at(this.x, this.y - 1, this.z),
            Vector.at(this.x, this.y, this.z + 1),
            Vector.at(this.x, this.y, this.z - 1),
        )
    }

    override val exampleAnswer: String
        get() = "64"
}

class PartB18 : PartA18() {
    override fun compute(): String {
        val exteriorCubes = findExteriorCubes()
        return cubes.flatMap { it.neighbours() }
            .count { it in exteriorCubes }
            .toString()
    }

    private fun findExteriorCubes(): Set<Vector> {
        val lowerLimit = Vector.at(cubes.minOf { it.x } - 1, cubes.minOf { it.y } - 1, cubes.minOf { it.z } - 1)
        val upperLimit = Vector.at(cubes.maxOf { it.x } + 2, cubes.maxOf { it.y } + 2, cubes.maxOf { it.z } + 2)

        fun nextNodes(node: Vector): List<Vector> {
            return node.neighbours()
                .filter {
                    it.x in lowerLimit.x..upperLimit.x
                            && it.y in lowerLimit.y..upperLimit.y
                            && it.z in lowerLimit.z..upperLimit.z
                }
        }

        val traversal = TraversalBreadthFirstSearch { node, _ -> nextNodes(node) }
            .startFrom(lowerLimit)
            .withAlreadyVisited(cubes.toSet())

        return traversal.toSet()

    }

    override val exampleAnswer: String
        get() = "58"
}
