package com.damiandantas.daylighthabits.utils

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Test
import kotlin.test.assertContentEquals

class AsyncTest {
    @Test
    fun `parallelMap is processing elements in order`() = runTest {
        val flowOfDelay = flowOf<Long>(200, 1000, 400, 100, 300)

        val collectedFlow = flowOfDelay.parallelMap {
            delay(it)
            it
        }.toList()

        assertContentEquals(flowOfDelay.toList(), collectedFlow)
    }

    @Test
    fun `parallelMap is processing elements in multiple coroutines without blocking`() = runTest {
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