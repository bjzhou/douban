package cn.bjzhou.douban.api

import cn.bjzhou.douban.bean.SearchSubject
import com.google.gson.Gson
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.nio.charset.Charset

/**
 * @author zhoubinjia
 * @date 2017/11/6
 */
class DConverterFactory : Converter.Factory() {

    private val gson = Gson()

    override fun responseBodyConverter(type: Type, annotations: Array<Annotation>,
                                       retrofit: Retrofit): Converter<ResponseBody, *> {
        return DResponseBodyConverter(gson)
    }

    override fun requestBodyConverter(type: Type?, parameterAnnotations: Array<out Annotation>?, methodAnnotations: Array<out Annotation>?, retrofit: Retrofit?): Converter<*, RequestBody>? {
        val adapter = gson.getAdapter(TypeToken.get(type))
        return DRequestBodyConverter(gson, adapter)
    }

    class DResponseBodyConverter(private val gson: Gson) : Converter<ResponseBody, Any> {

        override fun convert(value: ResponseBody): Any {
            val jsonReader = gson.newJsonReader(value.charStream())
            value.use {
                val searchAdapter = gson.getAdapter(SearchSubject::class.java)
                val subject = searchAdapter.read(jsonReader)
                return subject.subjects
            }
        }
    }

    class DRequestBodyConverter<T>(private val gson: Gson, private val adapter: TypeAdapter<T>) : Converter<T, RequestBody> {
        override fun convert(value: T): RequestBody {
            val buffer = Buffer()
            val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
            val jsonWriter = gson.newJsonWriter(writer)
            adapter.write(jsonWriter, value)
            jsonWriter.close()
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString())
        }

        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")
        private val UTF_8 = Charset.forName("UTF-8")

    }
}