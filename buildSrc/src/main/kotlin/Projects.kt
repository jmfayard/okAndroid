import com.android.build.gradle.internal.dsl.DefaultConfig
import org.gradle.api.artifacts.dsl.RepositoryHandler

fun DefaultConfig.addResValues(
        vararg entries: Pair<String, String>
) {
    for ((key, value) in entries) {
        resValue("string", key, value)
    }
}


fun RepositoryHandler.maven(
        url: String,
        name: String? = null,
        username: String? = null,
        password: String? = null,
        configure: RepositoryHandler.() -> Unit = {}
) {
    this.maven {
        setUrl(url)
        if (name != null) setName(name)
        if (username != null && password != null) {
            credentials {
                setUsername(username)
                setPassword(password)

            }
        }
        configure()
    }
}

