package cn.bjzhou.douban

import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bjzhou.douban.extension.setTVSupport
import cn.bjzhou.douban.hot.HotContentFragment
import cn.bjzhou.douban.playing.PlayingContentFragment
import cn.bjzhou.douban.wrapper.BaseFragment
import kotlinx.android.synthetic.main.fragment_tab.*

/**
 * @author zhoubinjia
 * @date 2017/11/3
 */
class TabFragment : BaseFragment() {

    private var type = "playing"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        type = arguments?.getString("type", type) ?: "playing"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_tab, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
    }

    override fun onFragmentVisible() {
        if (viewPager.adapter != null) return
        viewPager.adapter = object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment? {
                return when (type) {
                    "playing" -> PlayingContentFragment.newInstance(position)
                    "movie" -> HotContentFragment.newInstance(type, movieTags[position])
                    "tv" -> HotContentFragment.newInstance(type, tvTags[position])
                    else -> null
                }
            }

            override fun getCount(): Int {
                return when (type) {
                    "playing" -> playingTags.size
                    "movie" -> movieTags.size
                    "tv" -> tvTags.size
                    else -> 0
                }
            }

            override fun getPageTitle(position: Int): CharSequence {
                return when (type) {
                    "playing" -> playingTags[position]
                    "movie" -> movieTags[position]
                    "tv" -> tvTags[position]
                    else -> ""
                }
            }
        }
        tabLayout.tabMode = if (type == "playing") {
            TabLayout.MODE_FIXED
        } else {
            TabLayout.MODE_SCROLLABLE
        }
        tabLayout.setupWithViewPager(viewPager)
//        tabLayout.setTVSupport()
    }

    companion object {

        private val playingTags = arrayOf("正在热映", "即将上映")
        private val movieTags = arrayOf("热门", "高分", "华语", "欧美", "韩国", "日本", "动作",
                "喜剧", "爱情", "科幻", "悬疑", "恐怖", "动画")
        private val tvTags = arrayOf("热门", "美剧", "英剧", "韩剧", "日剧", "国产剧", "港剧",
                "日本动画", "综艺")

        fun newInstance(type: String): TabFragment {
            val fragment = TabFragment()
            val bundle = Bundle()
            bundle.putString("type", type)
            fragment.arguments = bundle
            return fragment
        }
    }
}