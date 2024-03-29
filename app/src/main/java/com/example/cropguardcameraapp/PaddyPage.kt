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

import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder

import com.example.cropguardcameraapp.ml.RiceModel


class PaddyPage : AppCompatActivity() {
    private lateinit var camera: Button
    private lateinit var gallery: Button
    private lateinit var imageView: ImageView
    private lateinit var result: TextView

    private val imageSize = 224

    private val STORAGE_PERMISSION_CODE = 101
    private val IMAGE_PICK_REQUEST_CODE = 102

    private fun launchImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        intent.type = "image/*"  // Filter to only pick images
        startActivityForResult(intent, IMAGE_PICK_REQUEST_CODE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.paddy_page)
        camera = findViewById(R.id.paddyCam)
        gallery = findViewById(R.id.paddyGal)
        result = findViewById(R.id.paddyRes)
        imageView = findViewById(R.id.paddyImgV)


//        camera.setOnClickListener { view ->
//            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
//                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
//                startActivityForResult(cameraIntent, 3)
//            } else {
//                requestPermissions(arrayOf(Manifest.permission.CAMERA), 100)
//            }
//        }

        camera.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, 3)
        }

        gallery.setOnClickListener { view ->
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                launchImagePicker()
            } else {
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), STORAGE_PERMISSION_CODE)
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK){
            when (requestCode) {
                3 -> {
                    var image = data?.extras?.get("data") as? Bitmap ?: return // Handle case where data or image is null
                    val dimension = kotlin.math.min(image.width, image.height)
                    image = ThumbnailUtils.extractThumbnail(image, dimension, dimension)
                    imageView.setImageBitmap(image)

                    image = Bitmap.createScaledBitmap(image, imageSize, imageSize, false)
                    classifyImage(image)
                }else -> {
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
                    classifyImage(scaledImage)
                }
            }
            }

        }
        super.onActivityResult(requestCode, resultCode, data)
    }


    private fun classifyImage(image: Bitmap) {
        val model = RiceModel.newInstance(applicationContext)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())

        val intValues = IntArray(imageSize * imageSize)
        image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
        var pixel = 0
        for (i in 0 until imageSize) {
            for (j in 0 until imageSize) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16) and 0xFF) * (1f / 255))
                byteBuffer.putFloat(((value shr 8) and 0xFF) * (1f / 255))
                byteBuffer.putFloat((value and 0xFF) * (1f / 255))
            }
        }

        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val confidences = outputFeature0.floatArray

        var maxPos = 0
        var maxConfidence = 0f
        for (i in confidences.indices) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxPos = i
            }
        }
//        val classes = arrayOf("bacterial_leaf_blight", "brown_spot", "normal")
        val classes = arrayOf("bacterial_leaf_blight", "blast", "brown_spot", "normal")
        result.text = classes[maxPos]

        model.close()

    }

}
