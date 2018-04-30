package cn.bjzhou.douban.search

import android.arch.lifecycle.Observer
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.view.MenuItem
import android.view.View
import cn.bjzhou.douban.R
import cn.bjzhou.douban.bean.DoubanItem
import cn.bjzhou.douban.extension.cancel
import cn.bjzhou.douban.extension.refresh
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
    private val engine = SpiderEngine.instance
    private val adapter = PlayingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }
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
            swipeLayout.cancel()
            adapter.notifyDataSetChanged()
        })

        swipeLayout.setColorSchemeResources(R.color.colorAccent)
        swipeLayout.setOnRefreshListener {
            crawl(false)
        }

        swipeLayout.post {
            if (recyclerView.adapter.itemCount == 0) {
                swipeLayout.refresh()
                crawl(true)
            }
        }
    }

    private fun crawl(useCache: Boolean) {
        engine.crawl(spider, useCache = useCache, error = {
            swipeLayout.cancel()
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