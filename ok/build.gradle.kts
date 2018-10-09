
plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-android-extensions")
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
//        signingConfigs {
//            named("debug").configure {
//                storeFile = file("debug.keystore")
//            }
//            register("release") {
//                storeFile = file("debug.keystore")
//            }
//        }


        buildTypes {
            named("debug").configure {
//                applicationIdSuffix = ".debug"
//                isMinifyEnabled = false
            }

            named("release").configure {
//                isMinifyEnabled = true
//                signingConfig = if (true)
//                    signingConfigs.getByName("release") else
//                    signingConfigs.getByName("debug")
            }
        }

        lintOptions {
            isAbortOnError = false
        }
    }
}


dependencies {
    /*** other projects **/
    add("compile", project(":common"))
    add("compile", project(":androidcommon"))
    add("compile", project(":room"))
    add("compile", project(":urlalias"))

    compileOnly(Libs.jsr305)
    testCompileOnly(Libs.jsr305)

    /****** TESTING ****/
//    testImplementation  Libs.room_testing
 //    api "uk.co.chrisjenx:calligraphy:2.3.0"

    testImplementation(Libs.kotlin_reflect)
    testImplementation(Libs.kotlintest)
    testImplementation(Libs.junit)
    testImplementation(Libs.mockito_kotlin)
    testImplementation(Libs.retrofit_mock)
    androidTestImplementation(Libs.kotlintest)
    androidTestImplementation(Libs.espresso_core)
    androidTestImplementation(Libs.espresso_contrib)
    
    
    implementation(Libs.kotlin_stdlib)
    implementation(Androidx.LegacySupportV4)
    implementation(Androidx.AppCompat)
    implementation(Androidx.Preference)
    implementation(Androidx.Material)
    implementation(Androidx.Percent)
    implementation(Androidx.CardView)
    implementation(Androidx.Browser)
    implementation(Androidx.Multidex)
    implementation(Androidx.AndroidxCore)
    implementation(Androidx.ConstraintLayout)
    implementation(Androidx.Mediarouter)
    implementation(Libs.timber)
    implementation(Libs.android_job)
    implementation(Libs.rxjava)
    implementation(Libs.rxkotlin)
    implementation(Libs.rxbinding_kotlin)
    implementation(Libs.rxbinding_support_v4_kotlin)
    implementation(Libs.okhttp)
    implementation(Libs.logging_interceptor)
    implementation(Libs.moshi)
    implementation(Libs.okio)
    implementation(Libs.rxpermissions)
    implementation(Libs.retrofit)
    implementation(Libs.converter_moshi)
    implementation(Libs.retrofit_mock)
    implementation(Libs.retrofit2_rxjava2_adapter)
    implementation(Libs.com_afollestad_material_dialogs_core)
    implementation(Libs.magellan)
    implementation(Libs.magellan_support)
    implementation(Libs.slimadapter)
    implementation(Libs.kotlinandroidviewbindings)
    implementation(Libs.magellan_rx2)


}

