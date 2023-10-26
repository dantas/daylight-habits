package com.damiandantas.daylighthabits

import android.app.Application
import com.damiandantas.daylighthabits.modules.alert.domain.AlertRescheduler
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MainApplication : Application() {
    @Inject
    lateinit var rescheduler: AlertRescheduler

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()

        GlobalScope.launch {
            rescheduler.reschedule()
        }
    }
}
