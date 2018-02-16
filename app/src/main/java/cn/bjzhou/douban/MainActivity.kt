package cn.bjzhou.douban

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.app.AppCompatActivity
import cn.bjzhou.douban.category.CategoryFragment
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        container.adapter = object : FragmentPagerAdapter(supportFragmentManager) {
            override fun getItem(position: Int): Fragment? {
                return when (position) {
                    0 -> TabFragment.newInstance("playing")
                    1 -> TabFragment.newInstance("movie")
                    2 -> TabFragment.newInstance("tv")
                    else -> CategoryFragment()
                }
            }

            override fun getCount(): Int {
                return 4
            }
        }
        container.offscreenPageLimit = 3

        bottomNavigationView.enableAnimation(false)
        bottomNavigationView.enableShiftingMode(false)
        bottomNavigationView.enableItemShiftingMode(false)
        bottomNavigationView.setupWithViewPager(container)
        bottomNavigationView.selectedItemId = R.id.playing
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }
}
