package year2022

import lib.aoc.Day
import lib.aoc.Part
import kotlin.math.sign

fun main() {
    Day(13, 2022, PartA13(), PartB13()).run()
}


open class PartA13 : Part() {
    internal lateinit var packetPairs: List<Pair<List<Any>, List<Any>>>
    override fun parse(text: String) {
        val pairs = text.split("\n\n").map { it.split("\n") }
        packetPairs = pairs.map { Pair(parseList(it[0]).first, parseList(it[1]).first) }
    }

    private fun parseList(packet: String, nextId: Int = 1): Pair<List<Any>, Int> {
        val result = mutableListOf<Any>()
        var index = nextId
        var number = ""
        while (true) {
            when (val c = packet[index++]) {
                ']' -> {
                    if (number.isNotEmpty()) {
                        result.add(number.toInt())
                    }
                    return Pair(result, index)
                }

                ',' -> {
                    if (number.isNotEmpty()) {
                        result.add(number.toInt())
                    }
                    number = ""
                }

                '[' -> {
                    val (list, i) = parseList(packet, index)
                    index = i
                    result.add(list)
                }

                in '0'..'9' -> number += c
            }
        }
    }

    override fun compute(): String {
        return packetPairs.mapIndexed { i, pair ->
            if (compareLists(pair.first, pair.second) == -1) i + 1 else 0
        }.sum().toString()
    }

    internal fun compareLists(list1: List<Any>, list2: List<Any>): Int {
        val mutableList1 = list1.toMutableList()
        val mutableList2 = list2.toMutableList()
        while (true) {
            if (mutableList1.isEmpty() && mutableList2.isEmpty()) {
                return 0
            }
            if (mutableList1.isEmpty()) {
                return -1
            }
            if (mutableList2.isEmpty()) {
                return 1
            }
            val data1 = mutableList1.removeFirst()
            val data2 = mutableList2.removeFirst()
            val result = compare(data1, data2)
            if (result != 0) {
                return result
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun compare(data1: Any, data2: Any): Int {
        if (data1 is List<*> && data2 is List<*>) {
            return compareLists(data1 as List<Any>, data2 as List<Any>)
        }
        if (data1 is Int && data2 is Int) {
            return (data1 - data2).sign
        }
        var nextData1 = data1
        var nextData2 = data2
        if (nextData1 is Int) {
            nextData1 = listOf(nextData1)
        }
        if (nextData2 is Int) {
            nextData2 = listOf(nextData2)
        }
        return compareLists(nextData1 as List<Any>, nextData2 as List<Any>)
    }

    override val exampleAnswer: String
        get() = "13"
}

class PartB13 : PartA13() {
    override fun compute(): String {
        val packets = packetPairs.flatMap { it.toList() }.toMutableList()
        val sortedPackets: MutableList<Any> = mutableListOf(listOf(listOf(2)), listOf(listOf(6)))
        while (packets.isNotEmpty()) {
            val packet = packets.removeFirst()
            var inserted = false
            for (i in sortedPackets.indices) {
                @Suppress("UNCHECKED_CAST")
                if (compareLists(packet, sortedPackets[i] as List<Any>) == -1) {
                    sortedPackets.add(i, packet)
                    inserted = true
                    break
                }
            }
            if (!inserted) {
                sortedPackets.add(packet)
            }
        }

        val marker1 = sortedPackets.indexOf(listOf(listOf(2))) + 1
        val marker2 = sortedPackets.indexOf(listOf(listOf(6))) + 1
        return (marker1 * marker2).toString()
    }

    override val exampleAnswer: String
        get() = "140"
}
