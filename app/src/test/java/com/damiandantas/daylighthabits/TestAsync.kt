package com.damiandantas.daylighthabits

import com.damiandantas.daylighthabits.utils.parallelMap
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertContentEquals

class AsyncTest {
    @Test
    fun parallelMapIsRespectingFlowSequence() = runTest {
        val flowOfDelay = flowOf<Long>(200, 1000, 400, 100, 300)

        val collectedFlow = flowOfDelay.parallelMap {
            delay(it)
            it
        }.toList()

        assertContentEquals(flowOfDelay.toList(), collectedFlow)
    }

    @Test
    fun parallelMapIsExecutingOutOfOrder() = runTest {
        val flowOfValues = flowOf<Long>(200, 1000, 400, 100, 300)

        val processedFlow = mutableListOf<Long>()

        flowOfValues.parallelMap {
            delay(it)
            processedFlow.add(it)
        }.toList()

        val isSorted = processedFlow.zipWithNext { a, b -> a <= b }.all { true }

        assert(isSorted)
    }
}