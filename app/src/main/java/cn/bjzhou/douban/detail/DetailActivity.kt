package cn.bjzhou.douban.detail

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.LinearLayoutManager
import android.text.Html
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import cn.bjzhou.douban.GlideApp
import cn.bjzhou.douban.R
import cn.bjzhou.douban.api.Api
import cn.bjzhou.douban.bean.YYeTsItem
import cn.bjzhou.douban.extension.actionBarSize
import cn.bjzhou.douban.extension.dp
import cn.bjzhou.douban.extension.isEmpty
import cn.bjzhou.douban.spider.DetailSpider
import cn.bjzhou.douban.spider.QQSearchSpider
import cn.bjzhou.douban.spider.SpiderEngine
import cn.bjzhou.douban.wrapper.KCallback
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.appbar.AppBarLayout
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*
import org.jsoup.Jsoup

/**
 * @author zhoubinjia
 * @date 2017/12/6
 */
class DetailActivity : AppCompatActivity() {

    private var title = ""
    private val engine = SpiderEngine.instance
    private var clickIntent: Intent? = null
    private var yyetsIntent: Intent? = null
    private lateinit var spider: DetailSpider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_Detail)
        setContentView(R.layout.activity_detail)
        if (Build.VERSION.SDK_INT >= 21) {
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            appBarLayout.setOnApplyWindowInsetsListener { _, insets ->
                toolbar.setPadding(0, insets.systemWindowInsetTop, 0, 0)
                toolbar.layoutParams.height = actionBarSize + insets.systemWindowInsetTop
                (topContentLayout.layoutParams as ViewGroup.MarginLayoutParams).topMargin = 48.dp + insets.systemWindowInsetTop
                appBarLayout.layoutParams.height = 256.dp + insets.systemWindowInsetTop
                insets.consumeSystemWindowInsets()
            }
            appBarLayout.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }
        appBarLayout.addOnOffsetChangedListener(AppBarLayout.BaseOnOffsetChangedListener { appBarLayout: AppBarLayout, verticalOffset: Int ->
            if (Math.abs(verticalOffset) >= appBarLayout.totalScrollRange) {
                val bitmap = Bitmap.createBitmap(toolbar.width, toolbar.height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                val matrix = Matrix()
                val scale = bitmap.width.toFloat() / coverBackgroundView.drawable.intrinsicWidth
                matrix.setScale(scale, scale)
                canvas.matrix = matrix
                coverBackgroundView.drawable.draw(canvas)
                toolbar.background = BitmapDrawable(resources, bitmap)
            } else {
                toolbar.background = null
            }
        })
        title = intent?.getStringExtra("title") ?: ""
        spider = DetailSpider(intent?.getStringExtra("url") ?: "")

        toolbarLayout.setCollapsedTitleTextColor(Color.WHITE)
        toolbarLayout.setExpandedTitleColor(Color.TRANSPARENT)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarLayout.title = title
        toolbarLayout.setContentScrimColor(ContextCompat.getColor(this, R.color.white))
        toolbarLayout.setExpandedTitleColor(Color.TRANSPARENT)
        relatedPicsView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        relatedPicsView.adapter = RelatedPicsAdapter()
        hotCommentsView.layoutManager = LinearLayoutManager(this)
        hotCommentsView.adapter = HotCommetsAdapter()
        playButton.setOnClickListener {
            clickIntent?.let {
                startActivity(it)
            }
        }
        yyetsButton.setOnClickListener {
            yyetsIntent?.let {
                startActivity(it)
            }
        }

        engine.crawl(spider, expiredTime = expiredTime) { item ->
            GlideApp.with(this)
                    .asBitmap()
                    .load(item.coverUrl)
                    .into(object : BitmapImageViewTarget(coverView) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            super.onResourceReady(resource, transition)
                            Palette.from(resource).generate { p ->
                                val color = p?.getDarkMutedColor(ContextCompat.getColor(this@DetailActivity, R.color.colorAccent)) ?: 0
                                Blurry.with(this@DetailActivity)
                                        .radius(25)
                                        .sampling(8)
                                        .color(ColorUtils.setAlphaComponent(color, 66))
                                        .animate()
                                        .from(resource)
                                        .into(coverBackgroundView)
                            }

                            titleView.text = title
                            genreView.text = item.genre
                            releaseDateView.text = item.releaseDate
                            if (!TextUtils.isEmpty(item.episode)) {
                                episodeView.visibility = View.VISIBLE
                                episodeView.text = item.episode
                            }

                            if (!TextUtils.isEmpty(item.episodeTime)) {
                                timeView.text = item.episodeTime
                            } else {
                                timeView.text = item.runTime
                            }

                            val infoBuilder = StringBuilder()
                            infoBuilder.append(item.rating).append("\n")
                                    .append(item.director).append("\n")
                                    .append(item.writer).append("\n")
                                    .append(item.actor).append("\n")
                                    .append(item.country).append("\n")
                                    .append(item.language).append("\n")
                            infoView.text = infoBuilder.toString()
                            introView.text = Html.fromHtml(item.summary)

                            val adapter = relatedPicsView.adapter as RelatedPicsAdapter
                            adapter.data.clear()
                            adapter.data.addAll(item.relatedPics)
                            adapter.notifyDataSetChanged()

                            val commentAdapter = hotCommentsView.adapter as HotCommetsAdapter
                            commentAdapter.data.clear()
                            commentAdapter.data.addAll(item.hotComments)
                            commentAdapter.notifyDataSetChanged()

                            contentView.visibility = View.VISIBLE
                        }
                    })
        }

        engine.crawl(QQSearchSpider(title), expiredTime = expiredTime) { url ->
            if (!TextUtils.isEmpty(url)) {
                playButton.visibility = View.VISIBLE
                val intent = Intent(Intent.ACTION_VIEW)
                val split = url.split("/")
                when {
                    split[2].contains("v.qq.com") -> playButton.text = "腾讯视频"
                    split[2].contains("youku") -> playButton.text = "优酷"
                    split[2].contains("iqiyi") -> playButton.text = "爱奇艺"
                    split[2].contains("letv") -> playButton.text = "乐视"
                    split[2].contains("mgtv") -> playButton.text = "芒果TV"
                    split[2].contains("bilibili") -> playButton.text = "B站"
                    else -> playButton.text = split[2]
                }
                intent.data = Uri.parse(url)
                clickIntent = intent
            }
        }

        val yyetsTitle = when {
            title.contains("：") -> title.split("：")[0]
            title.contains(" ") -> title.split(" ")[0]
            else -> title
        }
        Api.yyetsService.yyetsSearch(yyetsTitle).enqueue(object : KCallback<YYeTsItem>() {
            override fun onResponse(res: YYeTsItem) {
                val html = res.data.resource_html
                if (html.isEmpty) return
                yyetsButton.visibility = View.VISIBLE
                val intent = Intent(Intent.ACTION_VIEW)
                val doc = Jsoup.parse(html)
                val url = "http://m.zimuzu.tv" + doc.select("a")[0].attr("href")
                intent.data = Uri.parse(url)
                yyetsIntent = intent
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val expiredTime = 600 * 1000L
    }
}