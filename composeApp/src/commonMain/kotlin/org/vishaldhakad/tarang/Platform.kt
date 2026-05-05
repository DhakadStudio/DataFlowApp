package org.vishaldhakad.tarang

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform