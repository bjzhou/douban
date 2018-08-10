package cn.bjzhou.douban.extension

import android.annotation.SuppressLint
import com.google.android.material.tabs.TabLayout
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.appcompat.widget.ActionMenuView
import androidx.appcompat.widget.Toolbar
import android.view.View
import android.view.ViewGroup
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx

/**
 * @User zhoubinjia
 * @Date 2018/06/05
 */

@SuppressLint("RestrictedApi")
fun Toolbar.setTVSupport() {
    for (i in 0 until this.childCount) {
        val view = this.getChildAt(i)
        if (view is ActionMenuView) {
            for (j in 0 until view.childCount) {
                val itemView = view.getChildAt(j)
                if (itemView is ActionMenuItemView) {
                    itemView.isFocusable = true
                }
            }
        }
    }
}