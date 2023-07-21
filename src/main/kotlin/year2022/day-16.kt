package year2022

import lib.TraversalBreadthFirstSearch
import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(16, 2022, PartA16(), PartB16()).run()
}


open class PartA16 : Part() {
    internal data class State(val current: String, val minutes: Int, val opened: Set<Valve>)
    internal data class Valve(val name: String, val flowRate: Int, val links: List<String>)

    private lateinit var valves: Map<String, Valve>
    private lateinit var distances: Map<String, Map<String, Int>>
    private lateinit var remainingValves: Set<Valve>
    private var maxFlowRate: Int = 0

    override fun parse(text: String) {
        val regex = """Valve (.*) has flow rate=(\d+); tunnels? leads? to valves? (.*)""".toRegex()
        valves = text.split("\n")
            .map {
                val (name, flowRate, links) = regex.find(it)!!.destructured
                Valve(name, flowRate.toInt(), links.split(", "))
            }.associateBy { it.name }
        maxFlowRate = valves.values.sumOf(Valve::flowRate)

        val significantValves = valves.values.filter { it.name == "AA" || it.flowRate > 0 }
        distances = significantValves.associate { from ->
            val traversal = TraversalBreadthFirstSearch { n, _ -> valveNeighbours(n) }
                .startFrom(from.name)
                .traverseAll()
            from.name to significantValves.filter { it != from && it.name != "AA" }
                .associate { to -> to.name to traversal.getPath(to.name).size }
        }
        remainingValves = significantValves.filter { it.name != "AA" }.toSet()
    }

    private fun valveNeighbours(node: String) = valves[node]!!.links


    override fun compute(): String {
        return traverse(timeLeft = 30).toString()
    }

    internal fun traverse(
        timeLeft: Int,
        current: Valve = valves.getValue("AA"),
        remaining: Set<Valve> = remainingValves,
        cache: MutableMap<State, Int> = mutableMapOf(),
        withElephant: Boolean = false
    ): Int {
        val currentScore = timeLeft * current.flowRate
        val currentState = State(current.name, timeLeft, remaining)

        return currentScore + cache.getOrPut(currentState) {
            val maxCurrent = remaining
                .filter { distances[current.name]!![it.name]!! < timeLeft }
                .takeIf { it.isNotEmpty() }
                ?.maxOf {
                    val remainingMinutes = timeLeft - 1 - distances[current.name]!![it.name]!!
                    traverse(remainingMinutes, it, remaining - it, cache, withElephant)
                } ?: 0
            maxOf(maxCurrent, if (withElephant) traverse(timeLeft = 26, remaining = remaining) else 0)
        }
    }

    override val exampleAnswer: String
        get() = "1651"
}

class PartB16 : PartA16() {
    override fun compute(): String {
        return traverse(timeLeft = 26, withElephant = true).toString()
    }

    override val exampleAnswer: String
        get() = "1707"
}