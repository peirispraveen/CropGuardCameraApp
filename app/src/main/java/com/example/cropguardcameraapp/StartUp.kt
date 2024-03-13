package com.example.cropguardcameraapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import kotlin.system.exitProcess

class StartUp : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var exitPage: Button
    private lateinit var sendToPotato: Button
    private lateinit var sendToTea: Button
    private lateinit var sendToPaddy: Button
    private lateinit var sendToChat: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_page)

        imageView = findViewById(R.id.imageview1)
        exitPage = findViewById(R.id.exitButton)

        try {
            val inputStream = assets.open("favicon1transparent-modified.png")

            val bitmap = BitmapFactory.decodeStream(inputStream)

            imageView.setImageBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        exitPage.setOnClickListener {
            exitProcess(-1)
        }

        sendToPotato = findViewById(R.id.sendToProcess)
        sendToPaddy = findViewById(R.id.sendToPaddy)
        sendToTea = findViewById(R.id.sendToTea)
        sendToChat = findViewById(R.id.sendToChat)

        sendToPotato.setOnClickListener {
            val intent = Intent(this, ProcessPage::class.java)
            startActivity(intent)
        }

        sendToPaddy.setOnClickListener {
            val intent = Intent(this, PaddyPage::class.java)
            startActivity(intent)
        }

        sendToTea.setOnClickListener {
            val intent = Intent(this, TeaPage::class.java)
            startActivity(intent)
        }
        sendToChat.setOnClickListener {
            val intent = Intent(this, Chatbot::class.java)
            startActivity(intent)
        }

    }

}