package year2022

import lib.Day
import lib.Part

fun main() {
    Day(4, 2022, PartA4(), PartB4()).run()
}


open class PartA4 : Part() {
    data class Section(val start1: Int, val end1: Int, val start2: Int, val end2: Int)
    internal lateinit var sections: List<Section>
    override fun parse(text: String) {
        val regex = """\d+""".toRegex()
        sections = text.split("\n").map {
            val result = regex.findAll(it)
            val (start1, end1, start2, end2) = result.toList().map { it.value.toInt() }
            Section(start1, end1, start2, end2)
        }
    }

    override fun compute(): String {
        return sections.count(this::fullyContains).toString()
    }

    private fun fullyContains(section: Section): Boolean {
        return (((section.start1 <= section.start2) and (section.end1 >= section.end2))
                or ((section.start2 <= section.start1) and (section.end2 >= section.end1)))
    }

    override val exampleAnswer: String
        get() = "2"
}

class PartB4 : PartA4() {
    override fun compute(): String {
        return sections.count(this::overlaps).toString()
    }

    private fun overlaps(section: Section): Boolean {
        if (section.start1 in section.start2 .. section.end2) {
            return true
        }
        if (section.end1 in section.start2 .. section.end2) {
            return true
        }
        if (section.start2 in section.start1 .. section.end1) {
            return true
        }
        if (section.end2 in section.start1 .. section.end1) {
            return true
        }
        return false
    }

    override val exampleAnswer: String
        get() = "4"
}
