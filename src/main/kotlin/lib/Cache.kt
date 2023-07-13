package lib

import java.io.File


class Cache(year: Int, day: Int) {
    private val dir: File = File(".data/$year/$day")

    init {
        dir.mkdirs()
    }

    fun writeFile(name: String, data: String) {
        val file = File("${dir.absolutePath}/$name.txt")
        file.writeText(data)
    }

    fun addToFile(name: String, data: String) {
        val file = File("${dir.absolutePath}/$name.txt")
        file.appendText(data)
    }

    fun readFile(name: String): String {
        val file = File("${dir.absolutePath}/$name.txt")
        return if (file.exists()) file.readText() else ""
    }

    fun readFileOrElse(name: String, supplier: () -> String): String {
        if (!fileExists(name)) {
            val data = supplier()
            writeFile(name, data)
            return data
        }
        return readFile(name)
    }

    fun fileExists(name: String): Boolean {
        val file = File("${dir.absolutePath}/$name.txt")
        return file.exists()
    }
}
