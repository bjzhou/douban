package cn.bjzhou.douban.spider.cache

import android.util.Base64
import android.util.Log
import cn.bjzhou.douban.App
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import java.io.File

/**
* @User zhoubinjia
* @Date 2018/01/20
*/
class SpiderCacheManager(val expiredTime: Long) {

    private val cacheMap = HashMap<String, SpiderCache>()
    private val cacheDir = App.instance.cacheDir

    fun put(name: String, value: Document) {
        val cache = SpiderCache(System.currentTimeMillis(), value)
        cacheMap[name] = cache
        async {
            putDisk(name, cache)
        }
    }

    fun get(name: String, expiredTime: Long = this.expiredTime, callback: (Document?) -> Unit) {
        val cache = cacheMap[name]
        if (cache == null) {
            launch {
                val res = getDisk(name)
                async(UI) {
                    callback.invoke(res)
                }
            }
            return
        }
        if (System.currentTimeMillis() - expiredTime >= cache.parseTime) {
            Log.d(name, " memory cache expired")
            callback.invoke(null)
        }
        Log.d(name, "get on memory cache")
        callback.invoke(cache.value)
    }

    private fun getDisk(name: String, expiredTime: Long = this.expiredTime): Document? {
        val file = File(cacheDir, name)
        if (file.exists()) {
            if (System.currentTimeMillis() - expiredTime >= file.lastModified()) {
                Log.d(name, "disk cache expired")
                return null
            }
            val str = String(Base64.decode(file.readText(), Base64.URL_SAFE))
            val doc = Jsoup.parse(str)
            cacheMap[name] = SpiderCache(file.lastModified(), doc)
            Log.d(name, "get on disk cache")
            return doc
        }
        return null
    }

    private fun putDisk(name: String, cache: SpiderCache) {
        val file = File(cacheDir, name)
        val base64 = Base64.encodeToString(cache.value.html().toByteArray(), Base64.URL_SAFE)
        file.writeText(base64)
        file.setLastModified(cache.parseTime)
    }

}