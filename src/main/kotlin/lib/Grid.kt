package lib

import lib.math.Vector
import lib.math.minus
import lib.math.plus
import lib.math.product
import kotlin.math.abs

sealed class BaseGrid2d<T>(
    private val content: Iterable<Iterable<T>>,
) {
    /**
     * size of each dimension of the grid in the following order x, y
     */
    val size: List<Int> get() = listOf(content.first().count(), content.count())

    /**
     * List of intervals defining the limits of each dimension in the order x, y
     */
    val limits: List<Iterable<Int>> get() = size.map { 0 until it }

    operator fun get(position: Position): T {
        check(position.size == 2) { "Position must be of size 2" }
        return content.elementAt(position[1]).elementAt(position[0])
    }

    /**
     * Find content in the grid and return found positions.
     * @param elements Content to be searched
     */
    fun findAll(elements: Iterable<T>) =
        buildList {
            content.forEachIndexed { y, innerIterable ->
                innerIterable.forEachIndexed { x, element ->
                    if (element in elements) {
                        add(Position.at(x, y))
                    }
                }
            }
        }

    /**
     * Find content in the grid matching the given predicate and return found positions.
     * @param predicate Function that takes the element and returns the result of predicate evaluation on the element
     */
    fun findAll(predicate: (T) -> Boolean) =
        buildList {
            content.forEachIndexed { y, innerIterable ->
                innerIterable.forEachIndexed { x, element ->
                    if (predicate(element)) {
                        add(Position.at(x, y))
                    }
                }
            }
        }
}

data class Grid2d<T>(
    val content: Iterable<Iterable<T>>,
) : BaseGrid2d<T>(content) {
    companion object {
        /**
         * Creates a grid of type Char where each line is a row of the grid.
         */
        fun ofLines(lines: List<String>): Grid2d<Char> = Grid2d(lines.map { it.toList() })
    }
}

data class MutableGrid2d<T>(
    val content: MutableList<MutableList<T>>,
) : BaseGrid2d<T>(content) {
    operator fun set(
        position: Position,
        value: T,
    ) {
        check(position.size == 2) { "Position must be of size 2" }
        content[position[1]][position[0]] = value
    }
}

/**
 * A position in an n-dimensional array
 */
data class Position(
    val position: Vector,
) {
    companion object {
        fun at(vararg coordinates: Int): Position = Position(Vector(coordinates))

        /**
         * Generate a list of moves to neighbor positions in an n-dimensional grid.
         * @param dimensions Number of dimensions of the generated move vectors
         * @param diagonals Add diagonal moves (more than one non-zero coordinate)
         * @param zeroMove Add the zero move (all coordinates zero)
         */
        fun moves(
            dimensions: Int = 2,
            diagonals: Boolean = false,
            zeroMove: Boolean = false,
        ) = when (dimensions) {
            2 -> {
                (-1..1)
                    .product(-1..1)
                    .map { Vector.at(it.first, it.second) }
                    .filter {
                        (zeroMove || (it.x != 0 || it.y != 0)) &&
                            (diagonals || (abs(it.x) + abs(it.y) <= 1))
                    }
            }

            else -> throw IllegalArgumentException("Dimensions $dimensions not supported")
        }
    }

    val size: Int = position.size

    operator fun get(index: Int) = position[index]

    operator fun plus(vector: Vector): Position = Position((position + vector))

    operator fun minus(vector: Vector): Position = Position((position - vector))

    /**
     * Iterate the positions that are reached by performing the given moves. Can be limited or wrapped by optional
     * limits
     * @param moves Define what moves lead to neighbors
     * @param limits If given, the generated positions lie within the limits
     * @param wrap If true, positions outside the limits are wrapped. If false, such positions are omitted.
     */
    fun neighbours(
        moves: Iterable<Vector>,
        limits: List<Iterable<Int>>? = null,
        wrap: Boolean = false,
    ) = sequence<Position> {
        moves.forEach {
            val neighbour = this@Position + it
            if (limits == null) {
                check(!wrap) { "Limits required for wrapping" }
                yield(neighbour)
            } else if (wrap) {
                yield(neighbour.wrapToLimits(limits))
            } else {
                if (neighbour.isInLimits(limits)) {
                    yield(neighbour)
                }
            }
        }
    }.asIterable()

    /**
     * If a coordinate of the positions is outside the given limits, the coordinate is wrapped at the border
     * (i.e. add or subtract the size (to - from + 1) as often as necessary such that the coordinate lies within the
     * limits)
     */
    fun wrapToLimits(limits: List<Iterable<Int>>): Position {
        check(limits.size == this.size) { "Size of limits must match size of Position" }
        val data =
            (position.data zip limits).map { (coordinate, limit) ->
                if (coordinate < limit.first()) {
                    val window = limit.last() - limit.first() + 1
                    limit.last() - (limit.last() - coordinate).mod(window)
                } else if (coordinate > limit.last()) {
                    val window = limit.last() - limit.first() + 1
                    (coordinate - limit.first()).mod(window) + limit.first()
                } else {
                    coordinate
                }
            }
        return Position(Vector(data))
    }

    fun isInLimits(limits: List<Iterable<Int>>): Boolean {
        check(limits.size == this.size) { "Size of limits must match size of Position" }
        return (position.data zip limits).all { (coordinate, limit) -> coordinate in limit }
    }

    /**
     * Manhattan distance to the target position (|x1 - x2| + |y1 - y2|)
     */
    fun manhattanDistance(target: Position): Int = (this - target.position).position.data.sumOf { abs(it) }

    fun squaredDistance(target: Position): Int = (this - target.position).position.data.sumOf { it * it }
}
