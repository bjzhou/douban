package cn.bjzhou.douban.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * @author zhoubinjia
 * @date 2017/11/6
 */
object Api {

    private val retrofit = Retrofit.Builder()
            .baseUrl("https://movie.douban.com/j/")
            .addConverterFactory(DConverterFactory())
            .build()

    val service = retrofit.create(DoubanService::class.java)

    val yyetsService = Retrofit.Builder()
            .baseUrl("http://m.zimuzu.tv/")
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(DoubanService::class.java)

}