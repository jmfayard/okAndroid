package com.github.jmfayard

fun log(message: String, warn: Boolean = false) {
    val logger = common().logger
    if (warn) logger.warn(message) else logger.log(message)
}

interface ILogger {
    fun log(message: String)
    fun warn(message: String)
}

/** Overriden in [App.onCreate()] to use Timber **/
val printlnJvmLogger: ILogger = object : ILogger {
    override fun log(message: String) = println(message)

    override fun warn(message: String) = System.err.println(message)

}