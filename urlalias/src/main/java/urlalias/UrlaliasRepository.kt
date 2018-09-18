package urlalias

import okhttp3.HttpUrl


interface UrlaliasRepository {

    // Find a urlalias, or null if not found
    fun findByUrlOrNull(url: HttpUrl): UrlAlias?

    // Find a urlalias, or null if not found
    fun findByIdOrNull(urlId: Long): UrlAlias?

    // Add an url. Idempotent if it already exists
    fun addUrlAlias(url: HttpUrl): UrlAlias

    // fetch all urls
    fun allUrlAliases(): List<UrlAlias>

    // Add an alias for the url. Null if does not exist. Idempotent if it already exists
    fun addAliasName(urlId: Long, aliasName: String): UrlAlias?

}


internal object InmemoryRepository : UrlaliasRepository {

    val data = mutableListOf<UrlAlias>()

    override fun findByUrlOrNull(url: HttpUrl): UrlAlias? =
            data.firstOrNull { it.url == url }

    override fun findByIdOrNull(urlId: Long): UrlAlias? =
            data.firstOrNull { it.id == urlId }

    override fun allUrlAliases(): List<UrlAlias> =
            data.toList()

    override fun addAliasName(urlId: Long, aliasName: String): UrlAlias? {
        val alias = findByIdOrNull(urlId) ?: return null
        val names = (alias.aliases + aliasName).distinct()
        alias.aliases = names
        return alias
    }

    override fun addUrlAlias(url: HttpUrl): UrlAlias {
        val existing = findByUrlOrNull(url)
        if (existing == null) {
            val result = UrlAlias(data.size.toLong(), url, 0, mutableListOf())
            data.add(result)
            return result
        } else {
            return existing
        }
    }


}