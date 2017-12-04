package cn.bjzhou.douban.playing

import android.arch.lifecycle.MutableLiveData
import android.content.Intent
import android.net.Uri
import android.support.customtabs.CustomTabsIntent
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bjzhou.douban.GlideApp
import cn.bjzhou.douban.R
import cn.bjzhou.douban.R.string.score
import cn.bjzhou.douban.bean.DoubanItem
import kotlinx.android.synthetic.main.item_playing.view.*

/**
 * @author zhoubinjia
 * @date 2017/11/3
 */
class PlayingAdapter : RecyclerView.Adapter<PlayingAdapter.ViewHolder>() {

    var data = MutableLiveData<List<DoubanItem>>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data.value?.get(position) ?: return
        holder.itemView.setOnClickListener {
            val intent = CustomTabsIntent.Builder()
                    .setToolbarColor(ContextCompat.getColor(holder.itemView.context, R.color.colorPrimary))
                    .build()
            intent.launchUrl(holder.itemView.context, Uri.parse(item.url))
//            val intent = Intent(Intent.ACTION_VIEW)
//            intent.data = Uri.parse("http://www.iqiyi.com/v_19rr8w99fw.html?vfm=m_103_txsp")
//            intent.data = Uri.parse("http://v.youku.com/v_show/id_XMjk4ODAyMzIyOA==.html")
//            intent.data = Uri.parse("tenvideo://?action=1&cover_id=iqwvbujgzj5obv1")
//            holder.itemView.context.startActivity(intent)
        }
        GlideApp.with(holder.itemView.context)
                .load(item.img)
                .placeholder(R.drawable.placeholder)
                .into(holder.image)
        holder.titleView.text = item.name
        if (item.score == null) {
            holder.subTitleView.text = item.wish
        } else {
            holder.subTitleView.text = if (item.score == 0f) {
                holder.itemView.context.getString(R.string.no_score)
            } else {
                holder.itemView.context.getString(R.string.score, item.score)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_playing, parent, false))
    }

    override fun getItemCount(): Int {
        return data.value?.size ?: 0
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var image = itemView.image
        var titleView = itemView.titleView
        var subTitleView = itemView.subTitleView
    }
}