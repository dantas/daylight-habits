package com.damiandantas.daylighthabits.modules.alert.schedule

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.damiandantas.daylighthabits.utils.hasLocationPermission
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ReschedulerBroadcastReceiver : BroadcastReceiver() {
    @Inject
    lateinit var rescheduler: AlertRescheduler

    @OptIn(DelicateCoroutinesApi::class)
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED && intent.action != Intent.ACTION_MY_PACKAGE_REPLACED) {
            return
        }

        val pendingResult = goAsync()

        GlobalScope.launch {
            if (context.hasLocationPermission()) rescheduler.reschedule()
            pendingResult.finish()
        }
    }
}