package urlalias

import okhttp3.HttpUrl

fun urlalias() : UrlaliasComponent = UrlaliasModule

interface UrlaliasComponent {
    val repository: UrlaliasRepository
    val sampleData : List<UrlAlias>
    fun validateUrl(url: String) : HttpUrl?
    fun isValidAlias(name: String) : Boolean
}

object UrlaliasModule: UrlaliasComponent {

    override val repository: UrlaliasRepository = InmemoryRepository

    override fun validateUrl(url: String) : HttpUrl? =
            HttpUrl.parse(url)

    override fun isValidAlias(name: String) : Boolean {
        if (name.length <= 2) return false
        val characters = name
        return characters.all { it in authorizedChars }
    }

    val authorizedChars = listOf('-', '_', ':') + ('a'..'z') + ('A'..'Z') + ('0'..'9')


    val URL_ALIAS_SAMPLEDATA = mapOf(
            "code" to "https://github.com/jmfayard/okAndroid",
            "kotlin" to "https://kotlinlang.org",
            "ktor" to "https://ktor.io",
            "android" to "https://developer.android.com/index.html"
    )

    override val sampleData: List<UrlAlias> = URL_ALIAS_SAMPLEDATA.entries.mapIndexed alias@ {
        index, (key, value) ->
        val url = requireNotNull(validateUrl(value)) { "Invalid url $value"}
        return@alias UrlAlias(index.toLong(), url, 0, listOf(key))
    }




}