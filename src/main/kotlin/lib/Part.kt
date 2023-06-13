package lib

enum class PartName {
    A, B
}

abstract class Part(private val partName: PartName) {
    abstract fun parse(text: String)
    abstract fun compute(): String

    fun run(puzzle: Puzzle) {
        val testSuccessful = runTest(puzzle.exampleData)
        if (testSuccessful) {
            val solution = runPuzzle(puzzle.inputData)
            puzzle.submit(partName, solution)
        }
    }

    private fun runTest(inputData: String): Boolean {
        parse(inputData)
        val solution = compute()
        return solution == exampleAnswer
    }

    private fun runPuzzle(inputData: String): String {
        parse(inputData)
        return compute()
    }

    val customExampleData: String
        get() = ""

    abstract val exampleAnswer: String
}