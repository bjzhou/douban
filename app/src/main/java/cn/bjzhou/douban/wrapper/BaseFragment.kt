package cn.bjzhou.douban.wrapper

import android.support.v4.app.Fragment
import android.util.Log

/**
 * @author zhoubinjia
 * @date 2017/11/6
 */
open class BaseFragment : Fragment() {

    var logTag = javaClass.simpleName
    var fragmentVisible = false
        private set

    override fun onResume() {
        super.onResume()
        log("onResume", userVisibleHint)
        if (userVisibleHint) {
            onFragmentVisible()
            fragmentVisible = true
        }
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isResumed) {
            fragmentVisible = if (isVisibleToUser) {
                onFragmentVisible()
                true
            } else {
                onFragmentHidden()
                false
            }
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        log("onHiddenChanged", userVisibleHint, isResumed)
        if (isResumed) {
            fragmentVisible = if (hidden) {
                onFragmentHidden()
                false
            } else {
                onFragmentVisible()
                true
            }
        }
    }

    open fun onFragmentVisible() {}
    open fun onFragmentHidden() {}

    fun log(vararg msg: Any) {
        Log.d(logTag, msg.joinToString(" "))
    }
}