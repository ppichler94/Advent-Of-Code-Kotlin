package lib.aoc

import org.jsoup.Jsoup
import java.io.File
import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class ApiClient(year: Int, day: Int) {
    private val baseUrl = "https://adventofcode.com/$year/day/$day"
    private val sessionId: String
    private val cache = Cache(year, day)

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
        if (response.body().contains("That's the right answer!")) {
            return true
        }
        if (response.body().contains("Did you already complete it")) {
            val answer = extractAnswer(partName)
            return answer == solution
        }
        return false
    }

    private fun postBodyData(part: PartName, solution: String): HttpRequest.BodyPublisher {
        fun String.utf8(): String = URLEncoder.encode(this, "UTF-8")
        val data = mapOf("level" to part.value.toString(), "answer" to solution)
        val result = data.map { (k, v) -> "${(k.utf8())}=${v.utf8()}" }
            .joinToString("&")
        return HttpRequest.BodyPublishers.ofString(result)
    }

    private fun extractAnswer(partName: PartName): String {
        val soup = Jsoup.parse(getProse())
        val result = soup.select("p:contains(puzzle answer was)")
        return when (partName) {
            PartName.A -> result[0].select("code").html()
            PartName.B -> result[1].select("code").html()
        }
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
            return response.body().trimEnd()
        }
        throw IllegalStateException("Error fetching input data: ${response.body()}")
    }

    fun fetchExampleData(): String {
        val prose = getProse()
        return Jsoup.parse(prose).selectFirst("pre code")?.wholeText()?.trimEnd() ?: ""
    }

    private fun getProse(): String {
        return cache.readFileOrElse("prose") {
            fetchProse()
        }
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