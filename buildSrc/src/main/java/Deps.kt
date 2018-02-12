object Deps {
    val versions = Versions

    @JvmStatic
    fun kotlin(module: String): Any =
            "org.jetbrains.kotlin:kotlin-$module:${versions.kotlin}"

    /**
     * https://developer.android.com/topic/libraries/support-library/packages.html#recommendation
     */
    @JvmStatic
    fun AndroidSupport(vararg module: String): Any {
        val invalids = module.filter { it !in androidSupportModules }
        require(invalids.isEmpty()) { "Support modules $invalids must be in $androidSupportModules" }
        return module.map { "com.android.support:$it:${versions.support}" }
    }

    val androidSupportModules = listOf("support-v4", "appcompat-v7", "preference-v7", "design", "percent", "cardview-v7", "customtabs", "gridlayout-v7",
            "mediarouter-v7", "palette-v7", "recyclerview-v7", "preference-v7"
            , "preference-v14", "preference-leanback-v17", "leanback-v17", "support-vector-drawable", "animated-vector-drawable", "percent", "exifinterface",
            "recommendation", "wear", "support-compat", "support-core-utils",
            "support-core-ui", "support-media-compat", "support-fragment")


    val Timber = "com.jakewharton.timber:timber:" + versions.timber

    /**Magellan, the Simplest Navigation for Android
    https://github.com/wealthfront/magellan **/
    val Magellan = "com.wealthfront:magellan:" + versions.magellan
    val MagellanSupport = "com.wealthfront:magellan-support:" + versions.magellan
    val MagellanRx = "com.wealthfront:magellan-rx:" + versions.magellan

    /** https://github.com/ReactiveX/RxAndroid **/
    val RxAndroid = "io.reactivex.rxjava2:rxandroid:" + versions.rxandroid
    val JUnit = "junit:junit:" + versions.junit
    val EspressoTestRunner = "com.android.support.test:runner:1.0.1"
    val EspressoCore = "com.android.support.test.espresso:espresso-core:" + versions.espresso
    val EspressoContrib = "com.android.support.test.espresso:espresso-core:" + versions.espresso
    val JavaMoney = "org.javamoney:moneta-bp:1.1"
    val JavaxInject = "javax.inject:javax.inject:1"

    val Multidex = "com.android.support:multidex:" + versions.multidex

    /** JSR-305 nullability annotations: **/
    val Jsr305 = "com.google.code.findbugs:jsr305:3.0.2"

    val ConstraintLayout = "com.android.support.constraint:constraint-layout:" + versions.constraint
    /**
    http://rxmarbles.com/
    http://reactivex.io/documentation/operators.html
    https://github.com/ReactiveX/RxJava/wiki
     */
    val RxJava2 = "io.reactivex.rxjava2:rxjava:" + versions.rxjava2



    // https://github.com/ReactiveX/RxKotlin
    val RxKotlin = "io.reactivex.rxjava2:rxkotlin:" + versions.rxkotlin2

    // https://github.com/JakeWharton/RxBinding
    val RxBindingKotlin = "com.jakewharton.rxbinding2:rxbinding-kotlin:" + versions.rxbinding
    val RxBindingV4Kotlin = "com.jakewharton.rxbinding2:rxbinding-support-v4-kotlin:" + versions.rxbinding


    // Retrofit
    // http://square.github.io/retrofit/
    // https://futurestud.io/tutorials/retrofit-getting-started-and-android-client
    val Retrofit = "com.squareup.retrofit2:retrofit:" + versions.retrofit
    val RetrofitMoshi = "com.squareup.retrofit2:converter-moshi:" + versions.retrofit
    val RetrofitMock = "com.squareup.retrofit2:retrofit-mock:" + versions.retrofit
    val RetrofitRxjava2 = "com.jakewharton.retrofit:retrofit2-rxjava2-adapter:1.0.0"
    val RetrofitWire = "com.squareup.retrofit2:converter-wire:" + versions.retrofit
    val RetrofitJackson = "com.squareup.retrofit2:converter-jackson:" + versions.retrofit
    val RetrofitGson = "com.squareup.retrofit2:converter-gson:" + versions.retrofit
    val RetrofitSimpleXml = "com.squareup.retrofit2:converter-simplexml:" + versions.retrofit
    val RetrofitProtobuf = "com.squareup.retrofit2:converter-protobuf:" + versions.retrofit

    val RxPermissions = "com.tbruyelle.rxpermissions2:rxpermissions:0.9.5@aar"


    // https://github.com/square/okhttp/wiki/Recipes
    val Okhttp = "com.squareup.okhttp3:okhttp:" + versions.okhttp
    val OkhttpLogging = "com.squareup.okhttp3:logging-interceptor:" + versions.okhttp
    val OkhttpMockserver = "com.squareup.okhttp3:mockwebserver:" + versions.okhttp

    // https://github.com/square/moshi
    val Moshi = "com.squareup.moshi:moshi:" + versions.moshi

    // https://github.com/square/okio
    val Okio = "com.squareup.okio:okio:" + versions.okio


    val Dagger = "com.google.dagger:dagger:" + versions.dagger2

    val Calligraphy = "uk.co.chrisjenx:calligraphy:" + versions.calligraphy


    /* A slim & clean & typeable Adapter without# VIEWHOLDER  https://github.com/MEiDIK/SlimAdapter */
    val SlimAdapter = "net.idik:slimadapter:${versions.slimadapter}"


    // http://marcinmoskala.com/android/kotlin/2017/05/05/still-mvp-or-already-mvvm.html
    val KotlinAndroidViewBindings = "com.github.MarcinMoskala:KotlinAndroidViewBindings:" + versions.KotlinAndroidViewBindings


    // https://developer.android.com/topic/libraries/architecture/adding-components.html
    val RoomRuntime = "android.arch.persistence.room:runtime:" + versions.architecture
    val RoomRxjava2 = "android.arch.persistence.room:rxjava2:" + versions.architecture
    val RoomCompiler = "android.arch.persistence.room:compiler:" + versions.architecture
    val RoomTesting = "android.arch.persistence.room:testing:" + versions.architecture

    // https://github.com/moove-it/fakeit
    val Fakeit = "com.github.moove-it:fakeit:" + versions.fakeit

    /* https://github.com/afollestad/material-dialogs */
    val MaterialDialogs = "com.afollestad.material-dialogs:core:" + versions.materialDialogs


    // https://github.com/evernote/android-job
    val AndroidJob = "com.evernote:android-job:" + versions.androidjob

    // https://github.com/android/android-ktx
    val AndroidxCore = "androidx.core:core-ktx:" + versions.ktx

    /****** TESTING ****/

    /**
    KotlinTest DSL https://github.com/kotlintest/kotlintest/blob/master/doc/reference.md
    KotlinTest Matchers: https://github.com/kotlintest/kotlintest/blob/master/doc/matchers.md
    Mockito-Kotlin https://github.com/nhaarman/mockito-kotlin/wiki/Mocking-and-verifying
     */
    val KotlinTest = "io.kotlintest:kotlintest:" + versions.kotlintest

    val MockitoKotlin = "com.nhaarman:mockito-kotlin:" + versions.mockitoKotlin


}

