package cn.bjzhou.douban

import android.content.SharedPreferences
import android.preference.PreferenceManager

/**
 * @User zhoubinjia
 * @Date 2018/06/06
 */

typealias ConfigObserver = (String) -> Unit

object AppConfig {

    val configObservers = mutableListOf<ConfigObserver>()

    private val sp: SharedPreferences by lazy {
        val _sp = PreferenceManager.getDefaultSharedPreferences(App.instance)
        _sp.registerOnSharedPreferenceChangeListener { _, key ->
            configObservers.forEach { it(key) }
        }
        _sp
    }

    var playable: Boolean
        get() = sp.getBoolean("playable", false)
        set(value) {
            sp.edit().putBoolean("playable", value).apply()
        }

    var onlyPass: Boolean
        get() = sp.getBoolean("onlyPass", false)
        set(value) {
            sp.edit().putBoolean("onlyPass", value).apply()
        }
}