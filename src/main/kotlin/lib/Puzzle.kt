package lib

import org.jsoup.Jsoup
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class Puzzle(private val day: Int, private val year: Int) {

    val exampleData: String
    val inputData: String

    private val sessionId: String
    private val baseUrl = "https://adventofcode.com/$year/day/$day"

    init {
        sessionId = readSessionId()
        inputData = readInputData()
        exampleData = readExampleData()
    }

    fun submit(partName: PartName, solution: String) {
        val alreadyAnswered = checkPreviousAnswers(partName, solution)
        if (!alreadyAnswered) {
            val correct = postAnswer(partName, solution)
            if (correct) {
                storeCorrectAnswer(partName, solution)
            } else {
                storeIncorrectAnswer(partName, solution)
            }
        }
    }

    private fun checkPreviousAnswers(partName: PartName, solution: String): Boolean {
        val answerFile = File(".data/$year/$day/answer_${partName.value}.txt")
        answerFile.parentFile.mkdirs()
        if (answerFile.exists() && answerFile.readText() == solution) {
            Util.printlnColored("  >> Answer $solution is correct", Util.Color.GREEN)
            return true
        }

        val incorrectAnswersFile = File(".data/$year/$day/incorrect_answers_${partName.value}.txt")
        if (incorrectAnswersFile.exists() && incorrectAnswersFile.readLines().contains(solution)) {
            Util.printlnColored("  >> Answer $solution was incorrect", Util.Color.RED)
            return true
        }

        return false
    }

    private fun postAnswer(partName: PartName, solution: String): Boolean {
        val url = "$baseUrl/answer"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("cookie", "session=$sessionId")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .header("User-Agent", "github.com/ppichler94/advent-of-code-kotlin by ppichler@outlook.at")
            .POST(postBodyData(partName, solution))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() != 200) {
            throw IllegalStateException("submit answer failed. Got ${response.statusCode()} status code")
        }
        if (response.body().contains("the right answer") or response.body().contains("Did you already complete it")) {
            return true
        }
        return false
    }

    private fun storeCorrectAnswer(partName: PartName, solution: String) {
        val answerFile = File(".data/$year/$day/answer_${partName.value}.txt")
        answerFile.parentFile.mkdirs()
        answerFile.writeText(solution)
    }

    private fun storeIncorrectAnswer(partName: PartName, solution: String) {
        val incorrectAnswersFile = File(".data/$year/$day/incorrect_answers_${partName.value}.txt")
        incorrectAnswersFile.parentFile.mkdirs()
        incorrectAnswersFile.printWriter().use { it.println(solution) }
    }

    private fun postBodyData(part: PartName, solution: String): HttpRequest.BodyPublisher {
        fun String.utf8(): String = URLEncoder.encode(this, "UTF-8")
        val data = mapOf("level" to part.value.toString(), "answer" to solution)
        val result = data.map {(k, v) -> "${(k.utf8())}=${v.utf8()}"}
            .joinToString("&")
        return HttpRequest.BodyPublishers.ofString(result)

    }

    private fun readInputData(): String {
        val inputFileName = ".data/$year/$day/input.txt"

        val inputData = File(inputFileName)
        if (!inputData.exists()) {
            val data = fetchInputData()
            inputData.parentFile.mkdirs()
            inputData.createNewFile()
            inputData.writeText(data)
            return data
        }
        return inputData.readText()
    }

    private fun fetchInputData(): String {
        val inputUrl = "$baseUrl/input"
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(inputUrl))
            .header("cookie", "session=$sessionId")
            .header("User-Agent", "github.com/ppichler94/advent-of-code-kotlin by ppichler@outlook.at")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() == 200) {
            return response.body().trim()
        }
        throw IllegalStateException("Error fetching input data: ${response.body()}")
    }

    private fun readExampleData(): String {
        val inputFileName = ".data/$year/$day/example.txt"

        val inputData = File(inputFileName)
        if (!inputData.exists()) {
            val data = fetchExampleData()
            inputData.parentFile.mkdirs()
            inputData.createNewFile()
            inputData.writeText(data)
            return data
        }
        return inputData.readText()
    }

    private fun fetchExampleData(): String {
        val prose = getProse()
        return Jsoup.parse(prose).select("pre code").html().trim()
    }

    private fun getProse(): String {
        val fileName = ".data/$year/$day/prose.txt"

        val file = File(fileName)
        if (!file.exists()) {
            val data = fetchProse()
            file.parentFile.mkdirs()
            file.createNewFile()
            file.writeText(data)
            return data
        }
        return file.readText()
    }

    private fun fetchProse(): String {
        val url = baseUrl
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .header("cookie", "session=$sessionId")
            .header("User-Agent", "github.com/ppichler94/advent-of-code-kotlin by ppichler@outlook.at")
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        if (response.statusCode() == 200) {
            return response.body()
        }
        throw IllegalStateException("Error fetching prose: ${response.body()}")
    }

    private fun readSessionId(): String {
        val file = File(".session_id")
        if (!file.exists()) {
            throw IllegalStateException("Session id file not found. Create a .session_id file in the working directory")
        }
        return file.readText()
    }
}