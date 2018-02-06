package com.github.jmfayard.okandroid.test

import io.kotlintest.matchers.shouldBe
import io.kotlintest.specs.StringSpec

class KotlinTest : StringSpec() { init {

    "2 + 2 = 4" {
        2 + 2 shouldBe 4
    }

}
}