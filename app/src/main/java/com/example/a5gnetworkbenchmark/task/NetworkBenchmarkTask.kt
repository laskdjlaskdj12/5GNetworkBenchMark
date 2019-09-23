package com.example.a5gnetworkbenchmark.task

import android.os.AsyncTask
import android.util.Log
import android.widget.TextView
import com.example.a5gnetworkbenchmark.utils.BenchmarkUtils
import fr.bmartel.speedtest.SpeedTestReport
import fr.bmartel.speedtest.model.SpeedTestError
import fr.bmartel.speedtest.inter.ISpeedTestListener
import fr.bmartel.speedtest.SpeedTestSocket
import fr.bmartel.speedtest.model.UploadStorageType
import java.lang.StringBuilder


class NetworkBenchmarkTask: AsyncTask<Void, String, String>(){
    private val speedTestSocket = SpeedTestSocket()
    private val benchmarkLogger = StringBuilder()

    var isDownloadTest:Boolean = false
    var isUploadTest:Boolean = false
    var logViewUI:TextView? = null

    fun activeDownloadTest() {
        isDownloadTest = true
        isUploadTest = false
    }

    fun activeUploadTest() {
        isDownloadTest = false
        isUploadTest = true
    }

    fun forceStopTest(){
        Log.d("test", "=========================== 벤치마크를 중단합니다. ================")

        speedTestSocket.forceStopTask()
        speedTestSocket.shutdownAndWait()
    }

    override fun doInBackground(vararg params: Void): String? {

        speedTestSocket.addSpeedTestListener(object : ISpeedTestListener {

            override fun onCompletion(report: SpeedTestReport) {
                val rateBit = report.transferRateBit.toDouble()
                val rateKiloByte = BenchmarkUtils.bitToKiloBit(rateBit)
                val rateMegaByte = BenchmarkUtils.bitToMegaBit(rateBit)

                if (rateMegaByte < 1) {
                    Log.d("test", "[COMPLETE] rate in : $rateKiloByte Kbps")
                } else {
                    Log.d("test", "[COMPLETE] rate in : $rateMegaByte Mbps")
                }

                benchmarkLogger.append("\n\n\n\n 벤치마크테스트가 완료되었습니다.")

                publishProgress(benchmarkLogger.toString())
            }

            override fun onError(speedTestError: SpeedTestError, errorMessage: String) {
                Log.d("text", "[ERROR] speedTestError by $errorMessage")
                benchmarkLogger.append("[ERROR] speedTestError by $errorMessage")
                publishProgress(benchmarkLogger.toString())
            }

            override fun onProgress(percent: Float, report: SpeedTestReport) {
                benchmarkLogger.clear()

                val rateBit = report.transferRateBit.toDouble()
                val rateKiloByte = BenchmarkUtils.bitToKiloBit(rateBit)
                val rateMegaByte = BenchmarkUtils.bitToMegaBit(rateBit)

                Log.d("test", "[PROGRESS] progress : $percent %")
                if (rateMegaByte < 1) {
                    Log.d("test", "[PROGRESS] rate in : $rateKiloByte Kbps \n")
                    benchmarkLogger.append("[PROGRESS] rate in : $rateKiloByte Kbps \n")
                } else {
                    Log.d("test", "[PROGRESS] rate in : $rateMegaByte Mbps \n")
                    benchmarkLogger.append("[PROGRESS] rate in : $rateMegaByte Mbps \n")
                }

                publishProgress(benchmarkLogger.toString())
            }
        })

        speedTestSocket.uploadStorageType = UploadStorageType.FILE_STORAGE
        if (isDownloadTest == true) {
            Log.d("test", "다운로드 테스트를 시작합니다.")
            speedTestSocket.startDownload("http://ipv4.ikoula.testdebit.info/1G.iso")
        } else {
            Log.d("test", "업로드 테스트를 시작합니다.")
            speedTestSocket.startUpload("http://ipv4.ikoula.testdebit.info/", 99000000)
        }

        return null
    }

    override fun onProgressUpdate(vararg values: String?) {

        if(logViewUI == null){
            return
        }

        for (log in values){
            logViewUI!!.text = log
        }

        //UI 스레드에 로그문을 업데이트함
        super.onProgressUpdate(*values)
    }

    override fun onCancelled() {
        Log.d("test", "=========================== 벤치마크를 중단합니다. ================")

        speedTestSocket.forceStopTask()
        speedTestSocket.shutdownAndWait()

        //테스트가 중단되더라도 테스트 결과값이 나오므로 사용자 UI에 표시를 해야함
        super.onCancelled()
    }

}