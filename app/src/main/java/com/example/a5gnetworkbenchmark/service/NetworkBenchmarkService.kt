package com.example.a5gnetworkbenchmark.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.util.Log
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import kotlin.concurrent.thread


class NetworkBenchmarkService : Service() {

    private val binder = LocalBinder()
    private val benchmarkLogger = StringBuilder()
    private var testThread:Thread? = null
    private var speedTest:SpeedTestSocket? = null
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
        isDownloadTest = active
        isUploadTest = !active
    }

    fun activeUploadTest(active: Boolean){
        isDownloadTest = !active
        isUploadTest = active
    }

    fun getBenchmarkLogger():String{
        return benchmarkLogger.toString()
    }

    fun startBenchmark(handler: Handler) {
        //벤치마크를 시작함
        testThread = thread(start = true) {
            speedTest = SpeedTestSocket()
            val threadID = android.os.Process.myTid()

            speedTest?.addSpeedTestListener(object : ISpeedTestListener {
                val listenerThreadID = android.os.Process.myTid()

                override fun onError(speedTestError: SpeedTestError?, errorMessage: String?) {
                    benchmarkLogger.append(errorMessage)
                    handler.sendMessage(Message())
                }

                override fun onProgress(percent: Float, report: SpeedTestReport?) {
                    if (report == null) {
                        throw RuntimeException("speedtest report is Empty")
                    }

                    benchmarkLogger.clear()

                    val rateKiloByte = report.transferRateBit.toDouble() / (1024)
                    val rateMegaByte = report.transferRateBit.toDouble() / (1024 * 1024)

                    Log.d("test [$listenerThreadID]", "[PROGRESS] progress : $percent %")
                    if (rateMegaByte < 1) {
                        Log.d("test [$listenerThreadID]", "[PROGRESS] rate in : $rateKiloByte Kbps \n")
                        benchmarkLogger.append("[PROGRESS] rate in : $rateKiloByte Kbps \n")
                    } else {
                        Log.d("test [$listenerThreadID]", "[PROGRESS] rate in : $rateMegaByte Mbps \n")
                        benchmarkLogger.append("[PROGRESS] rate in : $rateMegaByte Mbps \n")
                    }

                    handler.sendMessage(Message())
                }

                override fun onCompletion(report: SpeedTestReport?) {
                    if (report == null) {
                        throw RuntimeException("speedtest report is Empty")
                    }

                    val rateKiloByte = report.transferRateBit.toDouble() / (1024)
                    val rateMegaByte = report.transferRateBit.toDouble() / (1024 * 1024)

                    if (rateMegaByte < 1) {
                        Log.d("test [$listenerThreadID]", "[COMPLETE] rate in : $rateKiloByte Kbps")
                    } else {
                        Log.d("test [$listenerThreadID]", "[COMPLETE] rate in : $rateMegaByte Mbps")
                    }

                    benchmarkLogger.append("\n\n\n\n 벤치마크테스트가 완료되었습니다.")
                    handler.sendMessage(Message())
                }
            })

            if (isDownloadTest == true) {
                Log.d("test [$threadID]", "다운로드 테스트를 시작합니다.")
                speedTest?.startDownload("http://ipv4.ikoula.testdebit.info/1G.iso")
            } else {
                Log.d("test [$threadID]", "업로드 테스트를 시작합니다.")
                speedTest?.startUpload("http://ipv4.ikoula.testdebit.info/", 99000000)
            }
        }
    }

    fun stopBenchmark() {
        speedTest?.forceStopTask()
        speedTest?.shutdownAndWait()
        testThread?.interrupt()
    }
}
