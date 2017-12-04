package cn.bjzhou.douban.bean

import com.google.gson.annotations.SerializedName

/**
 * @author zhoubinjia
 * @date 2017/11/3
 */
class DoubanItem(@SerializedName("id") var id: String,
                 @SerializedName("title") var name: String,
                 @SerializedName("url") var url: String,
                 @SerializedName("cover") var img: String,
                 @SerializedName("rate") var score: Float? = null,
                 var wish: String? = null)