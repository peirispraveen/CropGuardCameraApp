package com.example.cropguardcameraapp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.cropguardcameraapp.R

class ProcessPage : Activity() {

    private lateinit var webView: WebView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.process_pic)

        webView = findViewById(R.id.webView)
        webView.settings.javaScriptEnabled = true // Enable JavaScript if necessary for API interaction

        // Replace with your Flask API's URL and handle potential issues
        val apiUrl = try {
            "https://www.stackoverflow.com" // This is an example, replace with your actual URL
        } catch (e: Exception) {
            // Handle exceptions gracefully, e.g., display an error message or retry logic
            "https://www.stackoverflow.com" // Replace with a placeholder or error page URL
        }

        webView.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url != null && url.startsWith("https://www.stackoverflow.com")) {
                    view?.loadUrl(url)
                    return true
                }
                return false
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                Log.e("WebView Error", "Error: $error")
                println("Error")
            }
        }

        webView.loadUrl(apiUrl)
    }
}
