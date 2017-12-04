package cn.bjzhou.douban.extension

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView

/**
 * @author zhoubinjia
 * @date 2017/11/6
 */
private var loading = false

var RecyclerView.isLoadingMore: Boolean
    get() = loading
    set(value) {
        loading = value
    }

fun RecyclerView.setOnLoadMore(action: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
            val totalItemCount = layoutManager.itemCount
            val lastVisibleItem = findLastVisibleItemPosition(layoutManager)
            if (!loading
                    && totalItemCount <= (lastVisibleItem + 5)) {
                action.invoke()
                loading = true
            }
        }
    })
}

private fun findLastVisibleItemPosition(layoutManager: RecyclerView.LayoutManager): Int {
    if (layoutManager is LinearLayoutManager) {
        return layoutManager.findLastVisibleItemPosition()
    } else if (layoutManager is GridLayoutManager) {
        return layoutManager.findLastVisibleItemPosition()
    }
    return 0
}
