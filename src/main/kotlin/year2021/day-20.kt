package year2021

import lib.aoc.Day
import lib.aoc.Part
import org.jetbrains.kotlinx.multik.api.mk
import org.jetbrains.kotlinx.multik.api.ndarray
import org.jetbrains.kotlinx.multik.api.zeros
import org.jetbrains.kotlinx.multik.ndarray.data.D2Array
import org.jetbrains.kotlinx.multik.ndarray.data.get
import org.jetbrains.kotlinx.multik.ndarray.data.set
import org.jetbrains.kotlinx.multik.ndarray.operations.filter

fun main() {
    Day(20, 2021, PartA20(), PartB20()).run()
}

open class PartA20 : Part() {
    private lateinit var enhancement: String
    private lateinit var image: D2Array<Int>
    open val steps = 2

    override fun parse(text: String) {
        val (enhancementText, imageText) = text.split("\n\n")
        enhancement = enhancementText.replace("\n", "")
        image = mk.ndarray(imageText.split("\n").map { it.map { c -> if (c == '#') 1 else 0 } })
    }

    override fun compute(): String {
        (0..<steps).forEach(::applyEnhancement)
        return image.filter { it != 0 }.size.toString()
    }

    private fun applyEnhancement(step: Int) {
        val newImage = mk.zeros<Int>(image.shape[0] + 2, image.shape[1] + 2)
        for (y in -1..image.shape[1]) {
            for (x in -1..image.shape[0]) {
                val filler = if (enhancement[0] == '.') 0 else step % 2
                val index = getIndex(x, y, filler)
                newImage[y + 1, x + 1] = if (enhancement[index] == '.') 0 else 1
            }
        }
        image = newImage
    }

    private fun getIndex(x: Int, y: Int, filler: Int): Int {
        val index = mutableListOf<Int>()
        for (yi in -1..1) {
            for (xi in -1..1) {
                if (x + xi < 0 || x + xi >= image.shape[1]) {
                    index.add(filler)
                } else if (y + yi < 0 || y + yi >= image.shape[0]) {
                    index.add(filler)
                } else {
                    index.add(image[y + yi, x + xi])
                }
            }
        }
        return index.joinToString("").toInt(2)
    }

    override val exampleAnswer: String
        get() = "35"
}

class PartB20 : PartA20() {
    override val steps = 50

    override val exampleAnswer: String
        get() = "3351"
}
