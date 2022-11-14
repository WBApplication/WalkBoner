// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }

    val kotlin_version = "1.6.20"
    dependencies {
        classpath("com.android.tools.build:gradle:7.3.1")
        classpath("com.google.gms:google-services:4.3.14")
        classpath("com.google.firebase:firebase-crashlytics-gradle:2.9.2")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:2.5.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version")
        classpath("org.jetbrains.kotlin:kotlin-serialization:$kotlin_version")
        classpath("dev.rikka.tools.materialthemebuilder:gradle-plugin:1.3.2")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

/*plugins {
    id("com.android.application" version "7.3.1" apply false)
    id("com.android.application" version "7.3.1" apply false)
    id("com.android.library" version "7.3.1" apply false)
    id("org.jetbrains.kotlin.android" version "1.7.20" apply false)
    id("dev.rikka.tools.materialthemebuilder" version "1.3.3")
}*/

tasks.register<Delete>("Clean") {
    delete(rootProject.buildDir)
}