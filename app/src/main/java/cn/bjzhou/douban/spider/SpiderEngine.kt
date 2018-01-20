package cn.bjzhou.douban.spider

import android.util.Log
import cn.bjzhou.douban.spider.cache.SpiderCacheManager
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
class SpiderEngine private constructor() {

    private val httpClient = OkHttpClient()
    private val jobs = mutableListOf<Job>()
    private val cacheManager = SpiderCacheManager(3 * 3600 * 1000L)

    fun <T> crawl(spider: Spider<T>, useCache: Boolean = true, expiredTime: Long = cacheManager.expiredTime, error: (() -> Unit)? = null, action: (T) -> Unit) {
        if (!useCache) {
            crawlNetwork(spider, error, action)
            return
        }
        cacheManager.get(spider.name, expiredTime) { cacheDoc ->
            if (cacheDoc != null) {
                Log.d(spider.name, "get on cache")
                launch {
                    val cacheT = spider.parse(cacheDoc)
                    async(UI) {
                        action.invoke(cacheT)
                    }
                }
                return@get
            }
            crawlNetwork(spider, error, action)
        }
    }

    private fun <T> crawlNetwork(spider: Spider<T>, error: (() -> Unit)? = null, action: (T) -> Unit) {
        Log.d(spider.name, "get on network")
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
                cacheManager.put(spider.name, doc)
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

    private object Holder { val INSTANCE = SpiderEngine() }

    companion object {
        const val UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_0) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.18 Safari/537.36"
        val instance by lazy { Holder.INSTANCE }
    }
}