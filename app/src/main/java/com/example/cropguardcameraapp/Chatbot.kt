package com.example.cropguardcameraapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class Chatbot : AppCompatActivity() {

    private lateinit var messageInput: EditText
    private lateinit var sendButton: Button
    private lateinit var chatMessageContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.chatbot_page)

        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.sendButton)
        chatMessageContainer = findViewById(R.id.chatMessageContainer)

        sendButton.setOnClickListener {
            val userMessage = messageInput.text.toString()
            displayMessage(userMessage, true)
            sendMessageToRasa(userMessage)
            messageInput.setText("") // Clear the input field
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun sendMessageToRasa(userMessage: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val rasaUrl = "http://192.168.8.101:5005/webhooks/rest/webhook"
            val url = URL(rasaUrl)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "POST"
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            val jsonObject = JSONObject().apply {
                put("message", userMessage)
            }

            val outputStreamWriter = OutputStreamWriter(connection.outputStream)
            outputStreamWriter.write(jsonObject.toString())
            outputStreamWriter.flush()

            val responseCode = connection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                val bufferedReader = BufferedReader(InputStreamReader(connection.inputStream))
                val response = StringBuilder()
                var line: String?
                while (bufferedReader.readLine().also { line = it } != null) {
                    response.append(line)
                }
                bufferedReader.close()

                val jsonResponse = JSONArray(response.toString()) // Response is an array
                val botResponse = jsonResponse.getJSONObject(0).optString("text") // Get text from the first object

                withContext(Dispatchers.Main) {
                    displayMessage(botResponse, false)
                }
            }
        }
    }

    private fun displayMessage(message: String, isUserMessage: Boolean) {
        runOnUiThread {
            val textView = TextView(this@Chatbot)
            textView.text = message
            if (isUserMessage) {
                textView.textAlignment = TextView.TEXT_ALIGNMENT_TEXT_END
            }
            chatMessageContainer.addView(textView)
        }
    }
}
