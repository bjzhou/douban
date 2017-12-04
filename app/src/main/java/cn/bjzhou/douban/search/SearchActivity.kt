package cn.bjzhou.douban.search

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import cn.bjzhou.douban.R
import cn.bjzhou.douban.bean.DoubanItem
import cn.bjzhou.douban.playing.PlayingAdapter
import cn.bjzhou.douban.spider.SearchSpider
import cn.bjzhou.douban.spider.Spider
import cn.bjzhou.douban.spider.SpiderEngine
import kotlinx.android.synthetic.main.activity_search.*

/**
 * @author zhoubinjia
 * @date 2017/11/7
 */
class SearchActivity : AppCompatActivity() {

    private lateinit var keyword: String
    private lateinit var spider: Spider<List<DoubanItem>>
    private val engine = SpiderEngine()
    private val adapter = PlayingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        if (intent == null || intent.extras == null) {
            finish()
            return
        }
        keyword = intent.getStringExtra("keyword")
        spider = SearchSpider(keyword)
        toolbar.title = "搜索\"$keyword\""
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        recyclerView.layoutManager = GridLayoutManager(this, 3)
        recyclerView.adapter = adapter

        adapter.data.observe(this, Observer {
            swipeLayout.isRefreshing = false
            adapter.notifyDataSetChanged()
        })

        swipeLayout.setOnRefreshListener {
            crawl()
        }

        swipeLayout.post {
            if (recyclerView.adapter.itemCount == 0) {
                swipeLayout.isRefreshing = true
                crawl()
            }
        }
    }

    private fun crawl() {
        engine.crawl(spider, {
            swipeLayout.isRefreshing = false
        }) { items ->
            adapter.data.value = items
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}