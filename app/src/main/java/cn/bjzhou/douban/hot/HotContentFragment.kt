package cn.bjzhou.douban.hot

import android.content.res.Configuration
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bjzhou.douban.AppConfig
import cn.bjzhou.douban.AppConfig.onlyPass
import cn.bjzhou.douban.AppConfig.playable
import cn.bjzhou.douban.R
import cn.bjzhou.douban.R.id.recyclerView
import cn.bjzhou.douban.R.id.swipeLayout
import cn.bjzhou.douban.api.Api
import cn.bjzhou.douban.bean.DoubanItem
import cn.bjzhou.douban.extension.isLoadingMore
import cn.bjzhou.douban.extension.setOnLoadMore
import cn.bjzhou.douban.playing.PlayingAdapter
import cn.bjzhou.douban.wrapper.BaseFragment
import cn.bjzhou.douban.wrapper.KCallback
import kotlinx.android.synthetic.main.layout_playing.*
import retrofit2.Call

/**
 * @author zhoubinjia
 * @date 2017/11/6
 */
class HotContentFragment : BaseFragment() {

    private var type = "movie"
    private var currentTag = "热门"
    private var call: Call<List<DoubanItem>>? = null
    private val adapter = PlayingAdapter()
    private var oldSize = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString("type", type) ?: "movie"
        currentTag = arguments?.getString("tag", currentTag) ?: "热门"
        adapter.setHasStableIds(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val count = if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            6
        } else {
            3
        }
        recyclerView.layoutManager = GridLayoutManager(activity, count)
        recyclerView.adapter = adapter
        swipeLayout.setColorSchemeResources(R.color.colorAccent)
        swipeLayout.setOnRefreshListener {
            loadContent()
        }

        recyclerView.setOnLoadMore {
            loadContent(recyclerView.adapter?.itemCount ?: 0)
        }

        AppConfig.configObservers.add {
            if (it == "playable" || it == "onlyPass") {
                if (fragmentVisible) {
                    swipeLayout.isRefreshing = true
                    loadContent()
                }
            }
        }
    }

    private fun loadContent(start: Int = 0) {
        call?.cancel()
        call = Api.service.search(type, start = start, tag = currentTag, playable = if (playable) {
            "1"
        } else {
            null
        })
        call?.enqueue(object : KCallback<List<DoubanItem>>() {
            override fun onResponse(res: List<DoubanItem>) {
                val nr = if (onlyPass) {
                    res.filter { (it.score ?: 0f) >= 6f }
                } else {
                    res
                }
                val newData = mutableListOf<DoubanItem>()
                if (start != 0) {
                    newData.addAll(adapter.data)
                }
                newData.addAll(nr)
                oldSize= adapter.data.size
                adapter.data = newData.distinct()
                adapter.notifyDataSetChanged()
                swipeLayout.isRefreshing = false
                recyclerView.isLoadingMore = false
            }

            override fun onFailure() {
                if (activity == null) return
                swipeLayout.isRefreshing = false
            }
        })
    }

    override fun onFragmentVisible() {
        swipeLayout.post {
            if (recyclerView.adapter?.itemCount == 0) {
                swipeLayout.isRefreshing = true
                loadContent()
            }
        }
    }

    companion object {
        fun newInstance(type: String, tag: String): HotContentFragment {
            val fragment = HotContentFragment()
            fragment.logTag = if (type == "movie") {
                "movie"
            } else {
                "tv"
            }
            val bundle = Bundle()
            bundle.putString("type", type)
            bundle.putString("tag", tag)
            fragment.arguments = bundle
            return fragment
        }
    }
}