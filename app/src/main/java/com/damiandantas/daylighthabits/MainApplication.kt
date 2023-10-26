package com.damiandantas.daylighthabits

import android.app.Application
import com.damiandantas.daylighthabits.modules.alert.domain.AlertRescheduler
import com.damiandantas.daylighthabits.utils.di.DispatcherDefault
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MainApplication @Inject constructor(
    private val rescheduler: AlertRescheduler,
    @DispatcherDefault private val dispatcherDefault: CoroutineScope
): Application() {
    override fun onCreate() {
        super.onCreate()

        dispatcherDefault.launch {
            rescheduler.reschedule()
        }
    }
}