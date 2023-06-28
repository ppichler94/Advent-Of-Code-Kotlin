package lib

import kotlin.system.measureTimeMillis

enum class PartName(val value: Int) {
    A(1), B(2)
}

data class TestCase(val name: String, val input: String, val expected: String)

abstract class Part() {
    abstract fun parse(text: String)
    abstract fun compute(): String

    fun run(puzzle: Puzzle, partName: PartName) {
        val testSuccessful = runTests(puzzle.exampleData)
        if (testSuccessful) {
            val solution = runPuzzle(puzzle.inputData)
            puzzle.submit(partName, solution)
        }
    }

    private fun runTests(exampleInput: String): Boolean {
        println("Running tests...")
        val exampleData = customExampleData ?: exampleInput
        val exampleSuccessful = runTest(TestCase("Example", exampleData, exampleAnswer))
        val testsSuccessful = testCases.all { runTest(it) }
        return exampleSuccessful and testsSuccessful
    }

    private fun runTest(testCase: TestCase): Boolean {
        print("  ")
        parse(testCase.input)
        var solution: String
        val runtime = measureTimeMillis {
            solution = compute()
        }
        val successful = testCase.expected.isEmpty() || solution == testCase.expected
        print(Util.colored(">> ", if (successful) Util.Color.GREEN else Util.Color.RED))
        print("TestCase '${testCase.name}' ")
        if (successful) {
            print(Util.colored("OK", Util.Color.GREEN))
        } else {
            print(Util.colored("failed", Util.Color.RED))
            print(" Expected: ${testCase.expected}")
        }
        val runtimeString = "%.2f".format(runtime.toDouble() / 1000)
        print(" Result: $solution Runtime: ${runtimeString}s\n")
        return successful
    }

    private fun runPuzzle(inputData: String): String {
        println("Running puzzle...")
        parse(inputData)
        var solution: String
        val runtime = measureTimeMillis {
            solution = compute()
        }
        val runtimeString = "%.2f".format(runtime.toDouble() / 1000)
        println("  >> Runtime: ${runtimeString}s")
        return solution
    }

    open val testCases = sequence<TestCase> {}

    open val customExampleData: String?
        get() = null

    abstract val exampleAnswer: String
}