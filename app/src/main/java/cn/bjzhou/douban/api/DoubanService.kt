package cn.bjzhou.douban.api

import cn.bjzhou.douban.bean.DoubanItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * @author zhoubinjia
 * @date 2017/11/6
 */
interface DoubanService {
    @GET("search_subjects")
    fun search(@Query("type") type: String,
               @Query("tag") tag: String = "热门",
               @Query("sort") sort: String = "recommend",
               @Query("playable") playable: String? = null,
               @Query("page_limit") limit: Int = 21,
               @Query("page_start") start: Int = 0) : Call<List<DoubanItem>>

    @GET("new_search_subjects")
    fun newSearch(@Query("sort") sort: String = "T",
                  @Query("range") range: String = "6,10",
                  @Query("tags") tags: String = "",
                  @Query("start") start: Int = 0,
                  @Query("playable") playable: String? = null,
                  @Query("limit") limit: Int = 21) : Call<List<DoubanItem>>
}