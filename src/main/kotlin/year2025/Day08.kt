package year2025

import lib.aoc.Day
import lib.aoc.Part
import lib.math.Vector
import lib.math.combinations
import lib.math.distanceTo
import lib.splitLines

fun main() {
    Day(8, 2025, PartA8(), PartB8()).run()
}

open class PartA8 : Part() {
    private var targetCircuits = 10
    protected lateinit var positions: List<Vector>

    override fun parse(text: String) {
        positions =
            text
                .splitLines()
                .map { Vector(it.split(",").map(String::toInt)) }
        if (positions.size > 20) {
            targetCircuits = 1000
        }
    }

    override fun compute(): String {
        val circuits = mutableMapOf<Vector, Int>()
        val connections =
            positions
                .combinations()
                .map { it.first.distanceTo(it.second) to it }
                .sortedBy { it.first }
                .take(targetCircuits)
        var nextId = 1
        connections.forEach { connection ->
            val (_, boxes) = connection
            val firstCircuit = circuits[boxes.first]
            val secondCircuit = circuits[boxes.second]
            if (firstCircuit != null && secondCircuit != null && firstCircuit != secondCircuit) {
                val toChange = circuits.entries.filter { it.value == secondCircuit }
                toChange.forEach { circuits[it.key] = firstCircuit }
            } else if (firstCircuit == null && secondCircuit == null) {
                val id = nextId++
                circuits[boxes.first] = id
                circuits[boxes.second] = id
            } else if (firstCircuit != null) {
                circuits[boxes.second] = firstCircuit
            } else if (secondCircuit != null) {
                circuits[boxes.first] = secondCircuit
            }
        }
        return circuits.values
            .groupBy { it }
            .entries
            .sortedBy { it.value.size }
            .reversed()
            .take(3)
            .fold(1) { acc, x -> acc * x.value.size }
            .toString()
    }

    override val exampleAnswer = "40"
}

class PartB8 : PartA8() {
    override fun compute(): String {
        val circuits = mutableMapOf<Vector, Int>()
        val connections =
            positions
                .combinations()
                .map { it.first.distanceTo(it.second) to it }
                .sortedBy { it.first }
        var nextId = 1
        var lastConnection: Pair<Vector, Vector> = connections.first().second
        connections.forEach { connection ->
            val (_, boxes) = connection
            val firstCircuit = circuits[boxes.first]
            val secondCircuit = circuits[boxes.second]
            if (firstCircuit != null && secondCircuit != null && firstCircuit != secondCircuit) {
                val toChange = circuits.entries.filter { it.value == secondCircuit }
                toChange.forEach { circuits[it.key] = firstCircuit }
            } else if (firstCircuit == null && secondCircuit == null) {
                val id = nextId++
                circuits[boxes.first] = id
                circuits[boxes.second] = id
            } else if (firstCircuit != null) {
                circuits[boxes.second] = firstCircuit
            } else if (secondCircuit != null) {
                circuits[boxes.first] = secondCircuit
            }
            lastConnection = connection.second
            if (circuits.size == positions.size && circuits.values.toSet().size == 1) {
                return (lastConnection.first.x.toLong() * lastConnection.second.x.toLong()).toString()
            }
        }
        return "-1"
    }

    override val exampleAnswer = "25272"
}