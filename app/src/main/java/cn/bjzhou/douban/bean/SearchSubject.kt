package cn.bjzhou.douban.bean

import com.google.gson.annotations.SerializedName

/**
 * @author zhoubinjia
 * @date 2017/11/6
 */
class SearchSubject(@SerializedName("subjects", alternate = arrayOf("data")) var subjects: List<DoubanItem>)