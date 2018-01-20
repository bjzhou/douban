package cn.bjzhou.douban.spider

import cn.bjzhou.douban.bean.DoubanItem
import org.jsoup.nodes.Document

/**
 * @author zhoubinjia
 * @date 2017/11/7
 */
class SearchSpider(keyword: String) : Spider<List<DoubanItem>> {
    override val name = "search_$keyword"
    override val url = "https://www.douban.com/search?cat=1002&q=" + keyword

    override fun parse(doc: Document): List<DoubanItem> {
        return doc.select("div.result-list")
                .select("div.result")
                .map { sel ->
                    val id = sel.select("a.nbg").attr("href").split("%2F")[4]
                    val name = sel.select("div.title>h3>a").text()
                    val score = try {
                        sel.select("span.rating_nums").text().toFloat()
                    } catch (e: Exception) {
                        0f
                    }
                    val url = sel.select("a.nbg").attr("href")
                    val img = sel.select("a.nbg>img").attr("src")
                    DoubanItem(id, name, url, img, score)
                }
    }
}