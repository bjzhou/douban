package cn.bjzhou.douban.spider

import cn.bjzhou.douban.bean.DoubanItem
import org.jsoup.nodes.Document

/**
 * @author zhoubinjia
 * @date 2017/11/3
 */
class UpcomingSpider: Spider<List<DoubanItem>> {
    override val name = "upcoming"
    override val url = "https://movie.douban.com/cinema/later/shanghai/"

    override fun parse(doc: Document): List<DoubanItem> {
        return doc.select("div#showing-soon").select("div.item").map { sel ->
            val intro = sel.select("div.intro")
            val name = intro.select("h3>a").text()
            val url = intro.select("h3>a").attr("href")
            val id = url.split('/')[4]
            val wish = intro.select("li.dt.last>span").text()
            val img = sel.select("a.thumb>img").attr("src")
            DoubanItem(id, name, url, img, false, wish = wish)
        }
    }
}