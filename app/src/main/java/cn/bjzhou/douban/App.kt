package cn.bjzhou.douban

import android.app.Application
import com.tencent.bugly.crashreport.CrashReport

/**
 * @User zhoubinjia
 * @Date 2018/01/20
 */
class App : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this

        CrashReport.initCrashReport(applicationContext, "e371fd2157", false)
    }

    companion object {
        lateinit var instance : App
    }
}