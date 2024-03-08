//package com.example.cropguardcameraapp
//
//import android.content.Intent
//import android.graphics.BitmapFactory
//import android.os.Bundle
//import android.widget.Button
//import android.widget.ImageView
//import androidx.appcompat.app.AppCompatActivity
//import java.io.IOException
//
//class StartUp : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.main_page)
//
//        val logoView: ImageView = findViewById(R.id.logoView)
//        val startButton: Button = findViewById(R.id.loginButton)
//
//        // Load logo image from assets folder
//        try {
//            // Read the image file from assets
//            val inputStream = assets.open("cropguard-high-resolution-logo-transparent.png")
//            // Decode the input stream into a bitmap
//            val bitmap = BitmapFactory.decodeStream(inputStream)
//            // Set the bitmap to the ImageView
//            logoView.setImageBitmap(bitmap)
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//        // Set click listener for the Start button
//        startButton.setOnClickListener {
//            // Navigate to MainActivity
//            startActivity(Intent(this, MainActivity::class.java))
//            // Finish current activity
//            finish()
//        }
//    }
//}


package com.example.cropguardcameraapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException
import kotlin.system.exitProcess

class StartUp : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var exitPage: Button
    private lateinit var sendToPotato: Button
    private lateinit var sendToTea: Button
    private lateinit var sendToPaddy: Button

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

    }

}