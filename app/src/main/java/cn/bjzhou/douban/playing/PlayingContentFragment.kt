package cn.bjzhou.douban.playing

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bjzhou.douban.R
import cn.bjzhou.douban.bean.DoubanItem
import cn.bjzhou.douban.spider.PlayingSpider
import cn.bjzhou.douban.spider.Spider
import cn.bjzhou.douban.spider.SpiderEngine
import cn.bjzhou.douban.spider.UpcomingSpider
import cn.bjzhou.douban.wrapper.BaseFragment
import kotlinx.android.synthetic.main.layout_playing.*

/**
 * @author zhoubinjia
 * @date 2017/11/3
 */
class PlayingContentFragment: BaseFragment() {

    private var position = 0
    private val adapter = PlayingAdapter()
    private val engine = SpiderEngine()
    private lateinit var spider: Spider<List<DoubanItem>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments.getInt("position", 0)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        spider = if (position == 0) {
            PlayingSpider()
        } else {
            UpcomingSpider()
        }
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = adapter

        adapter.data.observe(this, Observer {
            swipeLayout.isRefreshing = false
            adapter.notifyDataSetChanged()
        })

        swipeLayout.setOnRefreshListener {
            crawl()
        }
    }

    private fun crawl() {
        engine.crawl(spider, {
            swipeLayout.isRefreshing = false
        }) { items ->
            adapter.data.value = items
        }
    }

    override fun onFragmentVisible() {
        swipeLayout.post {
            if (recyclerView.adapter.itemCount == 0) {
                swipeLayout.isRefreshing = true
                crawl()
            }
        }
    }

    companion object {
        fun newInstance(position: Int): PlayingContentFragment {
            val fragment = PlayingContentFragment()
            fragment.logTag = if (position == 0) {
                "playing"
            } else {
                "upcoming"
            }
            val bundle = Bundle()
            bundle.putInt("position", position)
            fragment.arguments = bundle
            return fragment
        }
    }
}