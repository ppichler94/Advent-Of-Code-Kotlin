package year2021

import lib.aoc.Day
import lib.aoc.Part
import lib.aoc.TestCase

fun main() {
    Day(16, 2021, PartA16(), PartB16()).run()
}

open class PartA16 : Part() {
    sealed class Packet(val version: Int, val length: Int, val value: () -> Long)

    sealed class OperatorPacket(version: Int, length: Int, val subPackets: List<Packet>, value: () -> Long) :
        Packet(version, length, value)

    class LiteralPacket(version: Int, length: Int, v: Long) : Packet(version, length, { v })

    class SumPacket(version: Int, length: Int, subPackets: List<Packet>) :
        OperatorPacket(version, length, subPackets, { subPackets.sumOf { it.value() } })

    class ProductPacket(version: Int, length: Int, subPackets: List<Packet>) :
        OperatorPacket(version, length, subPackets, { subPackets.fold(1) { acc, p -> acc * p.value() } })

    class MinPacket(version: Int, length: Int, subPackets: List<Packet>) :
        OperatorPacket(version, length, subPackets, { subPackets.minOf { it.value() } })

    class MaxPacket(version: Int, length: Int, subPackets: List<Packet>) :
        OperatorPacket(version, length, subPackets, { subPackets.maxOf { it.value() } })

    class GreaterThanPacket(version: Int, length: Int, subPackets: List<Packet>) :
        OperatorPacket(version, length, subPackets, { if (subPackets[0].value() > subPackets[1].value()) 1 else 0 })

    class LessThanPacket(version: Int, length: Int, subPackets: List<Packet>) :
        OperatorPacket(version, length, subPackets, { if (subPackets[0].value() < subPackets[1].value()) 1 else 0 })

    class EqualToPacket(version: Int, length: Int, subPackets: List<Packet>) :
        OperatorPacket(version, length, subPackets, { if (subPackets[0].value() == subPackets[1].value()) 1 else 0 })

    private fun Packet(text: String): Packet {
        val version = text.take(3).toInt(2)
        val type = text.slice(3..<6).toInt(2)
        val content = text.drop(6)

        return if (type == 4) {
            val (length, value) = parseLiteralPacket(content)
            LiteralPacket(version, length + 6, value)
        } else {
            val (length, subpackets) = parseSubpackets(content)
            Packet(version, length + 6, subpackets, type)
        }
    }

    private fun Packet(version: Int, length: Int, subPackets: List<Packet>, type: Int): Packet = when (type) {
        0 -> SumPacket(version, length, subPackets)
        1 -> ProductPacket(version, length, subPackets)
        2 -> MinPacket(version, length, subPackets)
        3 -> MaxPacket(version, length, subPackets)
        5 -> GreaterThanPacket(version, length, subPackets)
        6 -> LessThanPacket(version, length, subPackets)
        7 -> EqualToPacket(version, length, subPackets)
        else -> throw IllegalArgumentException("Unknown Packet type $type")
    }

    private fun parseLiteralPacket(text: String): Pair<Int, Long> {
        var value = ""
        var length = 0
        var current = text
        while (true) {
            val block = current.take(5)
            value += block.drop(1)
            length += 5
            if (block[0] == '0') {
                return length to value.toLong(2)
            }
            current = current.drop(5)
        }
    }

    private fun parseSubpackets(text: String): Pair<Int, List<Packet>> {
        var length = 1
        val lengthType = text[0]
        var currentLength = 0
        val subPackets = mutableListOf<Packet>()
        var content: String
        val subpacketLength: Int
        if (lengthType == '0') {
            subpacketLength = text.slice(1..<16).toInt(2)
            length += 15
            content = text.drop(16)
        } else {
            subpacketLength = text.slice(1..<12).toInt(2)
            length += 11
            content = text.drop(12)
        }
        while (currentLength < subpacketLength) {
            val subpacket = Packet(content)
            subPackets.add(subpacket)
            length += subpacket.length
            content = content.drop(subpacket.length)
            currentLength += if (lengthType == '1') 1 else subpacket.length
        }
        return length to subPackets
    }

    protected lateinit var packet: Packet

    override fun parse(text: String) {
        val binaryText = text.map(::hexDigitToBinary).joinToString("")
        packet = Packet(binaryText)
    }

    private fun hexDigitToBinary(digit: Char): String {
        return  "%4s".format(digit.digitToInt(16).toString(2)).replace(' ', '0')
    }

    override fun compute(): String {
        return versionSum(packet).toString()
    }

    private fun versionSum(packet: Packet): Int = when (packet) {
        is OperatorPacket -> packet.version + packet.subPackets.sumOf { versionSum(it) }
        else -> packet.version
    }

    override val exampleAnswer: String
        get() = "16"

    override val customExampleData: String?
        get() = "8A004A801A8002F478"

    override val testCases = sequence {
        yield(TestCase("Operator Packet with 2 subpackets", "620080001611562C8802118E34", "12"))
        yield(TestCase("Operator Packet with length type 1", "C0015000016115A2E0802F182340", "23"))
        yield(TestCase("Multiple nested packets", "A0016C880162017C3686B18A3D4780", "31"))
    }
}

class PartB16 : PartA16() {
    override fun compute(): String {
        return packet.value().toString()
    }

    override val exampleAnswer: String
        get() = "15"

    override val testCases = sequence {
        yield(TestCase("Sum of 1 and 2", "C200B40A82", "3"))
        yield(TestCase("Product of 6 and 9", "04005AC33890", "54"))
        yield(TestCase("Min of 7, 8 and 9", "880086C3E88112", "7"))
        yield(TestCase("Max of 7, 8, and 9", "CE00C43D881120", "9"))
        yield(TestCase("5 less than 15", "D8005AC2A8F0", "1"))
        yield(TestCase("5 greater than 15", "F600BC2D8F", "0"))
        yield(TestCase("5 equal to 15", "9C005AC2F8F0", "0"))
        yield(TestCase("1 + 3 = 2 * 2", "9C0141080250320F1802104A08", "1"))
    }
}
