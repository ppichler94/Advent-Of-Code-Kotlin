package lib

private class Memoize<in T, out S>(val f: (T) -> S) : (T) -> S {
    private val cache = HashMap<T, S>()

    override fun invoke(p: T): S {
        return cache.getOrPut(p) { f(p) }
    }
}

/**
 * Memoization of function results. Can only be used with pure functions (i.e. the return value only depends on the
 * arguments). The arguments of the function to be memoized must be hashable (equals and hashCode is implemented).
 */
fun <T, S> ((T) -> S).memoize(): (T) -> S = Memoize(this)