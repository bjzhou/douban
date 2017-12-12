package cn.bjzhou.douban.spider

import android.util.Log
import cn.bjzhou.douban.bean.ItemDetail
import org.jsoup.nodes.Document

/**
 * @author zhoubinjia
 * @date 2017/12/6
 */
class DetailSpider(override val url: String) : Spider<ItemDetail> {
    override val name = "detail"

    override fun parse(doc: Document): ItemDetail {
        val detail = ItemDetail()
        detail.id = url.split("/")[4]
        detail.name = doc.select("div#content>h1>span")[0].text()
        detail.coverUrl = doc.select("a.nbgnbg>img").attr("src")
        val info = doc.select("div#info")
        val attrs = info.select("span.attrs")
        if (attrs.size > 0) {
            val directors = attrs[0].select("a").map { it.text() }
            detail.director = "导演: " + directors.joinToString("/")
        }
        if (attrs.size > 1) {
            val writers = attrs[1].select("a").map { it.text() }
            detail.writer = "编剧: " + writers.joinToString("/")
        }
        if (attrs.size > 2) {
            val actors = attrs[2].select("span:not([style='display: none;'])>a").map { it.text() }
            detail.actor = "主演: " + actors.joinToString("/")
        }
        val genres = info.select("span[property='v:genre']").map { it.text() }
        detail.genre = "类型: " + genres.joinToString("/")
        detail.country = "制片国家: " + info.select("span:contains(制片国家)")[0].nextSibling().toString().trim()
        detail.language = "语言: " + info.select("span:contains(语言)")[0].nextSibling().toString().trim()
        val releaseDates = info.select("span[property='v:initialReleaseDate']").map { it.text() }
        detail.releaseDate = "上映/首播: " + releaseDates.joinToString("/")
        detail.runTime = "片长: " + info.select("span[property='v:runtime']").text()
        val episode = info.select("span:contains(集数)")
        if (episode.size != 0) {
            detail.episode = "集数: " + episode[0].nextSibling().toString().trim()
        }
        val episodeTime = info.select("span:contains(单集片长)")
        if (episodeTime.size != 0) {
            detail.episodeTime = "单集片长: " + episodeTime[0].nextSibling().toString().trim()
        }
        detail.rating = "评分: " + doc.select("strong.ll.rating_num").text()
        detail.summary = doc.select("span[property='v:summary']").text()
        val relatedPicCss = doc.select("ul.related-pic-bd>li>a:not(.related-pic-video)>img")
        detail.relatedPics = relatedPicCss.map { it.attr("src") }
        detail.hotComments = doc.select("div.comment").map { el ->
            val user = el.select("span.comment-info>a").text().trim()
            val time = el.select("span.comment-time").text().trim()
            val text = el.select("p").text().trim()
            "<font color='#aaaaaa'>$user $time</font><br>$text"
        }
        return detail
    }

}