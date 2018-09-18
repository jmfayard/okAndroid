package urlalias

import okhttp3.HttpUrl
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface UrlaliasApi {

    @POST("api/url")
    fun postUrl(@Body url: PostUrl) : Call<UrlAlias>

    @POST("api/url/alias")
    fun addAlias(@Body alias: PostAlias) : Call<UrlAlias>

    @GET("api/url/{urlId}")
    fun getUrl(@Path("urlId") urlId: Long) : Call<UrlAlias>

    @GET("api/url")
    fun getAllUrls() : Call<List<UrlAlias>>

}

data class UrlAlias(
        val id: Long,
        val url: HttpUrl,
        var clicks: Int,
        var aliases: List<String>
)

data class PostUrl(val url: String)

data class PostAlias(val urlId: Long, val alias: String)

enum class ApiError(val code: Int, val clarification: String) {
    INVALID_URLID(100, "UrlId not found"),
    INVALID_URL_FORMAT(101, "Invalid url format"),
    INVALID_ALIAS_FORMAT(102, "Invalid alias format"),
    ALIAS_ALREADY_TAKEN(103, "Alias already taken")
}