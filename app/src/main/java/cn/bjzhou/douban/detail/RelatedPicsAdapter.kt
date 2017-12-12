package cn.bjzhou.douban.detail

import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import cn.bjzhou.douban.GlideApp

/**
 * @author zhoubinjia
 * @date 2017/12/12
 */
class RelatedPicsAdapter : RecyclerView.Adapter<RelatedPicsAdapter.ViewHolder>() {

    val data = mutableListOf<String>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        GlideApp.with(holder.itemView)
                .load(data[position])
                .into(holder.itemView as ImageView)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val imgView = ImageView(parent.context)
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 180)
        lp.rightMargin = 8
        imgView.layoutParams = lp
        return ViewHolder(imgView)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}