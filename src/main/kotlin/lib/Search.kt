package lib

import java.util.PriorityQueue

/**
 * Abstract superclass of all traversals. Its subclasses provide methods to iterate through vertices using specific
 * traversal strategies.
 */
sealed class Traversal<T : Any> : Iterable<T> {
    protected var end: T? = null
    protected var cameFrom = mutableMapOf<T, T>()
    protected lateinit var current: T

    val depth get() = getPath(current).size
    val visited get() = cameFrom.keys

    /**
     * Starts the traversal at the specified vertex
     */
    fun startFrom(start: T): Traversal<T> {
        cameFrom[start] = start
        init(listOf(start))
        return this
    }

    /**
     * Starts the traversal at the specified vertices
     */
    fun startFrom(start: Iterable<T>): Traversal<T> {
        cameFrom.putAll(start.associateWith { it })
        init(start.toList())
        return this
    }

    /**
     * For a started traversal, walk the graph until the end vertex is reached.
     *
     * @throws IllegalArgumentException if the end vertex could not be found
     */
    fun goTo(end: T): Traversal<T> {
        this.end = end
        while (hasNextNode()) {
            val current = nextNode()
            if (current == end) {
                return this
            }
        }
        throw IllegalArgumentException("End vertex could not be reached")
    }

    /**
     * Returns the path from the start of the traversal to the specified end vertex
     */
    fun getPath(to: T): List<T> {
        return buildList {
            var current = to
            while (cameFrom[current] != null && cameFrom[current] != current) {
                add(current)
                current = cameFrom.getValue(current)
            }
        }.asReversed()
    }

    /**
     * Returns the path from the start of the traversal to the end vertex of the traversal. The end vertex must have
     * been specified via [goTo]
     */
    fun getPath(): List<T> {
        val end = checkNotNull(end) { "End must be set to build a path" }
        return getPath(end)
    }

    /**
     * Walks the graph until it has been exhausted (i.e. all vertices were visited)
     */
    fun traverseAll(): Traversal<T> {
        while (hasNextNode()) {
            nextNode()
        }
        return this
    }


    /**
     * Marks vertices as already visited. This means that the specified vertices are not visited again.
     */
    fun withAlreadyVisited(alreadyVisited: Iterable<T>): Traversal<T> {
        cameFrom.putAll(alreadyVisited.associateWith { it })
        return this
    }

    protected abstract fun init(starts: List<T>)

    protected abstract fun nextNode(): T
    protected abstract fun hasNextNode(): Boolean

    /**
     * Returns the iterator of a started traversal. This allows to iterate the traversal in a for-loop and stop on a
     * specific condition (i.e. the end is unknown)
     */
    override fun iterator(): Iterator<T> {
        return object : Iterator<T> {
            override fun hasNext() = hasNextNode()
            override fun next() = nextNode()
        }
    }
}

/**
 * Traverses a graph in Breadth First order (vertices are reported when they are first "seen").
 *
 * **Algorithm:** Breadth First Search, non-recursive, using FIFO Queue
 *
 * @param neighbours This function should return the neighbours of the given vertex. If the vertex has no neighbours,
 * an empty list should be returned
 *
 */
class TraversalBreadthFirstSearch<T : Any>(private val neighbours: (T, TraversalBreadthFirstSearch<T>) -> Iterable<T>) : Traversal<T>() {
    private var toVisit = ArrayDeque<T>()

    override fun init(starts: List<T>) {
        toVisit.addAll(starts)
    }

    override fun nextNode(): T {
        current = toVisit.removeFirst()
        if (current == end) {
            return current
        }
        neighbours(current, this).forEach { next ->
            if (next !in cameFrom) {
                toVisit.add(next)
                cameFrom[next] = current
            }
        }
        return current
    }

    override fun hasNextNode() = toVisit.isNotEmpty()
}

/**
 * Traverses a graph using Dijkstra Algorithm (vertices are reported by increasing distance)
 *
 * **Algorithm:** Dijkstra, non-recursive, using PriorityQueue (heap)
 *
 * @param neighbours This function should return the neighbours of the given vertex and the cost (distance) to
 * travel to it. If the vertex has no neighbours, an empty iterable should be returned
 * @param maxCost Specifies the maximum allowed cost. If the cost to reach a vertex is higher than the maxCost, it is
 * not visited (or reported)
 *
 */
class TraversalDijkstra<T : Any>(
    private val neighbours: (T, TraversalDijkstra<T>) -> Iterable<Pair<T, Float>>,
    private val maxCost: Float = Float.MAX_VALUE,
) : Traversal<T>() {
    private val toVisit = PriorityQueue(compareBy<Pair<T, Float>> { it.second })
    private val costs = mutableMapOf<T, Float>()

    val distance get() = costs[current] ?: 0f

    override fun init(starts: List<T>) {
        toVisit.addAll(starts.associateWith { 0f }.toList())
        costs.putAll(starts.associateWith { 0f })
    }

    override fun nextNode(): T {
        val (c, _) = toVisit.remove()
        current = c
        if (current == end) {
            return current
        }
        neighbours(current, this).forEach { (next, cost) ->
            val nextCost = costs.getValue(current) + cost
            if (nextCost < maxCost && nextCost < costs.getOrDefault(next, Float.MAX_VALUE)) {
                toVisit.add(next to nextCost)
                cameFrom[next] = current
                costs[next] = nextCost
            }
        }
        return current
    }

    override fun hasNextNode(): Boolean = toVisit.isNotEmpty()
}

/**
 * Traverses a graph using A* Algorithm (vertices are reported by increasing distance)
 *
 * **Algorithm:** A*, non-recursive, using PriorityQueue (heap)
 *
 * @param neighbours This function should return the neighbours of the given vertex. If the vertex has no neighbours,
 * an empty list should be returned
 * @param neighbours This function should return the neighbours of the given vertex and the cost (distance) to
 * travel to it. If the vertex has no neighbours, an empty iterable should be returned
 * @param heuristic This function specifies the heuristic of the search (i.e. how far away is the given vertex
 * from the end vertex)
 * @param maxCost Specifies the maximum allowed cost. If the cost to reach a vertex is higher than the maxCost, it is
 * not visited (or reported)
 *
 */
class TraversalAStar<T : Any>(
    private val neighbours: (T, TraversalAStar<T>) -> Iterable<Pair<T, Float>>,
    private val heuristic: (T) -> Float,
    private val maxCost: Float = Float.MAX_VALUE,
) : Traversal<T>() {
    private val toVisit = PriorityQueue(compareBy<Pair<T, Float>> { it.second })
    private val costs = mutableMapOf<T, Float>()

    override fun init(starts: List<T>) {
        toVisit.addAll(starts.associateWith { 0f }.toList())
        costs.putAll(starts.associateWith { 0f })
    }

    override fun nextNode(): T {
        val (c, _) = toVisit.remove()
        current = c
        if (current == end) {
            return current
        }
        neighbours(current, this).forEach { (next, cost) ->
            val nextCost = costs.getValue(current) + cost
            if (nextCost < maxCost && nextCost < costs.getOrDefault(next, Float.MAX_VALUE)) {
                toVisit.add(next to nextCost + heuristic(current))
                cameFrom[next] = current
                costs[next] = nextCost
            }
        }
        return current
    }

    override fun hasNextNode(): Boolean = toVisit.isNotEmpty()
}
