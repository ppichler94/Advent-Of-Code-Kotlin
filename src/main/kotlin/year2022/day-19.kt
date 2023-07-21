package year2022

import lib.Traversal
import lib.TraversalDijkstra
import lib.aoc.Day
import lib.aoc.Part
import year2022.PartA19.Materials.Type.*
import kotlin.math.max

fun main() {
    Day(19, 2022, PartA19(), PartB19()).run()
}


open class PartA19 : Part() {
    protected data class Materials(val ore: Int = 0, val clay: Int = 0, val obsidian: Int = 0, val geode: Int = 0) {
        operator fun plus(other: Materials) =
            Materials(ore + other.ore, clay + other.clay, obsidian + other.obsidian, geode + other.geode)

        operator fun minus(other: Materials) =
            Materials(ore - other.ore, clay - other.clay, obsidian - other.obsidian, geode - other.geode)

        operator fun times(other: Int) = Materials(ore * other, clay * other, obsidian * other, geode * other)

        fun canAfford(costs: Materials): Boolean {
            return ore >= costs.ore && clay >= costs.clay && geode >= costs.geode && obsidian >= costs.obsidian
        }

        operator fun get(type: Type) = when (type) {
            ORE -> ore
            CLAY -> clay
            OBSIDIAN -> obsidian
            GEODE -> geode
        }

        fun inc(type: Type) = when (type) {
            ORE -> Materials(ore + 1, clay, obsidian, geode)
            CLAY -> Materials(ore, clay + 1, obsidian, geode)
            OBSIDIAN -> Materials(ore, clay, obsidian + 1, geode)
            GEODE -> Materials(ore, clay, obsidian, geode + 1)
        }

        enum class Type(val id: Int) {
            ORE(0), CLAY(1), OBSIDIAN(2), GEODE(3)
        }
    }

    protected data class Blueprint(val id: Int, val costs: List<Materials>) {
        data class State(val materials: Materials, val production: Materials)

        private var minutes = 0
        private lateinit var maxRobotsNeeded: Materials

        fun simulate(minutes: Int): Int {
            this.minutes = minutes
            maxRobotsNeeded = costs.reduce { acc, c -> maxm(acc, c) }

            val start = State(Materials(), Materials(1))
            val traversal = TraversalDijkstra(::nextStates)
                .startFrom(start)

            for (state in traversal) {
                if (traversal.depth == minutes - 1) {
                    val (materials, production) = state
                    return materials[GEODE] + production[GEODE]
                }
            }
            error("should not be reached")
        }

        private fun maxm(a: Materials, b: Materials): Materials {
            return Materials(max(a.ore, b.ore), max(a.clay, b.clay), max(a.obsidian, b.obsidian), max(a.geode, b.geode))
        }

        private fun nextStates(state: State, traversal: Traversal<State>) = sequence {
            val (materials, production) = state
            val minutesLeft = minutes - traversal.depth
            if (minutesLeft == 1) {
                return@sequence
            }

            val nextMaterials = materials + production
            var robotsBought = 0
            val blockedRobotsCosts = mutableListOf<Materials>()

            for (robot in Materials.Type.entries) {
                if (robot != GEODE && production[robot] >= maxRobotsNeeded[robot])
                    continue
                if (!materials.canAfford(costs[robot.id])) {
                    blockedRobotsCosts.add(costs[robot.id])
                    continue
                }
                yield(
                    Pair(
                        State(nextMaterials - costs[robot.id], production.inc(robot)),
                        if (robot == GEODE) 0f else minutesLeft.toFloat()
                    )
                )
                robotsBought++
            }
            var waitingPossible = false
            if (robotsBought > 0) {
                blockedRobotsCosts.forEach { blockedCosts ->
                    val maxProduction = materials + production * (minutesLeft - 1)
                    if (maxProduction.canAfford(blockedCosts)) {
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
                val regex =
                    """Blueprint (\d+):.*ore robot.*(\d+) ore\..*clay robot.*(\d+) ore\..*obsidian robot.*(\d+) ore and (\d+) clay\..*geode robot.*(\d+) ore and (\d+) obsidian""".toRegex()
                val match = regex.find(string)!!
                val id = match.groupValues[1].toInt()
                val oreRobotOreCost = match.groupValues[2].toInt()
                val clayRobotOreCost = match.groupValues[3].toInt()
                val obsidianRobotOreCost = match.groupValues[4].toInt()
                val obsidianRobotClayCost = match.groupValues[5].toInt()
                val geodeRobotOreCost = match.groupValues[6].toInt()
                val geodeRobotObsidianCost = match.groupValues[7].toInt()
                val costs = listOf(
                    Materials(oreRobotOreCost),
                    Materials(clayRobotOreCost),
                    Materials(obsidianRobotOreCost, obsidianRobotClayCost),
                    Materials(geodeRobotOreCost, 0, geodeRobotObsidianCost),
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
