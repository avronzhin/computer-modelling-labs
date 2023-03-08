package ru.rsreu.labs

import ru.rsreu.labs.generator.Generator
import kotlin.math.floor

class MonteCarloShooterSimulation(
    groups: List<ShooterGroup>, private val generator: Generator, private val iterationCount: Int
) : ShooterSimulation(groups) {
    override fun calculate(): Double {
        var counter = 0
        repeat(iterationCount) {
            val firstHitProbability = getRandomHitProbability()
            val secondHitProbability = getRandomHitProbability()
            val firstHit = (generator.nextDouble() < firstHitProbability)
            val secondHit = (generator.nextDouble() < secondHitProbability)
            val hit = firstHit or secondHit
            if (hit) counter++
        }
        return counter.toDouble() / iterationCount
    }

    private fun getRandomHitProbability(): Double {
        val shooterCount = groups.sumOf { it.shooterCount }
        val shooterIndex = floor((generator.nextDouble() * shooterCount)).toInt()
        var acc = 0
        for (i in groups.indices) {
            val group = groups[i]
            if (shooterIndex < group.shooterCount + acc) return group.hitProbability
            acc += group.shooterCount
        }
        throw IllegalStateException()
    }
}