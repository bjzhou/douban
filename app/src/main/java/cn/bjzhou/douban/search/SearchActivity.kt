package cn.bjzhou.douban.search

import androidx.lifecycle.Observer
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
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

        val count = if (resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            6
        } else {
            3
        }
        recyclerView.layoutManager = GridLayoutManager(this, count)
        adapter.setHasStableIds(true)
        recyclerView.adapter = adapter

        swipeLayout.setColorSchemeResources(R.color.colorAccent)
        swipeLayout.setOnRefreshListener {
            crawl(false)
        }

        swipeLayout.post {
            if (recyclerView.adapter?.itemCount == 0) {
                swipeLayout.refresh()
                crawl(true)
            }
        }
    }

    private fun crawl(useCache: Boolean) {
        engine.crawl(spider, useCache = useCache, error = {
            swipeLayout.cancel()
        }) { items ->
            adapter.data = items.toMutableList()
            swipeLayout.cancel()
            adapter.notifyDataSetChanged()
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