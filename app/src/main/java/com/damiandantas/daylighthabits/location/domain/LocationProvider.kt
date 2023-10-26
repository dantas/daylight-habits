package com.damiandantas.daylighthabits.location.domain

data class Location(val latitude: Double, val longitude: Double, val altitude: Double)

interface LocationProvider {
    suspend fun currentLocation(): Location
}
