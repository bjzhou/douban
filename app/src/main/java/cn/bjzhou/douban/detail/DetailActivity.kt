package cn.bjzhou.douban.detail

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v4.graphics.ColorUtils
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import android.support.v7.widget.LinearLayoutManager
import android.text.Html
import android.text.TextUtils
import android.view.MenuItem
import android.view.View
import cn.bjzhou.douban.GlideApp
import cn.bjzhou.douban.R
import cn.bjzhou.douban.spider.DetailSpider
import cn.bjzhou.douban.spider.QQSearchSpider
import cn.bjzhou.douban.spider.SpiderEngine
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.bumptech.glide.request.transition.Transition
import jp.wasabeef.blurry.Blurry
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

/**
 * @author zhoubinjia
 * @date 2017/12/6
 */
class DetailActivity : AppCompatActivity() {

    private var title = ""
    private val engine = SpiderEngine.instance
    private var clickIntent: Intent? = null
    private lateinit var spider: DetailSpider

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        title = intent?.getStringExtra("title") ?: ""
        spider = DetailSpider(intent?.getStringExtra("url") ?: "")

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

        engine.crawl(spider, expiredTime = expiredTime) { item ->
            GlideApp.with(this)
                    .asBitmap()
                    .load(item.coverUrl)
                    .into(object : BitmapImageViewTarget(coverView) {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            super.onResourceReady(resource, transition)
                            Palette.from(resource).generate { p ->
                                val color = p.getDarkMutedColor(ContextCompat.getColor(this@DetailActivity, R.color.colorAccent))
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
                if (split[2].contains("v.qq.com")) {
                    playButton.text = "腾讯视频"
                    val coverId = split[5]
                    intent.data = Uri.parse("tenvideo2://?action=1&cover_id=$coverId")
                    val component = intent.resolveActivity(packageManager)
                    if (component != null) {
                        clickIntent = intent
                        return@crawl
                    }
                } else if (split[2].contains("youku")) {
                    playButton.text = "优酷"
                } else if (split[2].contains("iqiyi")) {
                    playButton.text = "爱奇艺"
                } else if (split[2].contains("letv")) {
                    playButton.text = "乐视"
                } else if (split[2].contains("mgtv")) {
                    playButton.text = "芒果TV"
                } else if (split[2].contains("bilibili")) {
                    playButton.text = "B站"
                } else {
                    playButton.text = split[2]
                }
                intent.data = Uri.parse(url)
                clickIntent = intent
            }
        }
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