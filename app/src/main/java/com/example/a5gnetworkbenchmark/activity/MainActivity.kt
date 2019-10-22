package com.example.a5gnetworkbenchmark.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.a5gnetworkbenchmark.R
import com.example.a5gnetworkbenchmark.component.asynctask.NetworkBenchmarkTask
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var networkBenchmarkTask: NetworkBenchmarkTask

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        checkStopButton.isEnabled = false

        startButton.setOnClickListener {

            if(!downloadTestSwitch.isChecked && !uploadTestSwitch.isChecked){
                logView.text = "[알림] : 반드시 다운로드나 업로드를 체크를 해주셔야합니다."
                return@setOnClickListener
            }

            networkBenchmarkTask = NetworkBenchmarkTask.Builder()
                .isDownloadTest(downloadTestSwitch.isChecked)
                .isUploadTest(uploadTestSwitch.isChecked)
                .logViewUI(logView)
                .build()

            networkBenchmarkTask.execute()

            startButton.isEnabled = false
            checkStopButton.isEnabled = true
        }

        stopButton.setOnClickListener {
            networkBenchmarkTask.forceStopTest()
            startButton.isEnabled = true
        }

        checkStopButton.setOnClickListener{
            Log.d("test", "벤치마크 강제정지 체크 : ${networkBenchmarkTask.isCancelled}")
            Log.d("test", "벤치마크 async상태 : ${networkBenchmarkTask.status}")
        }

        downloadTestSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && downloadTestSwitch.isChecked) {
                uploadTestSwitch.isChecked = false
            }
        }

        uploadTestSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && uploadTestSwitch.isChecked) {
                downloadTestSwitch.isChecked = false
            }
        }
    }
}
