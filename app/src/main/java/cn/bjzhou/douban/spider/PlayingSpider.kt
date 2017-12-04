package cn.bjzhou.douban.spider

import cn.bjzhou.douban.bean.DoubanItem
import org.jsoup.nodes.Document

/**
 * @author zhoubinjia
 * @date 2017/11/3
 */
class PlayingSpider: Spider<List<DoubanItem>> {
    override val name = "playing"
    override val url = "https://movie.douban.com/cinema/nowplaying/shanghai/"

    override fun parse(doc: Document): List<DoubanItem> {
        return doc.select("div#nowplaying")
                .select("li.list-item")
                .map { sel ->
                    val id = sel.attr("id")
                    val poster = sel.select("li.poster")
                    val name = sel.attr("data-title")
                    val score = sel.attr("data-score").toFloat()
                    val url = poster.select("a").attr("href")
                    val img = poster.select("a>img").attr("src")
                    DoubanItem(id, name, url, img, score)
                }.filter { (it.score ?: 0f) > 0 }
    }
}