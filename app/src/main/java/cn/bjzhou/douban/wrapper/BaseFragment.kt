package cn.bjzhou.douban.wrapper

import android.support.v4.app.Fragment
import android.util.Log

/**
 * @author zhoubinjia
 * @date 2017/11/6
 */
open class BaseFragment : Fragment() {

    var logTag = javaClass.simpleName

    override fun onResume() {
        super.onResume()
        log("onResume", userVisibleHint)
        if (userVisibleHint) {
            onFragmentVisible()
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isResumed) {
            if (isVisibleToUser) {
                onFragmentVisible()
            } else {
                onFragmentHidden()
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        log("onHiddenChanged", userVisibleHint, isResumed)
        if (isResumed) {
            if (hidden) {
                onFragmentVisible()
            } else {
                onFragmentHidden()
            }
        }
    }

    open fun onFragmentVisible() {}
    open fun onFragmentHidden() {}

    fun log(vararg msg: Any) {
        Log.d(logTag, msg.joinToString(" "))
    }
}