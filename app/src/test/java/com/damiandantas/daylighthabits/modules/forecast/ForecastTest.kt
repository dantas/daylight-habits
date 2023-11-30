package com.damiandantas.daylighthabits.modules.forecast

import com.damiandantas.daylighthabits.modules.Location
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

class TestSunTime {
    @Test
    fun sunTimeFlorianopolis() {
        val localDate = LocalDate.of(2023, 11, 23)
        val zoneId = ZoneId.of("America/Sao_Paulo")
        val location = Location(-27.593947383922217, -48.5660607599406, 30.0)

        val sut = calculateForecast(location, localDate, zoneId)

        val expected = Forecast(
            sunrise = ZonedDateTime.of(localDate, LocalTime.of(5, 12, 0), zoneId),
            sunset = ZonedDateTime.of(localDate, LocalTime.of(18, 50, 0), zoneId)
        )

        assertIsRoughlyEqual(expected = expected, actual = sut)
    }

    @Test
    fun sunTimeLondon() {
        val localDate = LocalDate.of(2023, 8, 3)
        val zoneId = ZoneId.of("Europe/London")
        val location = Location(51.507369358115945, -0.12775781095086025, 11.0)

        val sut = calculateForecast(location, localDate, zoneId)

        val expected = Forecast(
            ZonedDateTime.of(localDate, LocalTime.of(6, 54, 55), zoneId),
            ZonedDateTime.of(localDate, LocalTime.of(17, 48, 32), zoneId)
        )

        assertIsRoughlyEqual(expected = expected, actual = sut)
    }
}

private fun assertIsRoughlyEqual(expected: Forecast, actual: Forecast): Boolean =
    assertIsRoughlyEqual(expected.sunrise, actual.sunrise) &&
            assertIsRoughlyEqual(expected.sunset, actual.sunset)

private fun assertIsRoughlyEqual(expected: ZonedDateTime, actual: ZonedDateTime): Boolean =
    Duration.between(expected, actual) < Duration.ofMinutes(5)
