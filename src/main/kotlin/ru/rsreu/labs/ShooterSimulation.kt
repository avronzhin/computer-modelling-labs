package ru.rsreu.labs

abstract class ShooterSimulation(
    protected val groups: List<ShooterGroup>
) {
    abstract fun calculate(): Double
}

data class ShooterGroup(
    val shooterCount: Int, val hitProbability: Double
)