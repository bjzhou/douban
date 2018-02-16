package cn.bjzhou.douban.hot

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bjzhou.douban.R
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
    var onlyPass = false
        set(value) {
            field = value
            if (fragmentVisible) {
                swipeLayout.isRefreshing = true
                loadContent()
            }
        }
    var onlyPlayable = false
        set(value) {
            field = value
            if (fragmentVisible) {
                swipeLayout.isRefreshing = true
                loadContent()
            }
        }
    private var call: Call<List<DoubanItem>>? = null
    private val adapter = PlayingAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments.getString("type", type)
        currentTag = arguments.getString("tag", currentTag)
        onlyPass = arguments.getBoolean("pass")
        onlyPlayable = arguments.getBoolean("playable")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.layout_playing, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        recyclerView.adapter = adapter
        adapter.data.observe(this, Observer {
            swipeLayout.isRefreshing = false
            recyclerView.isLoadingMore = false
            adapter.notifyDataSetChanged()
        })

        swipeLayout.setColorSchemeResources(R.color.colorAccent)
        swipeLayout.setOnRefreshListener {
            loadContent()
        }

        recyclerView.setOnLoadMore {
            loadContent(recyclerView.adapter.itemCount)
        }
    }

    private fun loadContent(start: Int = 0) {
        call?.cancel()
        call = Api.service.search(type, start = start, tag = currentTag, playable = if (onlyPlayable) {
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
                if (start == 0) {
                    adapter.data.value = nr
                } else {
                    adapter.data.value = adapter.data.value?.let {
                        val newData = it as MutableList
                        newData.addAll(nr)
                        newData
                    } ?: nr
                }
            }

            override fun onFailure() {
                if (activity == null) return
                swipeLayout.isRefreshing = false
            }
        })
    }

    override fun onFragmentVisible() {
        swipeLayout.post {
            if (recyclerView.adapter.itemCount == 0) {
                swipeLayout.isRefreshing = true
                loadContent()
            }
        }
    }

    companion object {
        fun newInstance(type: String, tag: String, pass: Boolean, playable: Boolean): HotContentFragment {
            val fragment = HotContentFragment()
            fragment.logTag = if (type == "movie") {
                "movie"
            } else {
                "tv"
            }
            val bundle = Bundle()
            bundle.putString("type", type)
            bundle.putString("tag", tag)
            bundle.putBoolean("pass", pass)
            bundle.putBoolean("playable", playable)
            fragment.arguments = bundle
            return fragment
        }
    }
}