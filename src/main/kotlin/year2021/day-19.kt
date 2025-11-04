package year2021

import lib.Position
import lib.aoc.Day
import lib.aoc.Part
import lib.math.Vector
import lib.math.combinations

fun main() {
    Day(19, 2021, PartA19(), PartB19()).run()
}

private fun Position.rotate(rotation: Int) = when (rotation) {
    0 -> Position.at(get(0), get(1), get(2))
    1 -> Position.at(get(0), -get(1), -get(2))
    2 -> Position.at(get(0), get(2), -get(1))
    3 -> Position.at(get(0), -get(2), get(1))
    4 -> Position.at(-get(0), get(1), -get(2))
    5 -> Position.at(-get(0), -get(1), get(2))
    6 -> Position.at(-get(0), get(2), get(1))
    7 -> Position.at(-get(0), -get(2), -get(1))
    8 -> Position.at(get(1), get(0), -get(2))
    9 -> Position.at(get(1), -get(0), get(2))
    10 -> Position.at(get(1), get(2), get(0))
    11 -> Position.at(get(1), -get(2), -get(0))
    12 -> Position.at(-get(1), get(0), get(2))
    13 -> Position.at(-get(1), -get(0), -get(2))
    14 -> Position.at(-get(1), get(2), -get(0))
    15 -> Position.at(-get(1), -get(2), get(0))
    16 -> Position.at(get(2), get(1), -get(0))
    17 -> Position.at(get(2), -get(1), get(0))
    18 -> Position.at(get(2), get(0), get(1))
    19 -> Position.at(get(2), -get(0), -get(1))
    20 -> Position.at(-get(2), get(0), -get(1))
    21 -> Position.at(-get(2), -get(0), get(1))
    22 -> Position.at(-get(2), get(1), get(0))
    23 -> Position.at(-get(2), -get(1), -get(0))
    else -> error("Rotation $rotation is out of range")
}

open class PartA19 : Part() {
    protected data class Scanner(val id: Int) {
        constructor(id: Int, beacons: List<Position>) : this(id) {
            this.beacons = beacons
        }

        var beacons: List<Position> = listOf()
        var position: Position = Position.at(0, 0, 0)
            private set

        val distances get(): Map<Position, List<Int>> {
            val beaconsMap = mutableMapOf<Position, List<Int>>()
            beacons.indices.combinations().forEach { (i, j) ->
                val distance = beacons[i].squaredDistance(beacons[j])
                beaconsMap[beacons[i]] = (beaconsMap[beacons[i]] ?: emptyList()) + distance
                beaconsMap[beacons[j]] = (beaconsMap[beacons[j]] ?: emptyList()) + distance
            }
            return beaconsMap
        }

        fun setPositionAndRotation(position: Position, rotation: Int) {
            this.position = position
            beacons = beacons.map { it.rotate(rotation) }
            beacons = beacons.map { it + position.position }
        }

        companion object {
            fun ofText(text: String): Scanner {
                val lines = text.split("\n")
                val id = """\d+""".toRegex().find(lines.first())?.value?.toInt() ?: -1
                val beacons = lines
                    .drop(1)
                    .map { it.split(",").map(String::toInt) }
                    .map { Position(Vector(it)) }
                return Scanner(id, beacons)
            }
        }
    }

    protected lateinit var scanners: MutableList<Scanner>
    private lateinit var unprocessedScanners: MutableList<Scanner>

    override fun parse(text: String) {
        val scannerText = text.split("\n\n")
        val originScanner = Scanner.ofText(scannerText.first())
        scanners = mutableListOf(originScanner)
        unprocessedScanners = scannerText.drop(1).map(Scanner::ofText).toMutableList()
    }

    override fun compute(): String {
        processScanners()
        return scanners.flatMap { it.beacons }.toSet().size.toString()
    }

    protected fun processScanners() {
        while (unprocessedScanners.isNotEmpty()) {
            for (i in 0..<scanners.size) {
                val intersections = getIntersections(scanners[i])
                for (toMatch in intersections) {
                    matchScanner(scanners[i], toMatch)
                }
            }
        }
    }

    private fun getIntersections(matchedScanner: Scanner): List<Scanner> {
        val distances = unprocessedScanners
            .map { scanner -> scanner to scanner.beacons.combinations().map { (a, b) -> a.squaredDistance(b) } }
            .associate { it }
        val matchedScannerDistances =
            matchedScanner.beacons.combinations().map { (a, b) -> a.squaredDistance(b) }.toSet()
        return distances
            .filter { (_, distances) -> distances.intersect(matchedScannerDistances).size >= 66 } // 66 = 12!/10!
            .map { (scanners, _) -> scanners }
    }

    private fun matchScanner(origin: Scanner, toMatch: Scanner) {
        val originDistances = origin.distances
        val toMatchDistances = toMatch.distances
        val matchingBeacons = toMatchDistances
            .mapNotNull { (pos, distances) ->
                val match = originDistances.filter { it.value.intersect(distances.toSet()).size >= 11 }
                if (match.isNotEmpty()) {
                    match.entries.first().key to pos
                } else {
                    null
                }
            }
        repeat(24) { rotation ->
            val distances = matchingBeacons.map { it.first.squaredDistance(it.second.rotate(rotation)) }
            if (distances.toSet().size == 1) {
                val offset = matchingBeacons.first().first - matchingBeacons.first().second.rotate(rotation).position
                toMatch.setPositionAndRotation(offset, rotation)
                scanners.add(toMatch)
                unprocessedScanners.remove(toMatch)
                return
            }
        }
    }


    override val exampleAnswer: String
        get() = "79"

    override val customExampleData: String?
        get() = inputDay19
}

class PartB19 : PartA19() {
    override fun compute(): String {
        processScanners()
        return scanners
            .combinations()
            .maxOf { it.first.position.manhattanDistance(it.second.position) }
            .toString()
    }

    override val exampleAnswer: String
        get() = "3621"
}

private val inputDay19 = """
    --- scanner 0 ---
    404,-588,-901
    528,-643,409
    -838,591,734
    390,-675,-793
    -537,-823,-458
    -485,-357,347
    -345,-311,381
    -661,-816,-575
    -876,649,763
    -618,-824,-621
    553,345,-567
    474,580,667
    -447,-329,318
    -584,868,-557
    544,-627,-890
    564,392,-477
    455,729,728
    -892,524,684
    -689,845,-530
    423,-701,434
    7,-33,-71
    630,319,-379
    443,580,662
    -789,900,-551
    459,-707,401

    --- scanner 1 ---
    686,422,578
    605,423,415
    515,917,-361
    -336,658,858
    95,138,22
    -476,619,847
    -340,-569,-846
    567,-361,727
    -460,603,-452
    669,-402,600
    729,430,532
    -500,-761,534
    -322,571,750
    -466,-666,-811
    -429,-592,574
    -355,545,-477
    703,-491,-529
    -328,-685,520
    413,935,-424
    -391,539,-444
    586,-435,557
    -364,-763,-893
    807,-499,-711
    755,-354,-619
    553,889,-390

    --- scanner 2 ---
    649,640,665
    682,-795,504
    -784,533,-524
    -644,584,-595
    -588,-843,648
    -30,6,44
    -674,560,763
    500,723,-460
    609,671,-379
    -555,-800,653
    -675,-892,-343
    697,-426,-610
    578,704,681
    493,664,-388
    -671,-858,530
    -667,343,800
    571,-461,-707
    -138,-166,112
    -889,563,-600
    646,-828,498
    640,759,510
    -630,509,768
    -681,-892,-333
    673,-379,-804
    -742,-814,-386
    577,-820,562

    --- scanner 3 ---
    -589,542,597
    605,-692,669
    -500,565,-823
    -660,373,557
    -458,-679,-417
    -488,449,543
    -626,468,-788
    338,-750,-386
    528,-832,-391
    562,-778,733
    -938,-730,414
    543,643,-506
    -524,371,-870
    407,773,750
    -104,29,83
    378,-903,-323
    -778,-728,485
    426,699,580
    -438,-605,-362
    -469,-447,-387
    509,732,623
    647,635,-688
    -868,-804,481
    614,-800,639
    595,780,-596

    --- scanner 4 ---
    727,592,562
    -293,-554,779
    441,611,-461
    -714,465,-776
    -743,427,-804
    -660,-479,-426
    832,-632,460
    927,-485,-438
    408,393,-506
    466,436,-512
    110,16,151
    -258,-428,682
    -393,719,612
    -211,-452,876
    808,-476,-593
    -575,615,604
    -485,667,467
    -680,325,-822
    -627,-443,-432
    872,-547,-609
    833,512,582
    807,604,487
    839,-516,451
    891,-625,532
    -652,-548,-490
    30,-46,-14
""".trimIndent()