
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
//
//
//        buildTypes {
//            named("debug").configure {
////                applicationIdSuffix = ".debug"
////                isMinifyEnabled = false
//            }
//
//            named("release").configure {
////                isMinifyEnabled = true
////                signingConfig = if (true)
////                    signingConfigs.getByName("release") else
////                    signingConfigs.getByName("debug")
//            }
//        }

        lintOptions {
            isAbortOnError = false
        }
    }
}


dependencies {
    api(project(":common"))
    api(Libs.moneta_bp)
    api(Libs.magellan)
    api(Libs.magellan_support)
    api(Libs.rxandroid)
    implementation(Libs.timber)

    androidTestImplementation(Libs.espresso_core)
    testImplementation(Libs.junit)



}
