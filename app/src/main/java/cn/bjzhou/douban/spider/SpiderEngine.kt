package cn.bjzhou.douban.spider

import android.util.Log
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup

/**
 * @author zhoubinjia
 * @date 2017/11/3
 */
class SpiderEngine {

    private val httpClient = OkHttpClient()
    private val jobs = mutableListOf<Job>()

    fun <T> crawl(spider: Spider<T>, error: (() -> Unit)? = null, action: (T) -> Unit) {
        val job = launch {
            val req = Request.Builder()
                    .url(spider.url)
                    .header("User-Agent", UA)
                    .build()
            val res = try {
                httpClient.newCall(req).execute()
            } catch (e: Exception) {
                Log.e("SpiderEngine", "http exception", e)
                null
            }
            val body = res?.body()?.string()
            if (body != null) {
                val doc = Jsoup.parse(body)
                val t = spider.parse(doc)
                async(UI) {
                    action.invoke(t)
                }
            } else {
                async(UI) {
                    error?.invoke()
                }
            }
        }
        jobs.add(job)
    }

    fun stopAll() {
        jobs.forEach { it.cancel() }
    }

    companion object {
        val UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.18 Safari/537.36"
    }
}