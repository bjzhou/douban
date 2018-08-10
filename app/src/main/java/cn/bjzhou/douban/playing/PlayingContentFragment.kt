package cn.bjzhou.douban.playing

import androidx.lifecycle.Observer
import android.content.res.Configuration
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bjzhou.douban.R
import cn.bjzhou.douban.bean.DoubanItem
import cn.bjzhou.douban.extension.cancel
import cn.bjzhou.douban.extension.refresh
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
    private val engine = SpiderEngine.instance
    private lateinit var spider: Spider<List<DoubanItem>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        position = arguments?.getInt("position", 0) ?: 0
        adapter.setHasStableIds(true)
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
        val count = if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            6
        } else {
            3
        }
        recyclerView.layoutManager = GridLayoutManager(activity, count)
        recyclerView.adapter = adapter

        swipeLayout.setColorSchemeResources(R.color.colorAccent)
        swipeLayout.setOnRefreshListener {
            crawl(false)
        }
    }

    private fun crawl(useCache: Boolean = true) {
        engine.crawl(spider, useCache = useCache, error = {

            swipeLayout.cancel()
        }) { items ->
            adapter.data = items.sorted().toMutableList()
            swipeLayout.cancel()
            adapter.notifyDataSetChanged()
        }
    }

    override fun onFragmentVisible() {
        swipeLayout.post {
            swipeLayout.refresh()
            crawl()
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