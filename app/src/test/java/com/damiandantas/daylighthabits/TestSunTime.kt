package com.damiandantas.daylighthabits

import com.damiandantas.daylighthabits.domain.Forecast
import com.damiandantas.daylighthabits.domain.forecast.calculateSunForecast
import com.damiandantas.daylighthabits.forecast.domain.forecast.Location
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

// -27.706954782768133, -48.68726500504946
// 06:56
// Quarta-feira, 2 de agosto de 2023 (BRT)
class TestSunTime {
    @Test
    fun sunTimeFlorianopolis() {
        val localDate = LocalDate.of(2023, 8, 3)
        val zoneId = ZoneId.of("America/Sao_Paulo")
        val location = Location(-27.593947383922217, -48.5660607599406, 30.0)

        val sut = calculateSunForecast(location, localDate, zoneId)

        val expected = Forecast(
            ZonedDateTime.of(localDate, LocalTime.of(6, 54, 55), zoneId),
            ZonedDateTime.of(localDate, LocalTime.of(17, 48, 32), zoneId)
        )

        assertEquals(
            expected, sut
        )
    }

    @Test
    fun sunTimeLondon() {
        val localDate = LocalDate.of(2023, 8, 3)
        val zoneId = ZoneId.of("Europe/London")
        val location = Location(51.507369358115945, -0.12775781095086025, 11.0)

        val sut = calculateSunForecast(location, localDate, zoneId)

        val expected = Forecast(
            ZonedDateTime.of(localDate, LocalTime.of(6, 54, 55), zoneId),
            ZonedDateTime.of(localDate, LocalTime.of(17, 48, 32), zoneId)
        )

        // dias errados
        // criar projeto e enviar para github
        // pensar em nome do app

        assertEquals(
            expected, sut
        )
    }
}