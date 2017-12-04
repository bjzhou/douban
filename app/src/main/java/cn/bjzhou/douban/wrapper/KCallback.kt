package cn.bjzhou.douban.wrapper

import android.util.Log
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @author zhoubinjia
 * @date 2017/11/6
 */
abstract class KCallback<T> : Callback<T> {
    override fun onFailure(call: Call<T>, t: Throwable) {
        Log.e("KCallback", call.request().toString(), t)
        onFailure()
    }

    override fun onResponse(call: Call<T>, response: Response<T>) {
        if (response.isSuccessful) {
            response.body()?.let {
                onResponse(it)
            } ?: Log.e("KCallback", call.request().toString() + " body null")
        } else {
            Log.e("KCallback", call.request().toString() + " response failed")
        }
    }

    abstract fun onResponse(res: T)
    open fun onFailure() {}
}