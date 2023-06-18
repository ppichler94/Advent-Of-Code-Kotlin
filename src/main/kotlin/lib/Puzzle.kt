package lib

import org.jsoup.Jsoup

class Puzzle(day: Int, year: Int) {

    val exampleData: String
    val inputData: String

    private val cache = Cache(year, day)
    private val apiClient = ApiClient(year, day)

    init {
        inputData = readInputData()
        exampleData = readExampleData()
    }

    fun submit(partName: PartName, solution: String) {
        val alreadyAnswered = checkPreviousAnswers(partName, solution)
        if (!alreadyAnswered) {
            val correct = apiClient.postAnswer(partName, solution)
            if (correct) {
                Util.printlnColored("  >> Answer $solution is correct", Util.Color.GREEN)
                cache.writeFile("answer_${partName.value}", solution)
            } else {
                Util.printlnColored("  >> Answer $solution was incorrect", Util.Color.RED)
                cache.writeFile("incorrect_answers_${partName.value}", solution)
            }
        }
    }

    private fun checkPreviousAnswers(partName: PartName, solution: String): Boolean {
        val fileName = "answer_${partName.value}"
        if (cache.fileExists(fileName)) {
            val previousSolution = cache.readFile(fileName)
            if (previousSolution == solution) {
                Util.printlnColored("  >> Answer $solution is correct", Util.Color.GREEN)
            } else {
                Util.printlnColored("  >> Answer $solution was incorrect", Util.Color.RED)
                Util.printlnColored("  >> Already answered. Correct answer is $previousSolution", Util.Color.YELLOW)
            }
            return true
        }

        val incorrectAnswersFileName = "incorrect_answers_${partName.value}"
        if (cache.readFile(incorrectAnswersFileName).split("\n").contains(solution)) {
            Util.printlnColored("  >> Answer $solution was incorrect", Util.Color.RED)
            return true
        }

        return false
    }

    private fun readInputData(): String {
        return cache.readFileOrElse("input") {
            apiClient.fetchInputData()
        }
    }

    private fun readExampleData(): String {
        return cache.readFileOrElse("example") {
            fetchExampleData()
        }
    }

    private fun fetchExampleData(): String {
        val prose = getProse()
        return Jsoup.parse(prose).selectFirst("pre code")?.html()?.trim() ?: ""
    }

    private fun getProse(): String {
        return cache.readFileOrElse("prose") {
            apiClient.fetchProse()
        }
    }
}