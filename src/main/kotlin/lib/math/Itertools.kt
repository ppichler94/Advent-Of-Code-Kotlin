package lib.math

fun <T, S> Iterable<T>.product(other: Iterable<S>): List<Pair<T, S>> {
    return this.flatMap { outer ->
        other.map { Pair(outer, it) }
    }
}

object Itertools {
    fun count(start: Int = 0): Iterator<Int> {
        return object : Iterator<Int> {
            private var i = 0

            override fun hasNext() = true

            override fun next(): Int {
                return start + i++
            }

        }
    }
}