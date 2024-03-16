package com.example.cropguardcameraapp

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.ThumbnailUtils
import android.provider.MediaStore

import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import org.tensorflow.lite.support.image.TensorImage
import com.example.cropguardcameraapp.ml.BestFloat32
import org.tensorflow.lite.Interpreter

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder



class TeaPage : AppCompatActivity() {
    private lateinit var camera: Button
    private lateinit var gallery: Button
    private lateinit var imageView: ImageView
    private lateinit var result: TextView

    private lateinit var yolov8detector: Yolov8Detect

    private val imageSize = 640

    private val STORAGE_PERMISSION_CODE = 101
    private val IMAGE_PICK_REQUEST_CODE = 102

    private fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"  // Filter to only pick images
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tea_page)
        camera = findViewById(R.id.teaCam)
        gallery = findViewById(R.id.teaGal)
        result = findViewById(R.id.teaRes)
        imageView = findViewById(R.id.teaImgV)


        yolov8detector = Yolov8Detect()
        yolov8detector.modelFile = "best_float16.tflite" // Setting the model file name
        yolov8detector.initialModel(this) // Initialize the model

        camera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 3)
        }

        gallery.setOnClickListener {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                3 -> {
                    var image = data?.extras?.get("data") as? Bitmap ?: return // Handle case where data or image is null
                    val dimension = kotlin.math.min(image.width, image.height)
                    image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
                    imageView.setImageBitmap(image)

                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
                    val recog: ArrayList<Recognition> = yolov8detector.detect(image)

                    for (recognition in recog) {
                        if (recognition.confidence > 0.4) {
                            result.text = recognition.labelName
                        }
                    }
//                    classifyImage(image)
                }
                else -> {
                    val selectedImageUri = data?.data ?: return
                    var image: Bitmap? = null
                    try {
                        image = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    image?.let {
                        imageView.setImageBitmap(it)
                        val scaledImage = Bitmap.createScaledBitmap(it, imageSize, imageSize, false)

                        val recog: ArrayList<Recognition> = yolov8detector.detect(scaledImage)

                        for (recognition in recog) {
                            if (recognition.confidence > 0.4) {
                                result.text = recognition.labelName
                            }
                        }

//                        yolov8detector.detect(scaledImage)
//                        classifyImage(scaledImage)
                    }
                }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }

//    private fun classifyImage(image: Bitmap): List<String> {
//        val model = BestFloat32.newInstance(applicationContext)
//
//        val resizedImage = Bitmap.createScaledBitmap(image, 640, 640, true)
//        val tensorImage = TensorImage.fromBitmap(resizedImage)
//
//        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 640, 640, 3), DataType.FLOAT32)
//        tensorImage.load(inputFeature0)
//
//        val outputFeature0 = model.getOutputTensor(0)
//
//        model.run(inputFeature0.buffer, outputFeature0.buffer)
//
//        val output = outputFeature0.floatArray.map { it.toInt() }
//        val classes = listOf("brown_blight", "gray_blight", "healthy", "white_spot")
//        return output.map { classes[it] }
//    }

}
