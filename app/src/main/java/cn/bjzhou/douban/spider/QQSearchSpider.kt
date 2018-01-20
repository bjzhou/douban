package cn.bjzhou.douban.spider

import android.text.TextUtils
import org.jsoup.nodes.Document

/**
 * @author zhoubinjia
 * @date 2017/12/12
 */
class QQSearchSpider(val title : String) : Spider<String> {
    override val name: String = "qq_$title"
    override val url = "http://v.qq.com/x/search/?q=$title"

    override fun parse(doc: Document): String {
        val playlist = doc.select("div.result_item")[0].select("div._playlist")
        if (playlist.isNotEmpty()) {
            val url = playlist.select("a.btn_primary").attr("href")
            return if (TextUtils.isEmpty(url)) {
                val a = playlist.select("div.item>a")
                if (a.isEmpty()) {
                    ""
                } else a[0].attr("href")
            } else url
        }
        return ""
    }
}