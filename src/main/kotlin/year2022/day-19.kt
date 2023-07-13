package year2022

import lib.Day
import lib.Part
import lib.Traversal
import lib.TraversalDijkstra

fun main() {
    Day(19, 2022, PartA19(), PartB19()).run()
}


open class PartA19 : Part() {
    protected data class Blueprint(val id: Int, val costs: List<List<Int>>) {
        data class State(val materials: List<Int>, val production: List<Int>)

        private var minutes = 0
        private lateinit var maxRobotsNeeded: List<Int>

        fun simulate(minutes: Int): Int {
            this.minutes = minutes
            maxRobotsNeeded = (0..3).map { robot -> (0..3).maxOf { costs[it][robot]} }

            val start = State(listOf(0, 0, 0, 0), listOf(1, 0, 0, 0))
            val traversal = TraversalDijkstra(::nextStates)
                .startFrom(start)

            for (state in traversal) {
                if (traversal.depth == minutes - 1) {
                    val (materials, production) = state
                    return materials.last() + production.last()
                }
            }
            error("should not be reached")
        }

        private fun nextStates(state: State, traversal: Traversal<State>) = sequence {
            val (materials, production) = state
            val minutesLeft = minutes - traversal.depth
            if (minutesLeft == 1) {
                return@sequence
            }

            val nextMaterials = (materials zip production).map { it.first + it.second }
            var robotsBought = 0
            val blockedRobotsCosts = mutableListOf<List<Int>>()

            for (robot in 0..3) {
                if (robot < 3 && production[robot] >= maxRobotsNeeded[robot])
                    continue
                if ((costs[robot].zip(materials)).any { it.first > it.second }) {
                    blockedRobotsCosts.add(costs[robot])
                    continue
                }
                yield(Pair(State(
                    (nextMaterials zip costs[robot]).map { it.first - it.second },
                    production.mapIndexed { i, x -> if (i == robot) x + 1 else x }
                ), if (robot == 3) 0f else minutesLeft.toFloat()))
                robotsBought++
            }
            var waitingPossible = false
            if (robotsBought > 0) {
                blockedRobotsCosts.forEach { blockedCosts ->
                    val maxProduction = (materials zip production).map { (m, p) -> m + (minutesLeft - 1) * p }
                    if ((maxProduction zip blockedCosts).all { (p, cost) -> p >= cost }) {
                        waitingPossible = true
                        return@forEach
                    }
                }
                if (!waitingPossible) {
                    return@sequence
                }
            }
            yield(Pair(State(nextMaterials, production), minutesLeft.toFloat()))
        }.asIterable()

        companion object {
            fun fromString(string: String): Blueprint {
                val regex = """Blueprint (\d+):.*ore robot.*(\d+) ore\..*clay robot.*(\d+) ore\..*obsidian robot.*(\d+) ore and (\d+) clay\..*geode robot.*(\d+) ore and (\d+) obsidian""".toRegex()
                val match = regex.find(string)!!
                val id = match.groupValues[1].toInt()
                val oreRobotOreCost = match.groupValues[2].toInt()
                val clayRobotOreCost = match.groupValues[3].toInt()
                val obsidianRobotOreCost = match.groupValues[4].toInt()
                val obsidianRobotClayCost = match.groupValues[5].toInt()
                val geodeRobotOreCost = match.groupValues[6].toInt()
                val geodeRobotObsidianCost = match.groupValues[7].toInt()
                val costs = listOf(
                    listOf(oreRobotOreCost, 0, 0, 0),
                    listOf(clayRobotOreCost, 0, 0, 0),
                    listOf(obsidianRobotOreCost, obsidianRobotClayCost, 0, 0),
                    listOf(geodeRobotOreCost, 0, geodeRobotObsidianCost, 0),
                )

                return Blueprint(id, costs)
            }
        }
    }

    protected lateinit var blueprints: List<Blueprint>

    override fun parse(text: String) {
        blueprints = text.split("\n").map(Blueprint::fromString)
    }

    override fun compute(): String {
        val minutesToSimulate = 24
        val results = blueprints.map { it.simulate(minutesToSimulate) * it.id }
        return results.sum().toString()
    }

    override val exampleAnswer: String
        get() = "33"

    override val customExampleData: String?
        get() = """
            Blueprint 1: Each ore robot costs 4 ore. Each clay robot costs 2 ore. Each obsidian robot costs 3 ore and 14 clay. Each geode robot costs 2 ore and 7 obsidian.
            Blueprint 2: Each ore robot costs 2 ore. Each clay robot costs 3 ore. Each obsidian robot costs 3 ore and 8 clay. Each geode robot costs 3 ore and 12 obsidian.
        """.trimIndent()
}

class PartB19 : PartA19() {
    override fun compute(): String {
        val minutesToSimulate = 32
        val results = blueprints.take(3).map { it.simulate(minutesToSimulate) }
        return results.reduce(Int::times).toString()
    }
    override val exampleAnswer: String
        get() = "3472"
}
