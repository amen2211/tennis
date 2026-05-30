package com.example

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    
    // Enable edge-to-edge immersive fullscreen mode
    WindowCompat.setDecorFitsSystemWindows(window, false)
    val controller = WindowCompat.getInsetsController(window, window.decorView)
    controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    controller.hide(WindowInsetsCompat.Type.systemBars())
    
    setContent {
      GameWebViewContainer()
    }
  }

  @SuppressLint("SetJavaScriptEnabled")
  @Composable
  fun GameWebViewContainer() {
    val webViewRef = remember { mutableStateOf<WebView?>(null) }

    // Physical back button integration
    BackHandler(enabled = true) {
      val webView = webViewRef.value
      if (webView != null && webView.canGoBack()) {
        webView.goBack()
      } else {
        finish()
      }
    }

    // Pure black backdrop to match the web game style and avoid flashes during load
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color.Black)
    ) {
      AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
          WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
              ViewGroup.LayoutParams.MATCH_PARENT,
              ViewGroup.LayoutParams.MATCH_PARENT
            )
            
            webViewClient = object : WebViewClient() {
              // Standard client for resource loading
            }
            webChromeClient = WebChromeClient()
            
            settings.apply {
              javaScriptEnabled = true
              domStorageEnabled = true
              allowFileAccess = true
              allowContentAccess = true
              mediaPlaybackRequiresUserGesture = false
              databaseEnabled = true
              mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
              useWideViewPort = true
              loadWithOverviewMode = true
              
              cacheMode = WebSettings.LOAD_DEFAULT
            }
            
            // Render fully transparent or matching retro black Canvas
            setBackgroundColor(0xFF000000.toInt())
            
            // Load local bundled retro web asset
            loadUrl("file:///android_asset/index.html")
            
            webViewRef.value = this
          }
        },
        update = { webView ->
          webViewRef.value = webView
        }
      )
    }
  }
}
