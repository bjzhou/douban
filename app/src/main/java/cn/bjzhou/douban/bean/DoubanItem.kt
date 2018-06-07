package cn.bjzhou.douban.bean

import com.google.gson.annotations.SerializedName

/**
 * @author zhoubinjia
 * @date 2017/11/3
 */
data class DoubanItem(@SerializedName("id") var id: String,
                 @SerializedName("title") var name: String,
                 @SerializedName("url") var url: String,
                 @SerializedName("cover") var img: String,
                 var playable: Boolean,
                 @SerializedName("rate") var score: Float? = null,
                 var wish: String? = null) : Comparable<DoubanItem> {

    override fun compareTo(other: DoubanItem): Int {
        if (wish != null && other.wish != null) {
            var wishInt = 0
            var oWishInt = 0
            try {
                wishInt = Integer.parseInt(wish!!.replace("人想看", ""))
                oWishInt = Integer.parseInt(other.wish!!.replace("人想看", ""))
            } catch (e: Exception) {
            }
            return - Integer.compare(wishInt, oWishInt)
        }
        return 0
    }

    override fun equals(other: Any?): Boolean {
        if (other !is DoubanItem) return false
        return id == other.id
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}