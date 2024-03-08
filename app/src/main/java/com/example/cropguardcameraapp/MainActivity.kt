//package com.example.cropguardcameraapp
//
//import android.app.Activity
//import android.content.Intent
//import android.graphics.Bitmap
//import android.os.Bundle
//import android.provider.MediaStore
//import android.widget.Button
//import android.widget.ImageView
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import kotlin.system.exitProcess
//
//class MainActivity : AppCompatActivity() {
//
//    private val REQUEST_CODE = 22
//    private lateinit var buttonPic: Button
//    private lateinit var imageView: ImageView
//    private lateinit var exitPage: Button
//    private lateinit var sendToProcess: Button
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        buttonPic = findViewById(R.id.buttonCam)
//        imageView = findViewById(R.id.imageview1)
//        exitPage = findViewById(R.id.exitButton)
//
//        buttonPic.setOnClickListener {
//            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//            startActivityForResult(cameraIntent, REQUEST_CODE)
//        }
//
//        exitPage.setOnClickListener {
//            exitProcess(-1)
//        }
//
//        sendToProcess = findViewById(R.id.sendToProcess)
//
//
//        sendToProcess.setOnClickListener {
//            val intent = Intent(this, ProcessPage::class.java)
//            startActivity(intent)
//        }
//
//    }
//
//    @Deprecated("Deprecated in Java")
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//
//        if ((requestCode == this.REQUEST_CODE) && (resultCode == Activity.RESULT_OK)) {
//            val photo = data?.extras?.get("data") as? Bitmap
//            imageView.setImageBitmap(photo)
//        } else {
//            Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//}


package com.example.cropguardcameraapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var startButton: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val logoView: ImageView = findViewById(R.id.logoView)
        startButton = findViewById(R.id.startButton)

        try {
            val inputStream = assets.open("cropguard-high-resolution-logo-transparent.png")

            val bitmap = BitmapFactory.decodeStream(inputStream)

            logoView.setImageBitmap(bitmap)
        } catch (e: IOException) {
            e.printStackTrace()
        }


        startButton.setOnClickListener {
            val intent = Intent(this, StartUp::class.java)
            startActivity(intent)
            finish()
        }
    }
}
