package com.cs407.lab4_milestone1

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private val TAG = "MyActivity"
    private var job: Job? = null
    private lateinit var startButton: Button
    private lateinit var stopButton: Button
    private lateinit var progressText: TextView
    private lateinit var switchButton: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        progressText = findViewById(R.id.downloadProgress)
        switchButton = findViewById(R.id.switchButton)

        progressText.visibility = View.GONE
        stopButton.isEnabled = false

        startButton.setOnClickListener { startDownload() }
        stopButton.setOnClickListener { stopDownload() }

        switchButton.setOnCheckedChangeListener { _, isChecked ->
            Log.d(TAG, if (isChecked) "Switch ON" else "Switch OFF")
        }
    }

    private suspend fun mockFileDownloader() {
        withContext(Dispatchers.Main) {
            startButton.text = "Downloading..."
            progressText.visibility = View.VISIBLE
            progressText.text = "Download Progress 0%"
        }

        for (downloadProgress in 0..100 step 10) {
            delay(1000)

            if (job?.isActive != true) {
                Log.d(TAG, "Download Cancelled")
                withContext(Dispatchers.Main) {
                    progressText.text = "Download Cancelled"
                    resetUI()
                }
                return
            }

            Log.d(TAG, "Download Progress $downloadProgress%")
            withContext(Dispatchers.Main) {
                progressText.text = "Download Progress $downloadProgress%"
            }
        }

        withContext(Dispatchers.Main) {
            progressText.text = "Download Complete"
            resetUI()
        }
    }

    private fun startDownload() {
        if (job == null || job?.isCompleted == true) {
            job = CoroutineScope(Dispatchers.Default).launch {
                mockFileDownloader()
            }
            startButton.isEnabled = false
            stopButton.isEnabled = true
        }
    }

    private fun stopDownload() {
        job?.cancel()
        job = null
        progressText.text = "Download Cancelled"
        progressText.visibility = View.VISIBLE
        resetUI()
    }

    private fun resetUI() {
        startButton.text = "Start"
        startButton.isEnabled = true
        stopButton.isEnabled = false
        job = null
    }
}
