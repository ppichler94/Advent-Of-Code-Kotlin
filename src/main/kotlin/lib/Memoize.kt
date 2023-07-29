package lib

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