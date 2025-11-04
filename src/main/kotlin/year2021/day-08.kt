package year2021

import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(8, 2021, PartA8(), PartB8()).run()
}

open class PartA8 : Part() {
    data class Entry(val signal: List<String>, val output: List<String>) {
        companion object {
            fun parse(line: String): Entry {
                val (signalText, outputText) = line.split(" | ")
                return Entry(signalText.split(" "), outputText.split(" "))
            }
        }
    }

    protected lateinit var entries: List<Entry>

    override fun parse(text: String) {
        entries = text.split("\n").map(Entry::parse)
    }

    override fun compute(): String {
        return entries.sumOf { entry ->
            entry.output.count { it.length in listOf(2, 3, 4, 7) }
        }.toString()
    }

    override val exampleAnswer: String
        get() = "26"

    override val customExampleData: String?
        get() = """
            be cfbegad cbdgef fgaecd cgeb fdcge agebfd fecdb fabcd edb | fdgacbe cefdb cefbgd gcbe
            edbfga begcd cbg gc gcadebf fbgde acbgfd abcde gfcbed gfec | fcgedb cgb dgebacf gc
            fgaebd cg bdaec gdafb agbcfd gdcbef bgcad gfac gcb cdgabef | cg cg fdcagb cbg
            fbegcd cbd adcefb dageb afcb bc aefdc ecdab fgdeca fcdbega | efabcd cedba gadfec cb
            aecbfdg fbg gf bafeg dbefa fcge gcbea fcaegb dgceab fcbdga | gecf egdcabf bgf bfgea
            fgeab ca afcebg bdacfeg cfaedg gcfdb baec bfadeg bafgc acf | gebdcfa ecba ca fadegcb
            dbcfg fgd bdegcaf fgec aegbdf ecdfab fbedc dacgb gdcebf gf | cefg dcbef fcge gbcadfe
            bdfegc cbegaf gecbf dfcage bdacg ed bedf ced adcbefg gebcd | ed bcgafe cdgba cbgef
            egadfb cdbfeg cegd fecab cgb gbdefca cg fgcdab egfdb bfceg | gbdfcae bgc cg cgb
            gcafb gcf dcaebfg ecagb gf abcdeg gaef cafbge fdbac fegbdc | fgae cfgab fg bagce
        """.trimIndent()
}

class PartB8 : PartA8() {
    override fun compute(): String {
        return entries.sumOf { entry ->
            val (signal, output) = entry
            val sortedSignal = signal.map { it.toCharArray().sorted().joinToString("") }
            val sortedOutput = output.map { it.toCharArray().sorted().joinToString("") }
            val tmp = mutableMapOf<String, String>()

            findUniques(sortedSignal, tmp)
            find6(sortedSignal, tmp)
            find0(sortedSignal, tmp)
            find9(sortedSignal, tmp)
            find5(sortedSignal, tmp)
            find3(sortedSignal, tmp)
            find2(sortedSignal, tmp)

            val code = tmp.map { (k, v) -> v to k }.associate { it }
            sortedOutput.map { code[it] }.joinToString("").toInt()
        }.toString()
    }

    private fun findUniques(signal: List<String>, tmp: MutableMap<String, String>) {
        val lengthMap = mapOf(2 to "1", 3 to "7", 4 to "4", 7 to "8")
        for (word in signal) {
            if (word.length in lengthMap) {
                tmp[lengthMap.getValue(word.length)] = word
            }
        }
    }

    private fun find6(signal: List<String>, tmp: MutableMap<String, String>) {
        for (word in signal) {
            if (word.length == 6 && tmp.getValue("1").any { it !in word }) {
                tmp["6"] = word
                break
            }
        }
    }

    private fun find0(signal: List<String>, tmp: MutableMap<String, String>) {
        for (word in signal) {
            if (word.length == 6 && tmp.getValue("4").any { it !in word } && word !in tmp.values) {
                tmp["0"] = word
                break
            }
        }
    }

    private fun find9(signal: List<String>, tmp: MutableMap<String, String>) {
        for (word in signal) {
            if (word.length == 6 && word !in tmp.values) {
                tmp["9"] = word
                break
            }
        }
    }

    private fun find5(signal: List<String>, tmp: MutableMap<String, String>) {
        for (word in signal) {
            if (word.length == 5 && word.all { it in tmp.getValue("6") }) {
                tmp["5"] = word
                break
            }
        }
    }

    private fun find3(signal: List<String>, tmp: MutableMap<String, String>) {
        for (word in signal) {
            if (word.length == 5 && word.all { it in tmp.getValue("9") } && word !in tmp.values) {
                tmp["3"] = word
                break
            }
        }
    }

    private fun find2(signal: List<String>, tmp: MutableMap<String, String>) {
        for (word in signal) {
            if (word.length == 5 && word !in tmp.values) {
                tmp["2"] = word
                break
            }
        }
    }

    override val exampleAnswer: String
        get() = "61229"
}
