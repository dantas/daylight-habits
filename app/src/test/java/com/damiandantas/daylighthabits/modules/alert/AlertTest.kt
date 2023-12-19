package com.damiandantas.daylighthabits.modules.alert

import com.damiandantas.daylighthabits.modules.forecast.Forecast
import org.junit.Test
import java.time.Duration
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class AlertTest {
    @Test
    fun `sunrise alert is created with the correct time`() {
        val schedule = AlertSchedule(AlertType.SUNRISE, Duration.ofHours(2), true)

        val sut = AlertTime.create(forecast, schedule)

        assertNotNull(sut)
        assertEquals(expected = schedule.type, actual = sut.type)
        assertEquals(expected = forecast.sunrise.minus(schedule.noticePeriod), actual = sut.time)
    }

    @Test
    fun `sunset alert is created with the correct time`() {
        val schedule = AlertSchedule(AlertType.SUNSET, Duration.ofHours(5), true)

        val sut = AlertTime.create(forecast, schedule)

        assertNotNull(sut)
        assertEquals(expected = schedule.type, actual = sut.type)
        assertEquals(expected = forecast.sunset.minus(schedule.noticePeriod), actual = sut.time)
    }

    @Test
    fun `cannot create an alert if the schedule is disabled`() {
        val schedule = AlertSchedule(AlertType.SUNSET, Duration.ofHours(1), false)

        val sut = AlertTime.create(forecast, schedule)

        assertNull(sut)
    }

    @Test
    fun `default AlertSchedule has no duration and is disabled`() {
        val sunriseSchedule = AlertSchedule(AlertType.SUNRISE)
        assertEquals(expected = Duration.ZERO, sunriseSchedule.noticePeriod)
        assertFalse(sunriseSchedule.isEnabled)
    }

    @Test
    fun `Forecast getTime selects the correct time`() {
        assertEquals(expected = forecast.sunrise, actual = forecast.getTime(AlertType.SUNRISE))
        assertEquals(expected = forecast.sunset, actual = forecast.getTime(AlertType.SUNSET))
    }
}

private val forecast = Forecast(
    sunrise = ZonedDateTime.of(
        LocalDate.of(2023, 11, 23),
        LocalTime.of(9, 0),
        ZoneId.of("America/Sao_Paulo")
    ),
    sunset = ZonedDateTime.of(
        LocalDate.of(2023, 11, 23),
        LocalTime.of(18, 0),
        ZoneId.of("America/Sao_Paulo")
    )
)