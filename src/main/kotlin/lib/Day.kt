package lib

class Day(private val day: Int, private val year: Int, private val partA: Part?, private val partB: Part?) {
    fun run() {
        val puzzle = Puzzle(day, year)

        println("=== Part A ===")
        partA?.run(puzzle, PartName.A)
        println()

        println("=== Part B ===")
        partB?.run(puzzle, PartName.B)
        println()
    }
}