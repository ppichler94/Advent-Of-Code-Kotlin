package lib

import java.util.PriorityQueue

object Search {
    data class Result<T>(val cameFrom: Map<T, T>, val cost: Map<T, Float>) {
        fun getPath(to: T): List<T> {
            return buildList {
                var current = to
                while (cameFrom[current] != null && cameFrom[current] != current) {
                    add(current)
                    current = cameFrom.getValue(current)
                }
            }.asReversed()
        }
    }

    fun <T> bfs(neighboursFn: (T) -> List<T>, start: List<T>, goal: T?): Result<T> {
        val toVisit = ArrayDeque<T>()
        toVisit.addAll(start)
        val cameFrom = start.associateWith { it }.toMutableMap()
        while (toVisit.isNotEmpty()) {
            val current = toVisit.removeFirst()
            if (current == goal) {
                break
            }
            neighboursFn(current).forEach { next ->
                if (next !in cameFrom) {
                    toVisit.add(next)
                    cameFrom[next] = current
                }
            }
        }
        return Result(cameFrom, mapOf())
    }

    fun <T> dijkstra(neighboursFn: (T) -> List<T>, costFn: (T, T) -> Float, start: T, goal: T, maxCost: Float = Float.MAX_VALUE): Result<T> {
        val toVisit = PriorityQueue(compareBy<Pair<T, Float>> { it.second })
        toVisit.add(start to 0f)
        val cameFrom = mutableMapOf(start to start)
        val costs = mutableMapOf(start to 0f)
        while (toVisit.isNotEmpty()) {
            val (current, _) = toVisit.remove()
            if (current == goal) {
                break
            }
            neighboursFn(current).forEach { next ->
                val nextCost = costs.getValue(current) + costFn(current, next)
                if (nextCost < maxCost && nextCost < costs.getOrDefault(next, Float.MAX_VALUE)) {
                    toVisit.add(next to nextCost)
                    cameFrom[next] = current
                    costs[next] = nextCost
                }
            }
        }
        return Result(cameFrom, costs)
    }

    fun <T> dijkstra(neighboursFn: (T) -> List<T>, costFn: (T, T) -> Float, start: T, goalFn: (T) -> Boolean, maxCost: Float = Float.MAX_VALUE): Pair<T, Result<T>> {
        val toVisit = PriorityQueue(compareBy<Pair<T, Float>> { it.second })
        toVisit.add(start to 0f)
        val cameFrom = mutableMapOf(start to start)
        val costs = mutableMapOf(start to 0f)
        while (toVisit.isNotEmpty()) {
            val (current, _) = toVisit.remove()
            if (goalFn(current)) {
                return current to Result(cameFrom, costs)
            }
            neighboursFn(current).forEach { next ->
                val nextCost = costs.getValue(current) + costFn(current, next)
                if (nextCost < maxCost && nextCost < costs.getOrDefault(next, Float.MAX_VALUE)) {
                    toVisit.add(next to nextCost)
                    cameFrom[next] = current
                    costs[next] = nextCost
                }
            }
        }
        return start to Result(cameFrom, costs)
    }

    fun <T> aStar(neighboursFn: (T) -> List<T>, costFn: (T, T) -> Float, start: T, goalFn: (T) -> Boolean, heuristic: (T) -> Float, maxCost: Float = Float.MAX_VALUE): Pair<T, Result<T>> {
        val toVisit = PriorityQueue(compareBy<Pair<T, Float>> { it.second })
        toVisit.add(start to 0f)
        val cameFrom = mutableMapOf(start to start)
        val costs = mutableMapOf(start to 0f)
        while (toVisit.isNotEmpty()) {
            val (current, _) = toVisit.remove()
            if (goalFn(current)) {
                return current to Result(cameFrom, costs)
            }
            neighboursFn(current).forEach { next ->
                val nextCost = costs.getValue(current) + costFn(current, next)
                if (nextCost < maxCost && nextCost < costs.getOrDefault(next, Float.MAX_VALUE)) {
                    toVisit.add(next to nextCost + heuristic(current))
                    cameFrom[next] = current
                    costs[next] = nextCost
                }
            }
        }
        return start to Result(cameFrom, costs)
    }
}