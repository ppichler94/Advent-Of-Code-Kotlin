package year2021

import lib.TraversalBreadthFirstSearch
import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(12, 2021, PartA12(), PartB12()).run()
}

open class PartA12 : Part() {
    private data class State(val pos: String, val visited: List<String>)

    private lateinit var edges: Map<String, List<String>>

    override fun parse(text: String) {
        edges = text
            .split("\n")
            .map { it.split("-") }
            .flatMap { listOf(it[0] to it[1], it[1] to it[0]) }
            .groupBy({ it.first }, { it.second })
    }

    override fun compute(): String {
        val traversal = TraversalBreadthFirstSearch { s, _ -> nextEdges(s) }
            .startFrom(State("start", listOf("start")))
        return traversal.count { (pos, _) -> pos == "end" }.toString()
    }

    private fun nextEdges(state: State): Iterable<State> {
        val (pos, visited) = state
        if (pos == "end") {
            return emptyList()
        }
        return edges.getValue(pos)
            .filter { visitAllowed(it, visited) }
            .map { State(it, visited + it) }
    }

    protected open fun visitAllowed(pos: String, visited: List<String>): Boolean =
        pos != pos.lowercase() || pos !in visited

    override val exampleAnswer: String
        get() = "10"
}

class PartB12 : PartA12() {
    override fun visitAllowed(pos: String, visited: List<String>): Boolean {
        if (pos == "start" && pos in visited) {
            return false
        }
        if (pos != pos.lowercase()) {
            return true
        }
        val visitedLowercase = visited.filter { it == it.lowercase() }
        val sums = visitedLowercase.groupingBy { it }.eachCount()
        val visitedTwoTimes = sums.filter { it.value == 2 }.size
        if (visitedTwoTimes == 1) {
            return sums.getOrDefault(pos, 0) < 1
        }
        return visitedTwoTimes == 0
    }

    override val exampleAnswer: String
        get() = "36"
}
