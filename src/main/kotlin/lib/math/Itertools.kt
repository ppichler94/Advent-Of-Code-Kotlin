package lib.math

fun <T, S> Iterable<T>.product(other: Iterable<S>): List<Pair<T, S>> =
    this.flatMap { outer ->
        other.map { Pair(outer, it) }
    }

fun <T> Iterable<T>.product(): List<Pair<T, T>> =
    this.flatMap { outer ->
        this.map { Pair(outer, it) }
    }

fun <T> Iterable<T>.combinations(): List<Pair<T, T>> =
    buildList {
        for (i in 0..<this@combinations.count()) {
            for (j in i + 1..<this@combinations.count()) {
                add(this@combinations.elementAt(i) to this@combinations.elementAt(j))
            }
        }
    }

object Itertools {
    fun count(start: Int = 0): Iterator<Int> =
        object : Iterator<Int> {
            private var i = 0

            override fun hasNext() = true

            override fun next(): Int = start + i++
        }
}