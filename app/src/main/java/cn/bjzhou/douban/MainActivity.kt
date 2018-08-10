package cn.bjzhou.douban

import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.google.android.material.tabs.TabLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentPagerAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import android.view.View
import cn.bjzhou.douban.category.CategoryFragment
import cn.bjzhou.douban.search.SearchActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        }

        toolbar.inflateMenu(R.menu.filter)
        toolbar.setOnMenuItemClickListener { item ->
            if (item.isCheckable) {
                item.isChecked = !item.isChecked
                if (item.itemId == R.id.score) {
                    AppConfig.onlyPass = item.isChecked
                }
                if (item.itemId == R.id.playable) {
                    AppConfig.playable = item.isChecked
                }
                return@setOnMenuItemClickListener true
            }
            return@setOnMenuItemClickListener false
        }
        val subMenu = toolbar.menu.findItem(R.id.filter).subMenu
        val score = subMenu.findItem(R.id.score)
        val playable = subMenu.findItem(R.id.playable)
        score.isChecked = AppConfig.onlyPass
        playable.isChecked = AppConfig.playable
        val menuItem = toolbar.menu.findItem(R.id.search)
        val searchView = menuItem.actionView as SearchView

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

            override fun getPageTitle(position: Int): CharSequence? {
                return when (position) {
                    0 -> getString(R.string.playing)
                    1 -> getString(R.string.movie)
                    2 -> getString(R.string.tv)
                    else -> getString(R.string.category)
                }
            }
        }
        container.offscreenPageLimit = 3

        tabLayout.tabMode = TabLayout.MODE_FIXED
        tabLayout.setupWithViewPager(container)

        searchView.isSubmitButtonEnabled = true
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                intent.putExtra("keyword", query)
                startActivity(intent)
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    override fun onBackPressed() {
        moveTaskToBack(false)
    }
}
