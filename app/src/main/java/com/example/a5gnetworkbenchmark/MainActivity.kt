package com.example.a5gnetworkbenchmark

import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.a5gnetworkbenchmark.service.NetworkBenchmarkService
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var networkBenchmarkService: NetworkBenchmarkService
    private var isBind = false
    private var handler:Handler? = null

    private val conn = object : ServiceConnection {

        override fun onServiceConnected(componentName: ComponentName, service: IBinder) {
            val localBinder = service as NetworkBenchmarkService.LocalBinder
            networkBenchmarkService = localBinder.getService()
            isBind = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBind = false
        }
    }

    override fun onStart() {
        super.onStart()
        Intent(this, NetworkBenchmarkService::class.java).also { intent ->
            bindService(intent, conn, Context.BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        unbindService(conn)
        isBind = false
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        @SuppressLint("HandlerLeak")
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                val logString = networkBenchmarkService.getBenchmarkLogger()

                logView.text = logString
            }
        }

        startButton.setOnClickListener {
            val intent = Intent(this, NetworkBenchmarkService::class.java)

            if (!isBind) {
                Log.d("test", "NetworkBenchmarkService에 바인딩을 새로하고 있습니다.")
                bindService(intent, conn, Context.BIND_AUTO_CREATE)
            }

            networkBenchmarkService.startBenchmark(handler!!)
            logView.text = networkBenchmarkService.onClick()
        }

        stopButton.setOnClickListener {
            val intent = Intent(applicationContext, NetworkBenchmarkService::class.java)
            unbindService(conn)
            isBind = false
        }

        downloadTestSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && uploadTestSwitch.isChecked) {
                uploadTestSwitch.isChecked = false
            }

            networkBenchmarkService.activeDownloadTest(true)
        }

        uploadTestSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked && uploadTestSwitch.isChecked) {
                downloadTestSwitch.isChecked = false
            }

            networkBenchmarkService.activeUploadTest(true)
        }
    }
}
