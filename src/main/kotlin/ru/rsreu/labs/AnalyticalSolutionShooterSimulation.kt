package ru.rsreu.labs

class AnalyticalSolutionShooterSimulation(groups: List<ShooterGroup>) : ShooterSimulation(groups) {
    override fun calculate(): Double {
        val groupPickAndHitProbabilities = getGroupPickAndHitProbabilities()
        var sum = 0.0
        groupPickAndHitProbabilities.forEach { firstPickedGroup ->
            groupPickAndHitProbabilities.forEach { secondPickerGroup ->
                val pickProbability = firstPickedGroup.pickProbability * secondPickerGroup.pickProbability
                val doubleMissProbability =
                    (1.0 - firstPickedGroup.hitProbability) * (1.0 - secondPickerGroup.hitProbability)
                val hitProbability = 1.0 - doubleMissProbability
                sum += pickProbability * hitProbability
            }
        }
        return sum
    }

    private fun getGroupPickAndHitProbabilities(): List<GroupPickAndHitProbabilities> {
        val shooterCount = groups.sumOf { it.shooterCount }
        val groupPickAndHitProbabilities = groups.map {
            GroupPickAndHitProbabilities(
                it.shooterCount.toDouble() / shooterCount, it.hitProbability
            )
        }
        return groupPickAndHitProbabilities
    }
}

private data class GroupPickAndHitProbabilities(
    val pickProbability: Double, val hitProbability: Double
)