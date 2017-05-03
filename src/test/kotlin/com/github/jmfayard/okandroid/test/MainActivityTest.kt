package com.github.jmfayard.okandroid.test

import com.github.jmfayard.okandroid.BuildConfig
import com.github.jmfayard.okandroid.MainActivity

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.annotation.Config

import org.junit.Assert.assertTrue
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(23))
class MainActivityTest {

    @Test
    fun testSomething() {
        assertTrue(Robolectric.setupActivity(MainActivity::class.java) != null)
    }
}
