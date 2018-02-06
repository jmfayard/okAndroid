package com.github.jmfayard

fun common(): CommonComponent =
        CommonComponent.instance ?: DefaultCommonComponent()

interface CommonComponent {
    val logger: ILogger
    val isRunningTest: Boolean

    companion object {
        var instance: CommonComponent? = null
    }
}

data class DefaultCommonComponent(
        override val logger: ILogger = printlnJvmLogger,
        override val isRunningTest: Boolean = true
) : CommonComponent

