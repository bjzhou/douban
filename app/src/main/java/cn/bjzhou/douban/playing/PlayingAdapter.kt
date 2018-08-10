package cn.bjzhou.douban.playing

import android.content.Intent
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import cn.bjzhou.douban.GlideApp
import cn.bjzhou.douban.R
import cn.bjzhou.douban.bean.DoubanItem
import cn.bjzhou.douban.detail.DetailActivity
import kotlinx.android.synthetic.main.item_playing.view.*

/**
 * @author zhoubinjia
 * @date 2017/11/3
 */
class PlayingAdapter : RecyclerView.Adapter<PlayingAdapter.ViewHolder>() {

    var data = listOf<DoubanItem>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.contentView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailActivity::class.java)
            intent.putExtra("title", item.name)
            intent.putExtra("url", item.url)
            holder.itemView.context.startActivity(intent)
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

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_playing, parent, false))
    }

    override fun getItemCount(): Int {
        return data.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var contentView = itemView.contentView
        var image = itemView.image
        var titleView = itemView.titleView
        var subTitleView = itemView.subTitleView
    }
}