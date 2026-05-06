package org.vishaldhakad.tarang

class Greeting {
    private val platform = getPlatform()

    fun greet(): String {
        return "Hello, Vishal Dhakad you are using ${platform.name}!"
    }
}