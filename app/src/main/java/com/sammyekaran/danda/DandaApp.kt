package com.sammyekaran.danda

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.sammyekaran.danda.di.AppModule
import com.sammyekaran.danda.utils.Constants
import com.sammyekaran.danda.utils.SharedPref
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin

class DandaApp : Application(), LifecycleObserver {

    val sharedPref: SharedPref by inject()

    override fun onCreate() {
        super.onCreate()
        startKoin(this, listOf(AppModule.appModule()))
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {

    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
    }

}
