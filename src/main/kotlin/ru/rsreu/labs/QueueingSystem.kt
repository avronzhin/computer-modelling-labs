package ru.rsreu.labs

class QueueingSystem(
    private val channelCount: Int,
    private val channelCost: Double,
    private val intensity: Double,
    private val serviceTime: Double,
    private val revenue: Double,
) {

    fun getCharacteristicsEstimates(): CharacteristicsEstimates {
        return CharacteristicsEstimates(5)
    }

    data class CharacteristicsEstimates(
        private val a: Int
    )
}