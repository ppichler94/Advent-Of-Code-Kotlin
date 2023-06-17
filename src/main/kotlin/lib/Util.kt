package lib

class Util {
    enum class Color(val code: Int) {
        RED(31),
        GREEN(32),
        YELLOW(33),
    }

    companion object {
        fun colored(message: String, color: Color): String {
            return "\u001B[${color.code}m${message}\u001B[0m"
        }

        fun printlnColored(message: String, color: Color) {
            println(colored(message, color))
        }
    }
}