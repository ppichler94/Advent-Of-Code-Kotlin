package year2022

import lib.aoc.Day
import lib.aoc.Part
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.any
import org.jetbrains.kotlinx.multik.ndarray.operations.append
import org.jetbrains.kotlinx.multik.ndarray.operations.plus
import kotlin.math.max

fun main() {
    Day(17, 2022, PartA17(), PartB17()).run()
}


open class PartA17 : Part() {
    private lateinit var jets: List<Int>
    private lateinit var jetsIterator: Cycle<Int>
    internal lateinit var cave: D2Array<Int>
    internal var height = 0

    private val rocks = listOf(
        mk.ndarray(mk[mk[1, 1, 1, 1]]),
        mk.ndarray(
            mk[
                mk[0, 1, 0],
                mk[1, 1, 1],
                mk[0, 1, 0]
            ]
        ),
        mk.ndarray(
            mk[
                mk[1, 1, 1],
                mk[0, 0, 1],
                mk[0, 0, 1]
            ]
        ),
        mk.ndarray(
            mk[
                mk[1],
                mk[1],
                mk[1],
                mk[1]
            ]
        ),
        mk.ndarray(
            mk[
                mk[1, 1],
                mk[1, 1]
            ]
        ),
    )
    internal lateinit var rocksIterator: Cycle<D2Array<Int>>

    internal class Cycle<T>(private val iterable: Iterable<T>) : Iterator<T> {
        private var i = 0
        private val modulo = iterable.count()

        override fun hasNext() = true
        override fun next(): T {
            return iterable.elementAt(i++ % modulo)
        }

        fun nextIndexed(): Pair<T, Int> {
            val nextIndex = i++ % modulo
            return Pair(iterable.elementAt(nextIndex), nextIndex)
        }
    }

    override fun parse(text: String) {
        jets = text.map { if (it == '<') -1 else 1 }
        jetsIterator = Cycle(jets)
        rocksIterator = Cycle(rocks)
    }

    override fun compute(): String {
        height = 0
        cave = mk.zeros(512, 7)
        repeat(2022) {
            drop(rocksIterator.next())
        }
        return height.toString()
    }

    internal fun drop(rock: D2Array<Int>): Int {
        var x = 2
        var y = height + 3
        while (true) {
            if (y + rock.shape[0] + 1 >= cave.shape[0]) {
                cave = cave.append(mk.zeros(512, 7), 0)
            }
            val (dx, jetIndex) = jetsIterator.nextIndexed()
            if (!intersects(rock, x + dx, y)) {
                x += dx
            }
            if (intersects(rock, x, y - 1)) {
                addRock(rock, x, y)
                height = max(height, y + rock.shape[0])
                return jetIndex + 1
            }
            y--
        }
    }

    private fun intersects(rock: D2Array<Int>, x: Int, y: Int): Boolean {
        if (x < 0 || x + rock.shape[1] > 7) {
            return true
        }
        if (y < 0) {
            return true
        }
        return (cave[y until y + rock.shape[0], x until x + rock.shape[1]] + rock).any { it > 1 }

    }

    private fun addRock(rock: D2Array<Int>, x: Int, y: Int) {
        (0 until rock.shape[0]).forEach { dy ->
            (0 until rock.shape[1]).forEach { dx ->
                cave[y + dy, x + dx] += rock[dy, dx]
            }
        }
    }

    override val exampleAnswer: String
        get() = "3068"

    override val customExampleData: String?
        get() = ">>><<><>><<<>><>>><<<>>><<<><<<>><>><<>>"
}

class PartB17 : PartA17() {
    override fun compute(): String {
        height = 0
        cave = mk.zeros(512, 7)
        var roundNumber = 1
        var completeRounds = 0
        val jetsSeen = mutableSetOf<Int>()
        var heightPrefix = 0
        var roundPrefix = 0

        while (true) {
            val rock = rocksIterator.next()
            val jetIndex = drop(rock)
            if (roundNumber % 5 == 0) {
                if (jetIndex in jetsSeen) {
                    jetsSeen.clear()
                    jetsSeen.add(jetIndex)
                    completeRounds++
                    if (completeRounds == 1) {
                        heightPrefix = height
                        roundPrefix = roundNumber
                    } else if (completeRounds == 2) {
                        break
                    }
                } else if (completeRounds == 0) {
                    jetsSeen.add(jetIndex)
                }
            }
            roundNumber++
        }

        val heightModulo = height - heightPrefix
        val roundModulo = roundNumber - roundPrefix

        val rounds = 1_000_000_000_000
        val roundsCompleted = roundNumber

        val cycleModulo = (rounds - roundsCompleted) / roundModulo
        val cycleRounds = cycleModulo * roundModulo
        val cycleHeight = cycleModulo * heightModulo

        val roundsAfterCycle = rounds - roundsCompleted - cycleRounds

        repeat(roundsAfterCycle.toInt()) {
            drop(rocksIterator.next())
        }
        return (height + cycleHeight).toString()
    }

    override val exampleAnswer: String
        get() = "1514285714288"
}
