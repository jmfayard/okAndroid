import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `java-library`
    kotlin("jvm")
}

dependencies { /* :common */
    testImplementation(Libs.kotlintest)
    testImplementation(Libs.junit)
    compileOnly(Libs.jsr305)
    compileOnly(Libs.jsr305)
    api(Libs.rxjava)
    api(Libs.rxkotlin)
    api(Libs.retrofit)
    api(Libs.converter_moshi)
    api(Libs.okhttp)
    api(Libs.logging_interceptor)
    api(Libs.moshi)
    api(Libs.okio)
    api(Libs.moneta_bp)

}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

