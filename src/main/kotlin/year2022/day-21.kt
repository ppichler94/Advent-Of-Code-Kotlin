package year2022

import lib.aoc.Day
import lib.aoc.Part
import java.math.BigInteger

fun main() {
    Day(21, 2022, PartA21(), PartB21()).run()
}

open class PartA21 : Part() {
    protected sealed class Job {
        abstract fun doJob(jobs: Map<String, Job>): BigInteger
    }

    protected data class IntJob(private val value: BigInteger) : Job() {
        constructor(text: String) : this(text.toBigInteger())

        override fun doJob(jobs: Map<String, Job>): BigInteger {
            return value
        }
    }

    protected data class MathJob(val operand1: String, val operation: String, val operand2: String) : Job() {
        constructor(text: String) : this(text.split(" ")[0], text.split(" ")[1], text.split(" ")[2])

        override fun doJob(jobs: Map<String, Job>) = when (operation) {
            "+" -> jobs.getValue(operand1).doJob(jobs) + jobs.getValue(operand2).doJob(jobs)
            "-" -> jobs.getValue(operand1).doJob(jobs) - jobs.getValue(operand2).doJob(jobs)
            "*" -> jobs.getValue(operand1).doJob(jobs) * jobs.getValue(operand2).doJob(jobs)
            "/" -> jobs.getValue(operand1).doJob(jobs) / jobs.getValue(operand2).doJob(jobs)
            else -> throw IllegalArgumentException("Unknown operation $operation")
        }
    }

    protected lateinit var jobs: Map<String, Job>

    override fun parse(text: String) {
        jobs = text.split("\n").associate {
            val (name, operation) = it.split(": ")
            val job = if (operation.toIntOrNull() == null) MathJob(operation) else IntJob(operation)
            name to job
        }
    }

    override fun compute(): String {
        return jobs.getValue("root").doJob(jobs).toString()
    }

    override val exampleAnswer: String
        get() = "152"
}

class PartB21 : PartA21() {
    private lateinit var humnJobs: MutableMap<String, Job>

    override fun compute(): String {
        humnJobs = mutableMapOf()
        doHumnJobs("humn")
        return humnJobs["humn"]?.doJob(humnJobs).toString()
    }

    private fun doHumnJobs(name: String) {
        val (key, job) = findJobWith(name)
        job as MathJob
        if (key == "root") {
            val toSolve = if (job.operand1 != name) job.operand1 else job.operand2
            val toAdd = if (job.operand1 == name) job.operand1 else job.operand2
            val result = jobs.getValue(toSolve).doJob(jobs)
            humnJobs[toAdd] = IntJob(result)
            return
        }
        when (job.operation) {
            "+" -> {
                val other = if (job.operand1 != name) job.operand1 else job.operand2
                humnJobs[name] = MathJob(key, "-", other)
                humnJobs[other] = IntJob(jobs.getValue(other).doJob(jobs))
                doHumnJobs(key)
            }

            "-" -> {
                if (job.operand1 == name) {
                    humnJobs[name] = MathJob(key, "+", job.operand2)
                    humnJobs[job.operand2] = IntJob(jobs.getValue(job.operand2).doJob(jobs))
                    doHumnJobs(key)
                } else {
                    humnJobs[name] = MathJob(job.operand1, "-", key)
                    humnJobs[job.operand1] = IntJob(jobs.getValue(job.operand1).doJob(jobs))
                    doHumnJobs(key)
                }
            }

            "*" -> {
                val other = if (job.operand1 != name) job.operand1 else job.operand2
                humnJobs[name] = MathJob(key, "/", other)
                humnJobs[other] = IntJob(jobs.getValue(other).doJob(jobs))
                doHumnJobs(key)
            }

            "/" -> {
                if (job.operand1 == name) {
                    humnJobs[name] = MathJob(key, "*", job.operand2)
                    humnJobs[job.operand2] = IntJob(jobs.getValue(job.operand2).doJob(jobs))
                    doHumnJobs(key)
                } else {
                    humnJobs[name] = MathJob(job.operand1, "/", key)
                    humnJobs[job.operand1] = IntJob(jobs.getValue(job.operand1).doJob(jobs))
                    doHumnJobs(key)
                }
            }
        }
    }

    private fun findJobWith(name: String): Pair<String, Job> {
        return jobs.entries.find { (_, job) ->
            job is MathJob && (job.operand1 == name || job.operand2 == name)
        }?.toPair() ?: Pair("", MathJob("", "", ""))
    }

    override val exampleAnswer: String
        get() = "301"
}
