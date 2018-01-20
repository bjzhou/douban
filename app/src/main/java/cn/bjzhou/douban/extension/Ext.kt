package cn.bjzhou.douban.extension

import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch

/**
 * @author zhoubinjia
 * @date 2017/11/6
 */
private var loading = false
private var refreshJob: Job? = null

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

fun SwipeRefreshLayout.refresh() {
    Log.d("refresh", "start refresh")
    refreshJob?.cancel()
    refreshJob = launch {
        delay(500)
        Log.d("refresh", "after delay")
        async(UI) {
            Log.d("refresh", "cancel: ${refreshJob?.isCancelled}, active: ${refreshJob?.isActive}")
            if (refreshJob?.isCancelled != true) {
                this@refresh.isRefreshing = true
            }
        }
    }
}

fun SwipeRefreshLayout.cancel() {
    Log.d("refresh", "refresh cancel")
    refreshJob?.cancel()
    this.isRefreshing = false
}

private fun findLastVisibleItemPosition(layoutManager: RecyclerView.LayoutManager): Int {
    if (layoutManager is LinearLayoutManager) {
        return layoutManager.findLastVisibleItemPosition()
    } else if (layoutManager is GridLayoutManager) {
        return layoutManager.findLastVisibleItemPosition()
    }
    return 0
}
