package com.damiandantas.daylighthabits.alert.system

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AlertSchedulerBootReceiver @Inject constructor() : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.i("wasd", "BOOT RECEIVED")
        // TODO: Reschedule alerts because we lost them after reboot
    }
}

@AndroidEntryPoint
class AlertSchedulerPackageReplaced @Inject constructor() : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("wasd", "PACKAGE REPLACED")
        // TODO: Reschedule alerts because we lost them after reinstall
    }
}