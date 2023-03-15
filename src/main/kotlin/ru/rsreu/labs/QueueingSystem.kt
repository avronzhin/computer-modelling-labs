package ru.rsreu.labs

import ru.rsreu.labs.generator.ExponentialDistributionGenerator
import ru.rsreu.labs.generator.MacLarenMarsagliaGenerator

class QueueingSystem(
    private val channelCount: Int,
    private val channelCost: Double,
    private val intensity: Double,
    private val serviceTime: Double,
    private val revenue: Double
) {

    private val arrivalTimeGenerator = ExponentialDistributionGenerator(MacLarenMarsagliaGenerator(), intensity)
    private val serviceDurationGenerator =
        ExponentialDistributionGenerator(MacLarenMarsagliaGenerator(), 1.0 / serviceTime)

    fun getModellingCharacteristicsEstimates(
        applicationMaxCount: Int, modelTime: Double
    ): Pair<CharacteristicsEstimates, Finance> {
        val applications = generateApplications(applicationMaxCount, modelTime)
        val (approvedApplications, finishTime) = processApplications(applications)

        val applicationCount = applications.size
        val approvedCount = approvedApplications.size
        val approvedProbability = approvedCount.toDouble() / applicationCount
        val averageStayTime = approvedApplications.map { it.serviceDuration }.average()
        val workTime = approvedApplications.sumOf { it.serviceDuration }
        val utilizationRate = workTime / (finishTime * channelCount)

        val income = approvedCount * revenue
        val expenses = finishTime * channelCount * channelCost
        val profit = income - expenses
        return Pair(
            CharacteristicsEstimates(
                applicationCount, approvedCount, approvedProbability, averageStayTime, utilizationRate
            ), Finance(
                income, expenses, profit
            )
        )
    }

    private fun generateApplications(applicationMaxCount: Int, modelTime: Double): MutableList<Application> {
        var time = 0.0
        val applications = mutableListOf<Application>()
        for (i in 0 until applicationMaxCount) {
            time += arrivalTimeGenerator.nextDouble()
            if (time < modelTime) {
                val application = Application(time, serviceDurationGenerator.nextDouble())
                applications.add(application)
            } else {
                break
            }
        }
        return applications
    }

    data class Application(
        val arrivalTime: Double, val serviceDuration: Double
    )

    private fun processApplications(applications: List<Application>): Pair<List<Application>, Double> {
        val channels = MutableList(channelCount) { Channel() }
        val approvedApplications = mutableListOf<Application>()
        for (i in applications.indices) {
            val application = applications[i]
            for (j in channels.indices) {
                val channel = channels[j]
                if (channel.isFree(application.arrivalTime)) {
                    channel.occupy(application.arrivalTime + application.serviceDuration)
                    approvedApplications.add(application)
                    break
                }
            }
        }
        val finishTime = channels.maxOfOrNull { it.release } ?: throw IllegalStateException()
        return Pair(approvedApplications, finishTime)
    }

    class Channel {
        internal var release: Double = 0.0

        fun isFree(time: Double) = time > release

        fun occupy(release: Double) {
            this.release = release
        }
    }

    data class Finance(
        val income: Double, val expenses: Double, val profit: Double
    )

    data class CharacteristicsEstimates(
        val applicationCount: Int,
        val approvedCount: Int,
        val approvedProbability: Double,
        val averageStayTime: Double,
        val utilizationRate: Double,
    )
}