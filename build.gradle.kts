plugins {
    kotlin("jvm") version "1.8.22"
    application
}

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(17)
}

dependencies {
    implementation("org.jsoup:jsoup:1.14.3")
}