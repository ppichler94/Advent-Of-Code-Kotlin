package year2022

import lib.Day
import lib.Part

fun main() {
    Day(7, 2022, PartA7(), PartB7()).run()
}


open class PartA7 : Part() {
    data class File(val name: String, val size: Int)

    data class Directory(val name: String, val parent: Directory?) {
        val directories = mutableListOf<Directory>()
        private val files = mutableListOf<File>()

        val size: Int
            get() {
                val filesSize = files.sumOf { it.size }
                val directoriesSize = directories.sumOf { it.size }
                return filesSize + directoriesSize
            }

        fun parseLs(lines: List<String>) {
            lines.forEach {
                if (it.substring(0 .. 2) == "dir") {
                    val dir = Directory(it.drop(4), this)
                    directories.add(dir)
                } else {
                    val (size, name) = it.split(" ")
                    files.add(File(name, size.toInt()))
                }
            }

        }
    }

    sealed class Command(val action: (Directory) -> Directory)
    class Ls(private val value: List<String>) : Command({
        it.parseLs(value)
        it
    })
    class Cd(private val value: String) : Command({
        if (value == "..") {
            it.parent !!
        } else {
            it.directories.first { it.name == value }
        }
    })

    lateinit var rootDir: Directory
    override fun parse(text: String) {
        val lines = text.split("\n")
        rootDir = Directory("root", null)
        var currentDir = rootDir
        for (i in 1 until lines.size) {
            val parts = lines[i].split(" ")
            if (parts[0] == "$") {
                when(parts[1]) {
                    "cd" -> currentDir = Cd(parts[2]).action(currentDir)
                    "ls" -> {
                        val lsLines = lines.drop(i + 1).takeWhile { it[0] != '$' }
                        currentDir = Ls(lsLines).action(currentDir)
                    }
                }
            }
        }
    }

    override fun compute(): String {
        val sizes = mutableListOf<Int>()
        calculateSizes(rootDir, sizes)
        return sizes.filter { it <= 100_000 }.sum().toString()
    }

    private fun calculateSizes(dir: Directory, sizes: MutableList<Int>) {
        sizes.add(dir.size)
        dir.directories.forEach {
            calculateSizes(it, sizes)
        }
    }

    override val exampleAnswer: String
        get() = "95437"

    override val customExampleData: String
        get() = day7exampleInput
}

class PartB7 : PartA7() {
    override fun compute(): String {
        val requiredSize = 30_000_000 - (70_000_000 - rootDir.size)
        return findDirSizeToDelete(requiredSize, rootDir, rootDir.size).toString()
    }

    private fun findDirSizeToDelete(requiredSize: Int, dir: Directory, size: Int): Int {
        dir.directories.forEach {
            val result = findDirSizeToDelete(requiredSize, it, size)
            if (result in requiredSize until size) {
                return result
            }
        }
        if (dir.size in requiredSize until size) {
            return dir.size
        }
        return size
    }

    override val exampleAnswer: String
        get() = "24933642"
}

val day7exampleInput = """
$ cd /
$ ls
dir a
14848514 b.txt
8504156 c.dat
dir d
$ cd a
$ ls
dir e
29116 f
2557 g
62596 h.lst
$ cd e
$ ls
584 i
$ cd ..
$ cd ..
$ cd d
$ ls
4060174 j
8033020 d.log
5626152 d.ext
7214296 k 
""".trimIndent()