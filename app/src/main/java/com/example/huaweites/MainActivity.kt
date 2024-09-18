package com.example.huaweites

import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Timer
import java.util.TimerTask


class MainActivity : AppCompatActivity() {

    private lateinit var refreshBtn: MaterialButton
    private lateinit var jokeText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        refreshBtn = findViewById(R.id.btnRefresh)
        jokeText = findViewById(R.id.tvJoke)
        refreshBtn.setOnClickListener {
            getJoke()
        }
        Timer().schedule(object : TimerTask() {
            override fun run() {
                getJoke()
            }
        }, 0, 1000*60*5)
    }

    private fun getJoke() {
        lifecycleScope.launch(Dispatchers.IO) {
            var httpURLConnection: HttpURLConnection? = null
            try {
                val url = URL("https://api.chucknorris.io/jokes/random")
                httpURLConnection = url.openConnection() as HttpURLConnection
                if (httpURLConnection.responseCode != 200) {
                    throw IOException("The error from the server is $httpURLConnection.responseCode")
                }
                val bufferedReader = BufferedReader(InputStreamReader(httpURLConnection.inputStream))
                val jsonStringHolder = StringBuilder()
                while (true) {
                    val readLine = bufferedReader.readLine() ?: break
                    jsonStringHolder.append(readLine)
                }
                val joke = JSONObject(jsonStringHolder.toString())["value"] as? String
                withContext(Dispatchers.Main) {
                    jokeText.text = joke
                }
            } catch (ioexception: IOException) {
                Log.e(this.javaClass.name, ioexception.message.toString())
            } finally {
                httpURLConnection?.disconnect()
            }
        }
    }
}