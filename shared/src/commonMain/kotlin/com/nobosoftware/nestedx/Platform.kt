package com.nobosoftware.nestedx

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform