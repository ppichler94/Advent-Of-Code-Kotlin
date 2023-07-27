package lib.math

import kotlin.math.sqrt

open class Vector(open val data: IntArray) {
    constructor(data: List<Int>) : this(data.toIntArray())

    open val x: Int
        get() = data[0]

    open val y: Int
        get() = data[1]

    open val z: Int
        get() {
            check(data.size > 2) { "A vector of size ${data.size} does not have component z" }
            return data[2]
        }

    open val w: Int
    get() {
        check(data.size > 3) { "A vector of size ${data.size} does not have component w" }
        return data[3]
    }

    val size: Int = data.size

    operator fun get(index: Int): Int  {
        require(index < data.size) { "index must be in 0..${data.size - 1}" }
        return data[index]
    }

    fun length() = sqrt(data.map { (it*it).toFloat() }.sum())

    infix fun within(range: Iterable<Int>) = data.all { it in range }
    infix fun within(range: List<Iterable<Int>>): Boolean {
        require(range.size >= data.size) { "length of range != length of vector" }
        return (data zip range).all { it.first in it.second }
    }

    fun toMutableVector() = MutableVector(data.copyOf())

    override fun toString(): String = "Vector(${data.joinToString()})"

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        val vec = other as? Vector ?: return false

        return data.contentEquals(vec.data)
    }

    override fun hashCode(): Int {
        return data.contentHashCode()
    }

    companion object {
        fun at(vararg coordinates: Int) = Vector(coordinates)
    }
}

class MutableVector(override val data: IntArray) : Vector(data) {
    override var x: Int
        get() = data[0]
        set(v) { data[0] = v }

    override var y: Int
        get() = data[1]
        set(v) { data[1] = v }

    override var z: Int
        get() {
            check(data.size > 2) { "A vector of size ${data.size} does not have component z" }
            return data[2]
        }
        set(v) {
            check(data.size > 2) { "A vector of size ${data.size} does not have component z" }
            data[2] = v
        }

    override var w: Int
        get() {
            check(data.size > 3) { "A vector of size ${data.size} does not have component w" }
            return data[2]
        }
        set(v) {
            check(data.size > 3) { "A vector of size ${data.size} does not have component w" }
            data[3] = v
        }

    operator fun set(index: Int, v: Int) {
        require(index < data.size) { "index must be in 0..${data.size - 1}" }
        data[index] = v
    }

    fun toVector() = Vector(data.copyOf())

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as MutableVector

        return data.contentEquals(other.data)
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }

    override fun toString(): String = "MutableVector(${data.joinToString()})"

    companion object {
        fun at(vararg coordinates: Int) = MutableVector(coordinates)
    }
}

operator fun Vector.plus(other: Vector) = Vector((data zip other.data).map{ it.first + it.second })
operator fun <T: Vector> T.minus(other: Vector) = Vector((data zip other.data).map{ it.first - it.second })
operator fun <T: Vector> T.times(other: Int) = Vector(data.map{ it * other })
operator fun <T: Vector> T.times(other: Float) = Vector(data.map{ (it * other).toInt() })
operator fun <T: Vector> T.div(other: Int) = Vector(data.map{ it / other })
operator fun <T: Vector> T.div(other: Float) = Vector(data.map{ (it / other).toInt() })
operator fun <T: Vector> T.unaryMinus() = Vector(data.map { -it })

operator fun MutableVector.plusAssign(v: Vector) {
    for (i in this.data.indices) {
        data[i] += v.data[i]
    }
}
operator fun MutableVector.minusAssign(v: Vector) {
    for (i in this.data.indices) {
        data[i] -= v.data[i]
    }
}
operator fun MutableVector.timesAssign(other: Int) {
    for (i in this.data.indices) {
        data[i] *= other
    }
}
operator fun MutableVector.divAssign(other: Int) {
    for (i in this.data.indices) {
        data[i] /= other
    }
}