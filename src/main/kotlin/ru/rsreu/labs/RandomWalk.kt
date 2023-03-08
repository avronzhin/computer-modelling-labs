package ru.rsreu.labs

import ru.rsreu.labs.generator.Generator
import kotlin.math.absoluteValue

class RandomWalk(
    private val m: Int, private val generator: Generator, private val leftProbability: Double = 0.5
) {
    fun getValue(): Int {
        var counter = 0
        repeat(m) {
            val randomValue = generator.nextDouble()
            if (randomValue < leftProbability) counter-- else counter++
        }
        return counter.absoluteValue
    }
}