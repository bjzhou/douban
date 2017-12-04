package cn.bjzhou.douban.spider

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

/**
 * @author zhoubinjia
 * @date 2017/11/3
 */
interface Spider<out T> {
    val name: String
    val url: String
    fun parse(doc: Document): T
}