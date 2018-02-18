package urlalias

import io.kotlintest.forAll
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.specs.FreeSpec
import okhttp3.HttpUrl
import urlalias.InmemoryRepository.data

class Tests : FreeSpec() { init {

    val component = urlalias()

    "url validation" {
        val urlsOk = listOf("http://google.com/calendar", "https://example.com")
        val urlsKO = listOf("ftp://blah.com", "", "not an url", "http : / / example.com")

        forAll(urlsOk) { input ->
            component.validateUrl(input) shouldNotBe null
        }

        forAll(urlsKO) { input ->
            component.validateUrl(input) shouldBe null
        }
    }


    "alias validation" {
        val aliasOK = listOf("hello", "HellO", "666", "c-o_ol:")
        val aliasKO = listOf("h i", "été", "wh?t", "", "a", "api", "apidd", "app")

        forAll(aliasOK) { input ->
            component.isValidAlias(input) shouldBe true
        }

        forAll(aliasKO) { input ->
            component.isValidAlias(input) shouldBe false
        }
    }

    "repository" {
        val repository = component.repository
        for ((name, url) in component.sampleData) {

            val alias = repository.addUrlAlias(HttpUrl.parse(url)!!)
            repository.findByIdOrNull(alias.id) shouldBe alias
            repository.findByUrlOrNull(alias.url) shouldBe alias
            repository.addAliasName(alias.id, name)
            repository.addAliasName(alias.id, name)
            repository.findByIdOrNull(alias.id)?.aliases shouldBe  listOf(name)
        }

        val all: List<UrlAlias> = repository.allUrlAliases()
        val aliasesFound = all.flatMap(UrlAlias::aliases).sorted()
        val urlsFound = all.map { a -> a.url.toString() }.sorted()

        aliasesFound shouldBe component.sampleData.keys.sorted()
        urlsFound shouldBe component.sampleData.values.sorted()
    }

}}