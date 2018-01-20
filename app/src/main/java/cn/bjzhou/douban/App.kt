package cn.bjzhou.douban

import android.app.Application

/**
 * @User zhoubinjia
 * @Date 2018/01/20
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance : App
    }
}