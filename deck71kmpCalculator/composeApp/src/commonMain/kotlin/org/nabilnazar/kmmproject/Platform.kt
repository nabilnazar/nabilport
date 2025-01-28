package org.nabilnazar.kmmproject

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform