package lib

import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ApiClient(year: Int, day: Int) {
    private val baseUrl = "https://adventofcode.com/$year/day/$day"
    private val sessionId: String

    init {
        sessionId = readSessionId()
    }

    fun postAnswer(partName: PartName, solution: String): Boolean {
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

    private fun postBodyData(part: PartName, solution: String): HttpRequest.BodyPublisher {
        fun String.utf8(): String = URLEncoder.encode(this, "UTF-8")
        val data = mapOf("level" to part.value.toString(), "answer" to solution)
        val result = data.map {(k, v) -> "${(k.utf8())}=${v.utf8()}"}
            .joinToString("&")
        return HttpRequest.BodyPublishers.ofString(result)

    }

    fun fetchInputData(): String {
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

    fun fetchProse(): String {
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