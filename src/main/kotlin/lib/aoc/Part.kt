package lib.aoc

import lib.Util
import kotlin.time.DurationUnit
import kotlin.time.measureTimedValue

enum class PartName(val value: Int) {
    A(1), B(2)
}

data class TestCase(val name: String, val input: String, val expected: String)

abstract class Part {
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
        val timedValue = measureTimedValue { compute() }
        val (solution, runtime) = timedValue
        val successful = testCase.expected.isEmpty() || solution == testCase.expected
        print(Util.colored(">> ", if (successful) Util.Color.GREEN else Util.Color.RED))
        print("TestCase '${testCase.name}' ")
        if (successful) {
            print(Util.colored("OK", Util.Color.GREEN))
        } else {
            print(Util.colored("failed", Util.Color.RED))
            print(" Expected: ${testCase.expected}")
        }
        print(" Result: $solution Runtime: ${runtime.toString(DurationUnit.SECONDS, 3)}\n")
        return successful
    }

    private fun runPuzzle(inputData: String): String {
        println("Running puzzle...")
        parse(inputData)
        val timedValue = measureTimedValue { compute() }
        val (solution, runtime) = timedValue
        println("  >> Runtime: ${runtime.toString(DurationUnit.SECONDS, 3)}")
        return solution
    }

    open val testCases = sequence<TestCase> {}

    open val customExampleData: String?
        get() = null

    abstract val exampleAnswer: String
}