package year2021

import lib.aoc.Day
import lib.aoc.Part
import org.jetbrains.kotlinx.multik.api.linalg.dot
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.ones
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.filterMultiIndexed
import org.jetbrains.kotlinx.multik.ndarray.operations.indexOf
import org.jetbrains.kotlinx.multik.ndarray.operations.sum

fun main() {
    Day(4, 2021, PartA4(), PartB4()).run()
}

open class PartA4 : Part() {
    protected lateinit var numbers: List<Int>
    protected lateinit var boards: List<D2Array<Int>>
    protected lateinit var markedBoards: List<D2Array<Int>>

    override fun parse(text: String) {
        val blocks = text.split("\n\n")
        val numbersText = blocks.first()
        val boardsText = blocks.drop(1)
        numbers = numbersText.split(",").map(String::toInt)
        boards = boardsText.map(::parseBoard)
        markedBoards = boards.indices.map { mk.zeros(5, 5) }
    }

    private fun parseBoard(text: String): D2Array<Int> {
        val pattern = """\d+""".toRegex()
        return mk.ndarray(pattern.findAll(text).map { it.value.toInt() }.toList(), 5, 5)
    }

    override fun compute(): String {
        for (number in numbers) {
            for ((board, markedBoard) in boards zip markedBoards) {
                val index = board.indexOf(number)
                if (index < 0) {
                    continue
                }
                val rowIndex = index / 5
                val columnIndex = index % 5
                markedBoard[rowIndex, columnIndex] = 1
                if (isWinningBoard(markedBoard, rowIndex, columnIndex)) {
                    return calculatePoints(board, markedBoard, number).toString()
                }
            }
        }
        return "0"
    }

    protected fun isWinningBoard(markedBoard: D2Array<Int>, rowIndex: Int, columnIndex: Int): Boolean {
        val columnSum = mk.linalg.dot(markedBoard.transpose(), mk.ones<Int>(5))
        val rowSum = mk.linalg.dot(markedBoard, mk.ones<Int>(5))
        return columnSum[columnIndex] == 5 || rowSum[rowIndex] == 5
    }

    protected fun calculatePoints(board: D2Array<Int>, markedBoard: D2Array<Int>, number: Int): Int {
        return board.filterMultiIndexed { (row, col), _ -> markedBoard[row, col] == 0 }
            .sum() * number
    }

    override val exampleAnswer: String
        get() = "4512"
}

class PartB4 : PartA4() {
    override fun compute(): String {
        var lastWinningBoard = mk.zeros<Int>(5, 5)
        var lastWinningMarks = mk.zeros<Int>(5, 5)
        var lastNumber = 0
        val winningIndices = mutableListOf<Int>()

        for (number in numbers) {
            (boards zip markedBoards).forEachIndexed { i, (board, markedBoard) ->
                val index = board.indexOf(number)
                if (index < 0 || i in winningIndices) {
                    return@forEachIndexed
                }
                val rowIndex = index / 5
                val columnIndex = index % 5
                markedBoard[rowIndex, columnIndex] = 1
                if (isWinningBoard(markedBoard, rowIndex, columnIndex)) {
                    winningIndices.add(i)
                    lastWinningBoard = board
                    lastWinningMarks = markedBoard
                    lastNumber = number
                }
            }
        }
        return calculatePoints(lastWinningBoard, lastWinningMarks, lastNumber).toString()
    }

    override val exampleAnswer: String
        get() = "1924"
}
