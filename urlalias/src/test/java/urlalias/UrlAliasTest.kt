package urlalias

import io.kotlintest.forAll
import io.kotlintest.matchers.shouldBe
import io.kotlintest.matchers.shouldNotBe
import io.kotlintest.specs.FreeSpec

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
        val aliasKO = listOf("h i", "été", "wh?t", "", "a", "ab")

        forAll(aliasOK) { input ->
            component.isValidAlias(input) shouldBe true
        }

        forAll(aliasKO) { input ->
            component.isValidAlias(input) shouldBe false
        }
    }

    "repository" {
        val repository = component.repository
        val list: List<UrlAlias> = component.sampleData
        for (data in list) {
            val name = data.aliases.first()

            val alias = repository.addUrlAlias(data.url)
            repository.findByIdOrNull(alias.id) shouldBe alias
            repository.findByUrlOrNull(alias.url) shouldBe alias
            repository.addAliasName(alias.id, name)
            repository.addAliasName(alias.id, name)
            repository.findByIdOrNull(alias.id)?.aliases shouldBe listOf(name)
        }
        val allAliases = repository.allUrlAliases()
        allAliases.size shouldBe component.sampleData.size
        allAliases.flatMap { url -> url.aliases }.sorted() shouldBe list.flatMap { it.aliases }.sorted()
    }

}
}