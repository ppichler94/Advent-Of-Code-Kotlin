package lib

class Day(private val day: Int, private val year: Int, private val partA: Part?, private val partB: Part?) {
    fun run() {
        val puzzle = Puzzle(day, year)
        partA?.run(puzzle)
        partB?.run(puzzle)
    }
}