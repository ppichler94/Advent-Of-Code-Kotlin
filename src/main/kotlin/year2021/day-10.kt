package year2021

import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(10, 2021, PartA10(), PartB10()).run()
}

open class PartA10 : Part() {
    protected lateinit var lines: List<String>

    override fun parse(text: String) {
        lines = text.split("\n")
    }

    override fun compute(): String {
        return lines.sumOf(::pointsForLine).toString()
    }

    protected fun pointsForLine(line: String): Int {
        val pointTable = mapOf(')' to 3, ']' to 57, '}' to 1_197, '>' to 25_137)
        val stack = mutableListOf<Char>()
        for (char in line) {
            val matching = checkCharacter(char, stack)
            if (!matching) {
                return pointTable.getValue(char)
            }
        }
        return 0
    }

    protected fun checkCharacter(char: Char, stack: MutableList<Char>): Boolean {
        val matchingCharacter = mapOf('(' to ')', '[' to ']', '{' to '}', '<' to '>')
        if (char in listOf('(', '[', '{', '<')) {
            stack.add(char)
        } else {
            val lastChar = stack.removeLast()
            if (char != matchingCharacter[lastChar]) {
                return false
            }
        }
        return true
    }

    override val exampleAnswer: String
        get() = "26397"

    override val customExampleData: String?
        get() = """
            [({(<(())[]>[[{[]{<()<>>
            [(()[<>])]({[<{<<[]>>(
            {([(<{}[<>[]}>{[]{[(<()>
            (((({<>}<{<{<>}{[]{[]{}
            [[<[([]))<([[{}[[()]]]
            [{[{({}]{}}([{[{{{}}([]
            {<[[]]>}<{[{[{[]{()[[[]
            [<(<(<(<{}))><([]([]()
            <{([([[(<>()){}]>(<<{{
            <{([{{}}[<[[[<>{}]]]>[]]
        """.trimIndent()
}

class PartB10 : PartA10() {
    override fun compute(): String {
        val notCorruptLines = lines.filter { pointsForLine(it) == 0 }
        val points = notCorruptLines.map(::pointsToFinish)
        return points.median().toString()
    }

    private fun pointsToFinish(line: String): Long {
        val pointTable = mapOf('(' to 1, '[' to 2, '{' to 3, '<' to 4)
        val stack = mutableListOf<Char>()
        line.forEach { checkCharacter(it, stack) }
        stack.reverse()
        return stack.fold(0L) { acc, c -> acc * 5 + pointTable.getValue(c) }
    }

    private fun List<Long>.median() = sorted().let {
        if (it.size % 2 == 0) {
            (it[it.size / 2] + it[(it.size - 1) / 2]) / 2
        } else {
            it[it.size / 2]
        }
    }

    override val exampleAnswer: String
        get() = "288957"
}
