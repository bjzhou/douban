package cn.bjzhou.douban.extension

import android.content.Context
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import android.util.Log
import cn.bjzhou.douban.App
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
private var mActionSize = 0

var RecyclerView.isLoadingMore: Boolean
    get() = loading
    set(value) {
        loading = value
    }

fun RecyclerView.setOnLoadMore(action: () -> Unit) {
    addOnScrollListener(object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            layoutManager?.let {
                val totalItemCount = it.itemCount
                val lastVisibleItem = findLastVisibleItemPosition(it)
                if (!loading
                        && totalItemCount <= (lastVisibleItem + 5)) {
                    action.invoke()
                    loading = true
                }
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
    return when (layoutManager) {
        is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
        is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
        is StaggeredGridLayoutManager -> layoutManager.findLastVisibleItemPositions(null).max() ?: 0
        else -> 0
    }
}

val Int.dp: Int
    get() {
        return (App.instance.resources.displayMetrics.density * this + 0.5f).toInt()
    }

val Context.actionBarSize: Int
    get() {
        if (mActionSize == 0) {
            val actionbarSizeTypedArray = this.obtainStyledAttributes(intArrayOf(android.R.attr.actionBarSize))
            mActionSize = actionbarSizeTypedArray.getDimensionPixelSize(0, 0)
            actionbarSizeTypedArray.recycle()
        }
        return mActionSize
    }

val String?.isEmpty
    get() = this == null || this.isEmpty()