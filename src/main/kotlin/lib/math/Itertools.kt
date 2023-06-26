package lib.math

fun <T, S> Iterable<T>.product(other: Iterable<S>): List<Pair<T, S>> {
    return this.flatMap { outer ->
        other.map { Pair(outer, it) }
    }
}