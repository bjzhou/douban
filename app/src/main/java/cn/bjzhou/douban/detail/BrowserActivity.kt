package cn.bjzhou.douban.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.webkit.*
import cn.bjzhou.douban.R
import kotlinx.android.synthetic.main.activity_browser.*

/**
 * @User zhoubinjia
 * @Date 2018/06/06
 */
class BrowserActivity : AppCompatActivity() {

    private var currentUrl: String? = ""

    private var customView: View? = null
    private var customCallback: WebChromeClient.CustomViewCallback? = null
    private var defaultOrientation = 0

    private var webviewChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            super.onProgressChanged(view, newProgress)
            if (newProgress == 100) {
                progressBar.visibility = View.GONE
            } else {
                progressBar.visibility = View.VISIBLE
                progressBar.progress = newProgress
            }
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            super.onReceivedTitle(view, title)
        }

        override fun onShowCustomView(view: View, callback: CustomViewCallback) {
            Log.d("BrowserActivity", "onShowCustomView")
            if (customView != null) {
                callback.onCustomViewHidden()
                return
            }
            customView = view
            videoView.visibility = View.VISIBLE
            videoView.addView(customView)
            customCallback = callback
            webView.visibility = View.GONE
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)

            webView.loadUrl("""
                javascript:
                playingVideo.onended = function() {
                    if(playingVideo != undefined) {
                        if(playingVideo.src == src) {
                            HinnkaAPI.notifyVideoEnd();
                            playingVideo = undefined;
                        }
                    }
                };
            """.trimIndent())
        }

        override fun onHideCustomView() {
            Log.d("BrowserActivity", "onHideCustomView")
            webView.visibility = View.VISIBLE
            if (customView == null) return
            videoView.removeAllViews()
            videoView.visibility = View.GONE
            customCallback?.onCustomViewHidden()
            customView = null
            requestedOrientation = defaultOrientation
            window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
        defaultOrientation = requestedOrientation
        currentUrl = intent.dataString
        if (TextUtils.isEmpty(currentUrl)) {
            finish()
            return
        }

        WebView.setWebContentsDebuggingEnabled(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        if (Build.VERSION.SDK_INT >= 21) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptThirdPartyCookies(webView, true)
        }
        webView.addJavascriptInterface(this, "HinnkaAPI")
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    if (url.contains("fantasy") || url.contains("weiyun")) {
                        webView.settings.userAgentString = UA_PHONE
                    } else {
                        webView.settings.userAgentString = UA_PC
                    }
                }
                if (url.startsWith("http") || url.startsWith("https")) {
                    view.loadUrl(url)
                } else{
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    val component = intent.resolveActivity(packageManager)
                    if (component == null || component.packageName == packageName) {
                        view.loadUrl(url)
                    } else {
                        startActivity(intent)
                    }
                }
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
                val js = """javascript:
                    var videos = document.getElementsByTagName('video');
                    for(var i = 0; i < videos.length; i++){
                        var video = videos.item(i);
                        video.onplay = function() {
                            playingVideo = video;
                            src = playingVideo.src;
                            HinnkaAPI.log('onplay');
                            HinnkaAPI.notifyVideoStart();
                        };
                    }
                    var land = document.getElementsByClassName('mod-landscape')[0];
                    if(land != undefined) {
                        land.style.display = 'none'
                    }
                    """
                webView.loadUrl(js)
            }
        }
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            webView.settings.userAgentString = UA_PC
            mouseView.visibility = View.VISIBLE
            mouseView.init(webView)
        }
        webView.webChromeClient = webviewChromeClient
        webView.loadUrl(currentUrl)
    }

    @JavascriptInterface
    fun notifyVideoStart() {
        Log.d("BrowserActivity", "notifyVideoStart")
        if (webView.settings.userAgentString != UA_PC) {
            webView.post {
                webView.loadUrl("""javascript:
                if (playingVideo != undefined) {
                    if (!document.webkitFullScreen && playingVideo.webkitEnterFullscreen) {
                        playingVideo.webkitEnterFullscreen();
                    }
                }""")
            }
        }
    }

    @JavascriptInterface
    fun notifyVideoEnd() {
        Log.d("BrowserActivity", "notifyVideoEnd")
        if (webView.settings.userAgentString != UA_PC) {
            webView.post {
                webviewChromeClient.onHideCustomView()
            }
        }
    }

    @JavascriptInterface
    fun log(log: String) {
        Log.d("BrowserActivity", log)
    }

    override fun onResume() {
        super.onResume()
        webView.onResume()
    }

    override fun onPause() {
        super.onPause()
        webView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            webView.stopLoading()
            webView.loadUrl("about:blank")
            webView.destroy()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.keyCode == KeyEvent.KEYCODE_BACK) {
            if (event.action == KeyEvent.ACTION_UP) {
                if (customView != null) {
                    webviewChromeClient.onHideCustomView()
                    return true
                } else {
                    if (webView.canGoBack()) {
                        webView.goBack()
                        return true
                    }
                }
            }
        } else if (mouseView.visibility == View.VISIBLE) {
            return mouseView.onDpadClicked(event)
        }
        return super.dispatchKeyEvent(event)
    }

    companion object {

        private val UA_PHONE = "Mozilla/5.0 (Linux; Android 8.0; Nexus 6P Build/OPP3.170518.006) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.121 Mobile Safari/537.36"
        private val UA_PC = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_6) AppleWebKit/603.3.8 (KHTML, like Gecko) Version/10.1.2 Safari/603.3.8"


        fun loadUrl(context: Context, url: String) {
            val intent = Intent(context, BrowserActivity::class.java)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        }
    }
}