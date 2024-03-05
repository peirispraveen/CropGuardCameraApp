package com.example.cropguardcameraapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.tensorflow.lite.Interpreter
import java.nio.channels.FileChannel

class Modeltest : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var classifyButton: Button
    private lateinit var interpreter: Interpreter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.model_layout)

        imageView = findViewById(R.id.imageView)
        classifyButton = findViewById(R.id.classifyButton)

        classifyButton.setOnClickListener {
            openGallery()
        }

        // Load TensorFlow Lite model
        interpreter = Interpreter(loadModelFile())
    }

    private fun loadModelFile(): ByteBuffer {
        val modelFileDescriptor = assets.openFd("sampledata/potato_model.tflite")
        val inputStream = FileInputStream(modelFileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = modelFileDescriptor.startOffset
        val declaredLength = modelFileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
            .order(ByteOrder.nativeOrder())
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_GALLERY) {
            val imageUri = data?.data
            val imageStream = contentResolver.openInputStream(imageUri!!)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            imageView.setImageBitmap(selectedImage)

            // Preprocess the image and run inference
            val scaledBitmap = Bitmap.createScaledBitmap(selectedImage, INPUT_SIZE, INPUT_SIZE, true)
            val inputBuffer = convertBitmapToByteBuffer(scaledBitmap)
            val outputBuffer = ByteBuffer.allocateDirect(OUTPUT_SIZE).order(ByteOrder.nativeOrder())

            interpreter.run(inputBuffer, outputBuffer)

            // Process the output
            val outputArray = outputBuffer.array()
            // Display the result to the user
            // Here you can implement code to show the result on the screen
        }
    }

    private fun convertBitmapToByteBuffer(bitmap: Bitmap): ByteBuffer {
        val byteBuffer = ByteBuffer.allocateDirect(INPUT_SIZE * INPUT_SIZE * PIXEL_SIZE * NUM_CHANNELS)
        byteBuffer.order(ByteOrder.nativeOrder())
        val pixels = IntArray(INPUT_SIZE * INPUT_SIZE)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        var pixel = 0
        for (i in 0 until INPUT_SIZE) {
            for (j in 0 until INPUT_SIZE) {
                val pixelValue = pixels[pixel++]
                byteBuffer.putFloat(((pixelValue shr 16 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((pixelValue shr 8 and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
                byteBuffer.putFloat(((pixelValue and 0xFF) - IMAGE_MEAN) / IMAGE_STD)
            }
        }
        return byteBuffer
    }

    companion object {
        private const val REQUEST_IMAGE_GALLERY = 101
        private const val INPUT_SIZE = 256 // Adjust according to your model's input shape
        private const val NUM_CHANNELS = 3
        private const val PIXEL_SIZE = 4
        private const val IMAGE_MEAN = 127.5f
        private const val IMAGE_STD = 127.5f
        private const val OUTPUT_SIZE = 3 // Adjust according to your model's output size
    }
}
