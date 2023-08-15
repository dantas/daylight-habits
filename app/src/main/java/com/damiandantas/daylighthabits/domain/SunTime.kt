package com.damiandantas.daylighthabits.domain

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.round
import kotlin.math.roundToLong
import kotlin.math.sin
import kotlin.math.sqrt

// Code inspired by https://en.wikipedia.org/wiki/Sunrise_equation

data class SunTime(val sunrise: ZonedDateTime, val sunset: ZonedDateTime)
fun calculateSunTime(location: Location, date: LocalDate, zone: ZoneId): SunTime {
    val julianDay = julianDay(date, zone)
    val meanSolarTime = meanSolarTime(julianDay, location)
    val solarMeanAnomaly = solarMeanAnomaly(meanSolarTime)
    val equationOfCenter = equationOfCenter(solarMeanAnomaly)
    val eclipticLongitude = eclipticLongitude(solarMeanAnomaly, equationOfCenter)
    val solarTransit = solarTransit(meanSolarTime, solarMeanAnomaly, eclipticLongitude)
    val declinationOfSun = declinationOfSun(eclipticLongitude)
    val hourAngle = hourAngle(location, declinationOfSun)

    val sunrise = julianToInstant(solarTransit.value - hourAngle.value / 360)
    val sunset = julianToInstant(solarTransit.value + hourAngle.value / 360)

    return SunTime(
        sunrise.atZone(zone),
        sunset.atZone(zone)
    )
}

@JvmInline
value class JulianDay(val value: Double)
private inline fun julianDay(date: LocalDate, zone: ZoneId): JulianDay {
    val epochSecond = date.atStartOfDay(zone).toEpochSecond()
    val julianDate = epochSecond / 86400.0 + 2440587.5
    return JulianDay(
        round(julianDate - 2451545.0 + 0.0009 + 69.184 / 86400.0)
    )
}

@JvmInline
value class MeanSolarTime(val value: Double)
private inline fun meanSolarTime(julianDay: JulianDay, location: Location): MeanSolarTime =
    MeanSolarTime(julianDay.value + 0.0009 - location.longitude / 360.0)

data class SolarMeanAnomaly(val degrees: Double, val radians: Double)
private inline fun solarMeanAnomaly(meanSolarTime: MeanSolarTime): SolarMeanAnomaly {
    val degrees = (357.5291 + 0.98560028 * meanSolarTime.value) % 360
    val radians = Math.toRadians(degrees)
    return SolarMeanAnomaly(degrees, radians)
}

@JvmInline
value class EquationOfCenter(val value: Double)
private inline fun equationOfCenter(solarMeanAnomaly: SolarMeanAnomaly): EquationOfCenter =
    EquationOfCenter(
        1.9148 * sin(solarMeanAnomaly.radians) + 0.02 * sin(2 * solarMeanAnomaly.radians) + 0.0003 * sin(3 * solarMeanAnomaly.radians)
    )

@JvmInline
value class EclipticLongitude(val value: Double)
private inline fun eclipticLongitude(solarMeanAnomaly: SolarMeanAnomaly, equationOfCenter: EquationOfCenter): EclipticLongitude {
    val degrees = (solarMeanAnomaly.degrees + equationOfCenter.value + 180.0 + 102.9372) % 360
    return EclipticLongitude(Math.toRadians(degrees))
}

@JvmInline
value class SolarTransit(val value: Double)
private inline fun solarTransit(meanSolarTime: MeanSolarTime, solarMeanAnomaly: SolarMeanAnomaly, eclipticLongitude: EclipticLongitude): SolarTransit =
    SolarTransit(
        2451545.0 + meanSolarTime.value + 0.0053 * sin(solarMeanAnomaly.radians) - 0.0069 * sin(2 * eclipticLongitude.value)
    )

data class DeclinationOfSun(val sin: Double, val cos: Double)
private inline fun declinationOfSun(eclipticLongitude: EclipticLongitude): DeclinationOfSun {
    val sin = sin(eclipticLongitude.value) * sin(Math.toRadians(23.4397))
    val cos = cos(asin(sin))
    return DeclinationOfSun(sin, cos)
}

@JvmInline
value class HourAngle(val value: Double)
private inline fun hourAngle(location: Location, declinationOfSun: DeclinationOfSun): HourAngle  {
    val some = (sin(Math.toRadians(-0.833 - 2.076 * sqrt(location.altitude) / 60.0)) - sin(Math.toRadians(location.latitude)) * declinationOfSun.sin) / (cos(Math.toRadians(location.latitude)) * declinationOfSun.cos)
    val radians = acos(some) // Possible exception?
    val degrees = Math.toDegrees(radians)
    return HourAngle(degrees)
}

private inline fun julianToInstant(julianTime: Double): Instant {
    val value = (julianTime - 2440587.5) * 86400
    return Instant.ofEpochSecond(floor(value).roundToLong())
}
