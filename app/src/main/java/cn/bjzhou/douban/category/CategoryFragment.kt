package cn.bjzhou.douban.category

import androidx.lifecycle.Observer
import android.content.res.Configuration
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bjzhou.douban.AppConfig
import cn.bjzhou.douban.AppConfig.onlyPass
import cn.bjzhou.douban.AppConfig.playable
import cn.bjzhou.douban.R
import cn.bjzhou.douban.api.Api
import cn.bjzhou.douban.bean.DoubanItem
import cn.bjzhou.douban.extension.cancel
import cn.bjzhou.douban.extension.isLoadingMore
import cn.bjzhou.douban.extension.refresh
import cn.bjzhou.douban.extension.setOnLoadMore
import cn.bjzhou.douban.playing.PlayingAdapter
import cn.bjzhou.douban.wrapper.BaseFragment
import cn.bjzhou.douban.wrapper.KCallback
import kotlinx.android.synthetic.main.fragment_category.*
import retrofit2.Call

/**
 * @author zhoubinjia
 * @date 2017/11/7
 */
class CategoryFragment : BaseFragment(), TabLayout.OnTabSelectedListener {

    override fun onTabReselected(tab: TabLayout.Tab) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        sort = when (sortLayout.selectedTabPosition) {
            0 -> "T"
            1 -> "R"
            2 -> "S"
            else -> "R"
        }
        joinTags()
        swipeLayout.refresh()
        loadContent()
    }

    private var tags = ""
    private var sort = "R"
    private val adapter = PlayingAdapter()
    private var call: Call<List<DoubanItem>>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.setHasStableIds(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        sorts.forEach {
            sortLayout.addTab(sortLayout.newTab().setText(it))
        }
        forms.forEach {
            formLayout.addTab(formLayout.newTab().setText(it))
        }
        types.forEach {
            typeLayout.addTab(typeLayout.newTab().setText(it))
        }
        areas.forEach {
            areaLayout.addTab(areaLayout.newTab().setText(it))
        }
        features.forEach {
            featureLayout.addTab(featureLayout.newTab().setText(it))
        }
        sortLayout.addOnTabSelectedListener(this)
        formLayout.addOnTabSelectedListener(this)
        typeLayout.addOnTabSelectedListener(this)
        areaLayout.addOnTabSelectedListener(this)
        featureLayout.addOnTabSelectedListener(this)
        sortLayout.getTabAt(1)?.select()
        val count = if (activity?.resources?.configuration?.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            6
        } else {
            3
        }
        recyclerView.layoutManager = GridLayoutManager(activity, count)
        recyclerView.adapter = adapter

        recyclerView.setOnLoadMore {
            recyclerView.adapter?.let {
                if (it.itemCount % 21 == 0) {
                    loadContent(it.itemCount)
                } else {
                    recyclerView.isLoadingMore = false
                }
            }
        }
        swipeLayout.setColorSchemeResources(R.color.colorAccent)
        swipeLayout.setOnRefreshListener {
            loadContent()
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

    override fun onFragmentVisible() {
        recyclerView.post {
            if (recyclerView.adapter?.itemCount == 0) {
                swipeLayout.refresh()
                loadContent()
            }
        }
    }

    private fun loadContent(start: Int = 0) {
        call?.cancel()
        call = Api.service.newSearch(start = start, tags = tags, sort = sort, range = if (onlyPass) {
            "6,10"
        } else {
            "0,10"
        }, playable = if (playable) {
            "1"
        } else {
            null
        })
        call?.enqueue(object : KCallback<List<DoubanItem>>() {
            override fun onResponse(res: List<DoubanItem>) {
                val newData = mutableListOf<DoubanItem>()
                if (start != 0) {
                    newData.addAll(adapter.data)
                }
                newData.addAll(res)
                DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return adapter.data[oldItemPosition].url == newData[newItemPosition].url
                    }

                    override fun getOldListSize(): Int {
                        return adapter.data.size
                    }

                    override fun getNewListSize(): Int {
                        return newData.size
                    }

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                        return adapter.data[oldItemPosition] == newData[newItemPosition]
                    }
                }).dispatchUpdatesTo(adapter)
                adapter.data = newData
                swipeLayout.cancel()
                recyclerView.isLoadingMore = false
            }

            override fun onFailure() {
                if (activity == null) return
                swipeLayout.cancel()
            }
        })
    }

    private fun joinTags() {
        var newTags = ""
        if (formLayout.selectedTabPosition != 0) {
            newTags += forms[formLayout.selectedTabPosition]
        }
        if (typeLayout.selectedTabPosition != 0) {
            if (newTags != "") newTags += ","
            newTags += types[typeLayout.selectedTabPosition]
        }
        if (areaLayout.selectedTabPosition != 0) {
            if (newTags != "") newTags += ","
            newTags += areas[areaLayout.selectedTabPosition]
        }
        if (featureLayout.selectedTabPosition != 0) {
            if (newTags != "") newTags += ","
            newTags += features[featureLayout.selectedTabPosition]
        }
        tags = newTags
    }

    companion object {
        private val sorts = arrayOf("按热度排序", "按时间排序", "按评价排序")
        private val forms = arrayOf("全部形式", "电影", "电视剧", "综艺", "动画", "纪录片", "短片")
        private val types = arrayOf("全部类型", "剧情", "爱情", "喜剧", "科幻", "动作", "悬疑", "犯罪",
                "恐怖", "青春", "励志", "战争", "文艺", "黑色幽默", "传记", "情色", "暴力", "音乐", "家庭")
        private val areas = arrayOf("全部地区", "大陆", "美国", "香港", "台湾", "日本", "韩国", "英国",
                "法国", "德国", "意大利", "西班牙", "印度", "泰国", "俄罗斯", "伊朗", "加拿大", "澳大利亚",
                "爱尔兰", "瑞典", "巴西", "丹麦")
        private val features = arrayOf("全部特色", "经典", "冷门", "佳片", "魔幻", "黑帮", "女性")
    }
}