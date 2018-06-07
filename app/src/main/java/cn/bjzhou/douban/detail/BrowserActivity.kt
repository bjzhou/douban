package cn.bjzhou.douban.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import cn.bjzhou.douban.R
import cn.bjzhou.douban.tv.TcMouseManager
import kotlinx.android.synthetic.main.activity_browser.*

/**
 * @User zhoubinjia
 * @Date 2018/06/06
 */
class BrowserActivity : AppCompatActivity() {

    private var currentUrl: String? = ""
    private var mouseManager = TcMouseManager()

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_browser)
        currentUrl = intent.dataString
        if (TextUtils.isEmpty(currentUrl)) {
            finish()
            return
        }

        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.allowFileAccessFromFileURLs = true
        webView.settings.allowUniversalAccessFromFileURLs = true
        if (Build.VERSION.SDK_INT >= 21) {
            webView.settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptThirdPartyCookies(webView, true)
        }
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                super.onPageFinished(view, url)
            }
        }
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            webView.settings.userAgentString = "Mozilla/5.0(iPad; U; CPU iPhone OS 3_2 like Mac OS X; en-us) AppleWebKit/531.21.10 (KHTML, like Gecko) Version/4.0.4 Mobile/7B314 Safari/531.21.10"
            mouseManager.init(webView, TcMouseManager.MOUSE_TYPE)
            mouseManager.isShowMouse = true
            mouseManager.showMouseView()
        }
        webView.webChromeClient = object : WebChromeClient() {
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
        }
        webView.loadUrl(currentUrl)


    }

    override fun onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack()
        } else {
            super.onBackPressed()
        }
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

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (mouseManager.isShowMouse) {
            return mouseManager.onDpadClicked(event)
        }
        return super.dispatchKeyEvent(event)
    }

    companion object {
        fun loadUrl(context: Context, url: String) {
            val intent = Intent(context, BrowserActivity::class.java)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        }
    }
}