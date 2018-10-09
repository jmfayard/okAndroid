object Config {

    private const val kotlinVersion = "1.2.71"

    object Plugins {
        const val android = "com.android.tools.build:gradle:3.2.0"
        const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlinVersion"
        const val firebase = "com.google.firebase:firebase-plugins:1.1.5"
        const val fabric = "io.fabric.tools:gradle:1.25.4"
        const val ktlint = "com.github.shyiko:ktlint:0.28.0"
        const val git = "org.ajoberstar:gradle-git:0.2.3"
        const val dexcount = "com.getkeepsafe.dexcount:dexcount-gradle-plugin:0.8.3"
        const val googleplay_publisher = "com.github.triplet.gradle:play-publisher:1.2.2"
    }

    object SdkVersions {
        val versionCode = 1
        val compile = 28
        val target = 27
        val min = 21
    }


    const val APP_VERSION = 34
    const val CARDLET_VERSION = "1878-b7136735"
    const val CARDLET_REQUIRED_VERSION = "1841-ab8a737d"

    const val ARTIFACTORY_VERSION = "1.26-SNAPSHOT"
    const val ACTIVITY = "com.github.jmfayard.okandroid.MainActivity"
    const val PACKAGE = "com.github.jmfayard.okandroid"


}

