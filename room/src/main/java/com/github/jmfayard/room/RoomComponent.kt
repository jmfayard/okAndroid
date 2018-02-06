package com.github.jmfayard.room

import android.app.Application
import android.content.Context

fun room() : RoomComponent {
    return RoomComponent.instance
}
interface RoomComponent {
    val ctx: Context
    val app: Application

    companion object {
        lateinit var instance: RoomComponent
    }
}
