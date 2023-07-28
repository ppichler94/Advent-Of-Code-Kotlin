package year2021

import lib.Position
import lib.aoc.Day
import lib.aoc.Part

fun main() {
    Day(13, 2021, PartA13(), PartB13()).run()
}

open class PartA13 : Part() {
    data class Paper(var points: List<Position>) {
        var width = points.maxOf { it[0] } + 1
        var height = points.maxOf { it[1] } + 1
    }

    sealed class Command {
        abstract fun fold(paper: Paper)
    }

    class FoldHorizontal(val y: Int) : Command() {
        override fun fold(paper: Paper) {
            paper.points = paper.points.map {
                if (it[1] > y) {
                    val newY = y - (it[1] - y)
                    Position.at(it[0], newY)
                } else {
                    it
                }
            }.distinct()
            paper.height = y
        }
    }

    class FoldVertical(val x: Int) : Command() {
        override fun fold(paper: Paper) {
            paper.points = paper.points.map {
                if (it[0] > x) {
                    val newX = x - (it[0] - x)
                    Position.at(newX, it[1])
                } else {
                    it
                }
            }.distinct()
            paper.width = x
        }
    }

    private fun Command(line: String): Command {
        val regex = """.*([yx])=(\d+)""".toRegex()
        val matches = regex.find(line)!!
        return when (matches.groupValues[1]) {
            "x" -> FoldVertical(matches.groupValues[2].toInt())
            "y" -> FoldHorizontal(matches.groupValues[2].toInt())
            else -> throw IllegalArgumentException("unknown fold ${matches.groupValues[1]}")
        }
    }

    private lateinit var commands: List<Command>
    protected lateinit var paper: Paper

    override fun parse(text: String) {
        val (pointsText, commandsText) = text.split("\n\n")
        commands = commandsText.split("\n").map(::Command)

        val points = pointsText.split("\n").map { it.split(",") }.map { Position.at(it[0].toInt(), it[1].toInt()) }

        paper = Paper(points)
    }

    override fun compute(): String {
        commands = commands.take(1)
        doFolds()
        return paper.points.size.toString()
    }

    protected fun doFolds() {
        commands.forEach { it.fold(paper) }
    }

    override val exampleAnswer: String
        get() = "17"

    override val customExampleData: String?
        get() = """
            6,10
            0,14
            9,10
            0,3
            10,4
            4,11
            6,0
            6,12
            4,1
            0,13
            10,12
            3,4
            3,0
            8,4
            1,10
            2,14
            8,10
            9,0

            fold along y=7
            fold along x=5
        """.trimIndent()
}

class PartB13 : PartA13() {
    override fun compute(): String {
        doFolds()
        println()
        for (y in 0..<paper.height) {
            for (x in 0..<paper.width) {
                print(if (Position.at(x, y) !in paper.points) " " else "#")
            }
            println()
        }
        return ""
    }

    override val exampleAnswer: String
        get() = ""
}
