package com.github.jmfayard.screens

fun <T> T.debug(name: String): T {
    println("DEBUG: ${name} = ${toString()}")
    return this
}

fun <T> List<T>.debugList(name: String): List<T> {
    println("<List $name with $size elements>")
    forEachIndexed { i, t ->
        println("$name[$i] : $t")
    }
    println("</List $name>")
    return this
}

inline fun <K, V> Map<K, V>.printMap(name: String): Map<K, V> {
    println("<Map name=$name size=$size>")
    for ((k, v) in this) {
        println("$name[$k] : $v")
    }
    println("</Map name=$name size=$size>")
    return this
}