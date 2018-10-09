
plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    compileSdkVersion(Config.SdkVersions.compile)

    defaultConfig {
        minSdkVersion(Config.SdkVersions.min)
        targetSdkVersion(Config.SdkVersions.target)
        versionCode = Config.SdkVersions.versionCode
        versionName = "1." + Config.SdkVersions.versionCode
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
    }
}
dependencies {
    api(project(":common"))
    api(project(":androidcommon"))

    api(Libs.room_runtime)
    implementation(Libs.room_rxjava2)
    kapt(Libs.room_compiler)
    testImplementation(Libs.junit)
    testImplementation(Libs.kotlintest)
    testImplementation(Libs.kotlin_reflect)
    androidTestImplementation(Libs.androidx_test_runner)
    androidTestImplementation(Libs.espresso_core)
//    androidTestImplementation Libs.room_testing


}
