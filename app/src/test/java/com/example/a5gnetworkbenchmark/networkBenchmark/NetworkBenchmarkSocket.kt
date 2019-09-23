package com.example.a5gnetworkbenchmark.networkBenchmark

import android.util.Log
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.model.SpeedTestError
import fr.bmartel.speedtest.model.UploadStorageType
import org.junit.Test

class NetworkBenchmarkSocket {

    private val SPEED_TEST_URL = "http://ipv4.ikoula.testdebit.info/1G.iso"
    private val SOCKET_TIME_OUT = 1500

    inner class SpeedTestListener : ISpeedTestListener {
        override fun onCompletion(report: SpeedTestReport) {
            // called when download/upload is finished
            println("[COMPLETED] rate in octet/s : " + report.transferRateOctet)
            println("[COMPLETED] rate in bit/s   : " + report.transferRateBit)
        }

        override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
            // called when a download/upload error occur
            println("[PROGRESS progress")
        }

        override fun onProgress(percent: Float, report: SpeedTestReport) {
            // called to notify download/upload progress
            println("[PROGRESS] progress : $percent%")
            println("[PROGRESS] rate in octet/s : " + report.transferRateOctet)
            println("[PROGRESS] rate in bit/s   : " + report.transferRateBit)
        }
    }

    @Test
    fun startuploadTest() {
        val speedTestSocket = SpeedTestSocket()
        speedTestSocket.socketTimeout = SOCKET_TIME_OUT
        speedTestSocket.addSpeedTestListener(SpeedTestListener())
        speedTestSocket.uploadStorageType = UploadStorageType.FILE_STORAGE
        speedTestSocket.startDownload(SPEED_TEST_URL)
        Thread.sleep(4000)

        //스피드테스트들을 아예 셧다운 시킴
        println("셧다운을 시킵니다.")
        speedTestSocket.forceStopTask()
        speedTestSocket.shutdownAndWait()
        println("셧다운이 끝났습니다.")
        println("테스트가 끝났습니다.")
    }
}