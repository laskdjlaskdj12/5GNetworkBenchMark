package com.example.a5gnetworkbenchmark.listener

import android.app.Service
import android.content.Intent
import android.os.IBinder
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError

class SpeedTestListener: ISpeedTestListener, Service(){
    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onProgress(percent: Float, report: SpeedTestReport?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onError(speedTestError: SpeedTestError?, errorMessage: String?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onCompletion(report: SpeedTestReport?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}