package com.damiandantas.daylighthabits.modules.forecast

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.assertEquals

class UpcomingForecastTest {
    @Test
    fun `current time is before sunrise`() = runTest {
        val clock = stubTodayClock(6, 12)

        val sut = UpcomingForecast(OnDateForecastStub, clock, StandardTestDispatcher(testScheduler))

        assertEquals(expected = OnDateForecastStub.todayForecast, actual = sut.get())
    }

    @Test
    fun `current time is between sunrise and sunset`() = runTest {
        val clock = stubTodayClock(12, 8)

        val sut = UpcomingForecast(OnDateForecastStub, clock, StandardTestDispatcher(testScheduler))

        val expected = Forecast(
            sunrise = OnDateForecastStub.tomorrowForecast.sunrise,
            sunset = OnDateForecastStub.todayForecast.sunset
        )

        assertEquals(expected, actual = sut.get())
    }

    @Test
    fun `current time is after sunset`() = runTest {
        val clock = stubTodayClock(21, 5)

        val sut = UpcomingForecast(OnDateForecastStub, clock, StandardTestDispatcher(testScheduler))

        assertEquals(expected = OnDateForecastStub.tomorrowForecast, actual = sut.get())
    }
}

private val zoneId = ZoneId.of("America/Sao_Paulo")
private val todayDate = LocalDate.of(2023, 11, 23)
private val tomorrowDate = todayDate.plusDays(1)

private object OnDateForecastStub : OnDateForecast {
    val todayForecast = Forecast(
        sunrise = ZonedDateTime.of(
            todayDate,
            LocalTime.of(9, 0),
            zoneId
        ),
        sunset = ZonedDateTime.of(
            todayDate,
            LocalTime.of(18, 0),
            zoneId
        )
    )

    val tomorrowForecast = Forecast(
        sunrise = ZonedDateTime.of(
            tomorrowDate,
            LocalTime.of(8, 0),
            zoneId
        ),
        sunset = ZonedDateTime.of(
            tomorrowDate,
            LocalTime.of(19, 0),
            zoneId
        )
    )

    override suspend fun onDate(date: LocalDate): Forecast =
        when (date) {
            todayDate -> todayForecast
            tomorrowDate -> tomorrowForecast
            else -> throw AssertionError("A different date was provided $date")
        }
}

private fun stubTodayClock(hour: Int, minute: Int): Clock {
    val zonedDateTime = ZonedDateTime.of(todayDate, LocalTime.of(hour, minute), zoneId)
    val now = Instant.from(zonedDateTime)
    return Clock.fixed(now, zoneId)
}