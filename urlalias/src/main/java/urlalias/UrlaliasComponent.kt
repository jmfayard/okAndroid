package urlalias

import okhttp3.HttpUrl

fun urlalias() : UrlaliasComponent = UrlaliasModule

interface UrlaliasComponent {
    val rootUrl : HttpUrl
    val pageNotFoundUrl : HttpUrl
    val repository: UrlaliasRepository
    val sampleData : Map<String, String>
    fun validateUrl(url: String) : HttpUrl?
    fun isValidAlias(name: String) : Boolean
}

object UrlaliasModule: UrlaliasComponent {

    override val rootUrl: HttpUrl =
            HttpUrl.parse("http://urlalias.bertrou.eu")!!

    override val pageNotFoundUrl: HttpUrl =
        HttpUrl.parse("http://wearespry.com/404notfound")!!

    override val repository: UrlaliasRepository = InmemoryRepository

    override fun validateUrl(url: String) : HttpUrl? =
            HttpUrl.parse(url)

    override fun isValidAlias(name: String) : Boolean {
        val validChars = name.all(this::isValidCharacter)
        val reserved = name.startsWith("api") || name.startsWith("app")
        return validChars && !reserved && name.length >= 2
    }

    private fun isValidCharacter(c: Char): Boolean = when(c) {
        in 'a'..'z' -> true
        in 'A'..'Z' -> true
        in '0'..'9' -> true
        in authorizedChars -> true
        else -> false
    }

    val authorizedChars = listOf('-', '_', ':')


    override val sampleData = mapOf(
            "code" to "https://github.com/jmfayard/okAndroid",
            "di" to "https://medium.com/@jm_fayard/dependency-injection-the-pattern-without-the-framework-33cfa9d5f312",
            "cool-urls" to "https://www.w3.org/Provider/Style/URI",
            "kotlin" to "https://kotlinlang.org",
            "ktor" to "https://ktor.io",
            "android" to "https://developer.android.com/index.html"
    )




}