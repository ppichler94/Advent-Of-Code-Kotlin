package year2024

import lib.Grid2d
import lib.Position
import lib.aoc.Day
import lib.aoc.Part
import lib.math.product
import lib.splitLines

fun main() {
    Day(8, 2024, PartA8(), PartB8()).run()
}

open class PartA8 : Part() {
    data class Antenna(
        val position: Position,
        val frequency: Char,
    )

    private lateinit var antennaGroups: Map<Char, List<Antenna>>
    protected lateinit var limits: List<Iterable<Int>>

    override fun parse(text: String) {
        val map = Grid2d.ofLines(text.splitLines())
        val antennas =
            map
                .findAll { it != '.' }
                .map { Antenna(it, map[it]) }
        antennaGroups = antennas.groupBy { it.frequency }
        limits = map.limits
    }

    override fun compute(): String =
        antennaGroups.values
            .flatMap { antinodesForFrequency(it) }
            .toSet()
            .count { it.isInLimits(limits) }
            .toString()

    private fun antinodesForFrequency(antennas: List<Antenna>): Set<Position> =
        antennas
            .product()
            .filter { (a, b) -> a != b }
            .flatMap { (a, b) -> antinodes(a, b) }
            .toSet()

    protected open fun antinodes(
        a: Antenna,
        b: Antenna,
    ): Iterable<Position> {
        val distance = (a.position - b.position.position).position
        return listOf(a.position + distance, b.position - distance)
    }

    override val exampleAnswer: String
        get() = "14"
}

class PartB8 : PartA8() {
    override fun antinodes(
        a: Antenna,
        b: Antenna,
    ): Iterable<Position> {
        val distance = (a.position - b.position.position).position
        val result = mutableListOf<Position>(a.position, b.position)

        var pos = a.position + distance
        while (pos.isInLimits(limits)) {
            result.add(pos)
            pos += distance
        }

        pos = b.position - distance
        while (pos.isInLimits(limits)) {
            result.add(pos)
            pos -= distance
        }

        return result
    }

    override val exampleAnswer: String
        get() = "34"
}