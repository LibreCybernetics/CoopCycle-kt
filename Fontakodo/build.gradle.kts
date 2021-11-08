apply(plugin = "kotlinx-serialization")

buildscript {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:7.1.0-beta02")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.5.31")
        classpath("org.jetbrains.kotlin:kotlin-serialization:1.5.31")
    }
}

tasks.create<Delete>("clean") {
    delete = setOf (
        rootProject.buildDir
    )
}
