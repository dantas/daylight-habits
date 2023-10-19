package com.damiandantas.daylighthabits.forecast.domain

data class Location(val latitude: Double, val longitude: Double, val altitude: Double)

interface LocationProvider {
    suspend fun currentLocation(): Location
}
