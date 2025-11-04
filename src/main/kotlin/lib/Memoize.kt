package lib

import java.io.Serializable

/**
 * Memoization of function results. Can only be used with pure functions (i.e. the return value only depends on the
 * arguments). The arguments of the function to be memoized must be hashable (equals and hashCode is implemented).
 */
fun <A, R> ((A) -> R).memoize(initialCapacity: Int = 256): (A) -> R = memoize(HashMap(initialCapacity))

fun <A, R> ((A) -> R).memoize(cache: MutableMap<A, R>): (A) -> R = { a: A ->
    cache.getOrPut(a) { this(a) }
}

fun <A, B, R> ((A, B) -> R).memoize(initialCapacity: Int = 256): (A, B) -> R = memoize(HashMap(initialCapacity))

fun <A, B, R> ((A, B) -> R).memoize(cache: MutableMap<Pair<A, B>, R>): (A, B) -> R = { a: A, b: B ->
    cache.getOrPut(a to b) { this(a, b) }
}

fun <A, B, C, D, R> ((A, B, C, D) -> R).memoize(initialCapacity: Int = 256): (A, B, C, D) -> R =
    memoize(HashMap(initialCapacity))

fun <A, B, C, D, R> ((A, B, C, D) -> R).memoize(cache: MutableMap<Tuple4<A, B, C, D>, R>): (A, B, C, D) -> R =
    { a: A, b: B, c: C, d: D ->
        cache.getOrPut(Tuple4(a, b, c, d)) { this(a, b, c, d) }
    }

data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D) : Serializable {
    override fun toString(): String = "($a, $b, $c, $d)"
}
