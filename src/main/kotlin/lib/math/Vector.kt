package lib.math

open class Vector2i(open val x: Int, open val y: Int) {
    operator fun plus(v: Vector2i) = Vector2i(x + v.x, y + v.y)
    operator fun minus(v: Vector2i) = Vector2i(x - v.x, y - v.y)
    operator fun times(v: Int) = Vector2i(x * v, y * v)
    operator fun div(v: Int) = Vector2i(x / v, y / v)

    operator fun unaryMinus() = Vector2i(-x, -y)

    operator fun get(index: Int) = when(index) {
        0 -> x
        1 -> y
        else -> throw IllegalArgumentException("index must be in 0..1")
    }

    infix fun within(range: Iterable<Int>) = (x in range) && (y in range)
    infix fun within(range: List<Iterable<Int>>) = (x in range[0]) && (y in range[1])

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        val vec = other as? Vector2i ?: return false

        if (x != vec.x) return false
        return y == vec.y
    }

    override fun hashCode(): Int {
        var result = x
        result = 31 * result + y
        return result
    }

    override fun toString(): String = "Vector2i($x, $y)"

    fun toMutableVector() = MutableVector2i(x, y)
}

data class MutableVector2i(override var x: Int, override var y: Int) : Vector2i(x, y) {
    operator fun plusAssign(v: Vector2i) {
        x += v.x
        y += v.y
    }
    operator fun minusAssign(v: Vector2i) {
        x -= v.x
        y -= v.y
    }
    operator fun timesAssign(v: Int) {
        x *= v
        y *= v
    }
    operator fun divAssign(v: Int) {
        x /= v
        y /= v
    }

    operator fun inc() = MutableVector2i(x + 1, y + 1)
    operator fun dec() = MutableVector2i(x - 1, y - 1)

    operator fun set(index: Int, v: Int) = when(index) {
        0 -> x = v
        1 -> y = v
        else -> throw IllegalArgumentException("index must be in 0..1")
    }

    fun toVector() = Vector2i(x, y)
}