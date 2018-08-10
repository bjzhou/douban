package cn.bjzhou.douban.detail

import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import android.text.Html
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import cn.bjzhou.douban.GlideApp
import cn.bjzhou.douban.R

/**
 * @author zhoubinjia
 * @date 2017/12/12
 */
class HotCommetsAdapter : RecyclerView.Adapter<HotCommetsAdapter.ViewHolder>() {

    val data = mutableListOf<String>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        (holder.itemView as TextView).text = Html.fromHtml(data[position])
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val textView = TextView(parent.context)
        textView.setTextColor(ContextCompat.getColor(parent.context, R.color.textColorPrimary))
        textView.textSize = 14f
        val lp = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        lp.bottomMargin = 16
        textView.layoutParams = lp
        return ViewHolder(textView)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}