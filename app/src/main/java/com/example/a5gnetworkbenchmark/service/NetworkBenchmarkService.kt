package com.example.a5gnetworkbenchmark.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import java.lang.RuntimeException
import java.lang.StringBuilder
import fr.bmartel.speedtest.utils.SpeedTestUtils



class NetworkBenchmarkService : Service() {

    private val binder = LocalBinder()
    private val benchmarkLogger = StringBuilder()
    var isDownloadTest:Boolean? = null
    var isUploadTest:Boolean? = null

    inner class LocalBinder: Binder(){
        fun getService(): NetworkBenchmarkService = this@NetworkBenchmarkService
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d("test", "onBind()가 호출되었습니다.")
        return binder
    }

    override fun onCreate() {
        Log.d("test", "onCreate가 호출되었습니다.")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("test", "onstartCommand가 호출되었습니다.")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.d("test", "onDestory가 호출되었습니다.")
    }

    fun onClick():String{
        return "네트워크 속도측정을 시작합니다."
    }

    fun activeDownloadTest(active:Boolean){
        isUploadTest = !active
        isDownloadTest = active
    }

    fun activeUploadTest(active: Boolean){
        isDownloadTest = !active
        isUploadTest = active
    }

    fun startBenchmark(){
        //벤치마크를 시작함
        val speedTest = SpeedTestSocket()

        speedTest.addSpeedTestListener(object: ISpeedTestListener{
            override fun onError(speedTestError: SpeedTestError?, errorMessage: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onProgress(percent: Float, report: SpeedTestReport?) {
                if(report == null){
                    throw RuntimeException("speedtest report is Empty")
                }

                benchmarkLogger.clear()

                val rateKiloByte = report.transferRateBit.toDouble() / (8 * 1024)
                val rateMegaByte = report.transferRateBit.toDouble() / (8 * 1024 * 1024)

                Log.d("test", "[PROGRESS] progress : $percent %");
                if(rateMegaByte < 1){
                    Log.d("test", "[PROGRESS] rate in : $rateKiloByte Kb/s")
                    benchmarkLogger.append("[PROGRESS] rate in : $rateKiloByte Kb/s")
                }else{
                    Log.d("test", "[PROGRESS] rate in : $rateMegaByte Mb/s")
                    benchmarkLogger.append("[PROGRESS] rate in : $rateMegaByte Kb/s")
                }
            }

            override fun onCompletion(report: SpeedTestReport?) {
                if(report == null){
                    throw RuntimeException("speedtest report is Empty")
                }

                val rateKiloByte = report.transferRateBit.toDouble() / (8 * 1024)
                val rateMegaByte = report.transferRateBit.toDouble() / (8 * 1024 * 1024)

                if(rateMegaByte < 1){
                    Log.d("test", "[PROGRESS] rate in : $rateKiloByte Kb/s")
                }else{
                    Log.d("test", "[PROGRESS] rate in : $rateMegaByte Mb/s")
                }
            }
        })

        if(isDownloadTest == true) {
            Log.d("test", "다운로드 테스트를 시작합니다.")
            speedTest.startDownload("ftp://speedtest.tele2.net/100MB.zip")
        }else {
            Log.d("test", "업로드 테스트를 시작합니다.")
            val fileName = SpeedTestUtils.generateFileName() + ".txt"
            speedTest.startUpload("ftp://speedtest.tele2.net/upload/$fileName", 1000000)
        }
    }
}
