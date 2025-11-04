package year2024

import lib.aoc.Day
import lib.aoc.Part
import lib.aoc.TestCase

fun main() {
    Day(9, 2024, PartA9(), PartB9()).run()
}

open class PartA9 : Part() {
    protected data class File(
        val size: Int,
        val id: Int,
    ) {
        fun checksum(position: Long): Long {
            if (isEmpty()) {
                return 0
            }
            return (position until position + size).sumOf { id.toLong() * it }
        }

        fun isEmpty() = id == -1
    }

    protected lateinit var disk: MutableList<File>

    override fun parse(text: String) {
        disk = text.mapIndexed { i, char -> File(char.digitToInt(), if (i % 2 == 0) i / 2 else -1) }.toMutableList()
    }

    override fun compute(): String {
        val compactedDisk = mutableListOf<File>()
        var filler: File? = null

        while (disk.isNotEmpty()) {
            val file = disk.removeFirst()
            if (file.id != -1) {
                compactedDisk.add(file)
                continue
            }

            var space = file.size
            while (space > 0) {
                if (filler == null || filler.isEmpty() || filler.size == 0) {
                    filler = disk.removeLast()
                    continue
                }

                val fillSpace = minOf(filler.size, space)
                space -= fillSpace
                compactedDisk.add(File(fillSpace, filler.id))
                filler = File(filler.size - fillSpace, filler.id)
            }
        }

        if (filler != null && filler.id != -1 && filler.size > 0) {
            compactedDisk.add(filler)
        }

        return compactedDisk.checksum()
    }

    protected fun List<File>.checksum(): String =
        this
            .fold(0L to 0L) { (pos, sum), file ->
                pos + file.size to
                    sum + file.checksum(pos)
            }.second
            .toString()

    override val exampleAnswer: String
        get() = "1928"
}

class PartB9 : PartA9() {
    override fun compute(): String {
        val maxFileId = disk.maxOf { it.id }
        for (idToMove in maxFileId downTo 1) {
            val indexToMove = disk.indexOfFirst { it.id == idToMove }
            val file = disk[indexToMove]
            for (i in 0 until indexToMove) {
                if (disk[i].isEmpty() && disk[i].size >= file.size) {
                    val remainingSpace = disk[i].size - file.size
                    disk[indexToMove] = File(file.size, -1)
                    disk[i] = File(file.size, file.id)
                    if (indexToMove + 1 < disk.size && disk[indexToMove + 1].isEmpty()) {
                        disk[indexToMove] = File(disk[indexToMove].size + disk[indexToMove + 1].size, -1)
                        disk.removeAt(indexToMove + 1)
                    }
                    if (disk[indexToMove - 1].isEmpty()) {
                        disk[indexToMove] = File(disk[indexToMove].size + disk[indexToMove - 1].size, -1)
                        disk.removeAt(indexToMove - 1)
                    }
                    if (remainingSpace > 0) {
                        disk.add(i + 1, File(remainingSpace, -1))
                    }
                    break
                }
            }
        }

        return disk.checksum()
    }

    protected fun List<File>.print() {
        for (file in this) {
            repeat(file.size) {
                print(if (file.isEmpty()) '.' else file.id.toString())
            }
        }
        println()
    }

    override val exampleAnswer: String
        get() = "2858"

    override val testCases =
        sequence {
            yield(TestCase("test1", "2333133121414131499", "6204"))
            yield(TestCase("test2", "714892711", "813"))
            yield(TestCase("test3", "12101", "4"))
            yield(TestCase("test4", "1313165", "169"))
            yield(TestCase("test5", "12345", "132"))
        }
}