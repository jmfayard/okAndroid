buildscript {
    repositories {
        google()
        mavenCentral()
        jcenter()
    }

    dependencies {
        classpath(Config.Plugins.kotlin)
        classpath(Config.Plugins.android)
    }
}
plugins {
    id("com.gradle.build-scan") version "1.16"
    id("jmfayard.github.io.gradle-kotlin-dsl-libs") version "0.2.3"
}


allprojects {

    repositories {
        jcenter()
        google()
        maven("https://jitpack.io")
        maven("https://oss.sonatype.org/content/repositories/snapshots")
    }

    configurations.all {
        resolutionStrategy {
            force(Libs.rxjava)
            force(Libs.kotlin_stdlib_jdk8, Libs.kotlin_stdlib, Libs.kotlin_reflect, Libs.kotlin_android_extensions)
        }
    }
}

tasks.register("i", Exec::class) {
    dependsOn(":t", ":app:installDebug")
    description = "Install the app"
    commandLine = "adb shell am start -n ${Config.PACKAGE}/${Config.ACTIVITY} -a android.intent.action.MAIN -c android.intent.category.LAUNCHER".split(" ")
    doLast {
        println("""
            App ${Config.PACKAGE} has been installed
            You can watch the logcat with
               $ pidcat ${Config.PACKAGE}
            """.trimIndent())
    }
}

tasks.register("monkey", Exec::class) {
    dependsOn(":i")
    description = "Launch the monkey"
    commandLine = "bash -x gradle/monkey.sh ${Android.Package}".split(" ")
}


tasks.register("t", DefaultTask::class) {
    dependsOn(":app:testDebugUnitTest")
    description = "Run the unit tests"
}


buildScan {
    setTermsOfServiceUrl("https://gradle.com/terms-of-service")
    setTermsOfServiceAgree("yes")
    publishAlways()
}

tasks.register("printProperties", DefaultTask::class) {
    doLast {
        val properties = listOf("org.gradle.java.home", "org.gradle.jvmargs")
        val systemProperties = listOf("file.encoding", "user.country", "user.language", "java.io.tmpdir", "user.variant")
        println("Detecting what could cause incompatible gradle daemons")
        println("Run ./gradlew printProperties from the command-line and the same task Android studio")
        println("See https://docs.gradle.org/4.1/userguide/build_environment.html")
        println("See https://docs.gradle.org/4.1/userguide/gradle_daemon.html#daemon_faq")
        println()
        println("JAVA_HOME=" + System.getenv("JAVA_HOME"))
        properties.forEach { prop -> println(prop + "=" + project.findProperty(prop)) }
        systemProperties.forEach { prop -> println(prop + "=" + System.getProperty(prop)) }
    }
}