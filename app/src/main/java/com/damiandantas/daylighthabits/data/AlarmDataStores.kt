package com.damiandantas.daylighthabits.data

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

val Context.sunriseAlarmDataStore: DataStore<AlarmProtoStorage> by dataStore(
    fileName = "sunrise_alarm.pb",
    serializer = AlarmStorageSerializer
)

val Context.sunsetAlarmDataStore: DataStore<AlarmProtoStorage> by dataStore(
    fileName = "sunset_alarm.pb",
    serializer = AlarmStorageSerializer
)

object AlarmStorageSerializer : Serializer<AlarmProtoStorage> {
    override val defaultValue: AlarmProtoStorage = AlarmProtoStorage.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): AlarmProtoStorage =
        try {
            AlarmProtoStorage.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto", exception)
        }

    override suspend fun writeTo(t: AlarmProtoStorage, output: OutputStream) {
        t.writeTo(output)
    }
}