plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("kotlin-android")
    id("kotlin-parcelize")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
    id("kotlinx-serialization")
    id("dev.rikka.tools.materialthemebuilder")
}

apply(plugin = "com.google.firebase.crashlytics")

android {
    compileSdk = 33

    defaultConfig {
        applicationId = "com.fusoft.walkboner"
        minSdk = 24
        targetSdk = 33
        versionCode = 4
        versionName = "1.0.0.4 BETA"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        dataBinding = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_11)
        targetCompatibility(JavaVersion.VERSION_11)
    }
    namespace = "com.fusoft.walkboner"
}

materialThemeBuilder {
    themes {

        create("MaterialWalkBoner") {
            isDynamicColors = true
            generateTextColors = true

            lightThemeFormat = "ThemeOverlay.Light.%s"
            lightThemeParent = "AppTheme"
            darkThemeFormat = "ThemeOverlay.Dark.%s"
            darkThemeParent = "AppTheme"
        }
    }
}


    dependencies {
        // Firebase
        implementation("com.google.firebase:firebase-auth-ktx:21.1.0")
        implementation("com.google.firebase:firebase-firestore-ktx:24.4.0")
        implementation("com.google.firebase:firebase-storage-ktx:20.1.0")
        implementation("com.google.firebase:firebase-crashlytics-ktx:18.3.2")
        implementation("com.google.firebase:firebase-analytics-ktx:21.2.0")
        implementation("com.google.firebase:firebase-appcheck-ktx:16.1.0")
        implementation("com.google.firebase:firebase-appcheck-safetynet:16.1.0")

        // Kotlin
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.4")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.0")

        // Android
        implementation("androidx.core:core-ktx:1.9.0")
        implementation("androidx.appcompat:appcompat:1.5.1")
        implementation("androidx.activity:activity-ktx:1.6.1")
        implementation("androidx.biometric:biometric:1.1.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.fragment:fragment-ktx:1.5.4")
        implementation("androidx.preference:preference-ktx:1.2.0")
        implementation("androidx.vectordrawable:vectordrawable:1.1.0")
        implementation("androidx.navigation:navigation-runtime-ktx:2.5.3")
        implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
        implementation("androidx.navigation:navigation-ui-ktx:2.5.3")
        implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.5.1")
        implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.5.1")
        implementation("androidx.lifecycle:lifecycle-common-java8:2.5.1")
        implementation("androidx.legacy:legacy-support-v4:1.0.0")
        implementation("androidx.work:work-runtime-ktx:2.7.1")
        implementation("androidx.recyclerview:recyclerview-selection:1.1.0")
        implementation("androidx.transition:transition-ktx:1.4.1")
        implementation("com.google.android.material:material:1.8.0-alpha01")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")


        // Others
        implementation("com.github.YvesCheung.RollingText:RollingText:1.2.11")
        implementation("com.liulishuo.filedownloader:library:1.7.7")
        implementation("io.github.yanndroid:oneui:2.4.1")
        implementation("com.airbnb.android:lottie:5.2.0")
        implementation("com.github.Dimezis:BlurView:version-2.0.0")
        implementation("kr.co.prnd:readmore-textview:1.0.0")
        implementation("com.github.bumptech.glide:glide:4.14.2")
        implementation("com.github.virtualvivek:BlurShadowImageView:4.0")
        implementation("com.mikhaellopez:circularimageview:4.3.0")
        implementation("com.google.android.exoplayer:exoplayer:2.18.1")
        implementation("com.github.MikeOrtiz:TouchImageView:3.2.1")
        implementation("com.google.code.gson:gson:2.9.1")
        implementation("com.github.Drjacky:ImagePicker:2.3.20")
        implementation("com.google.android.gms:play-services-ads:21.3.0")
        implementation("com.squareup.picasso:picasso:2.8")
        implementation("com.squareup.okhttp3:okhttp:4.10.0")
        implementation(project(mapOf("path" to ":otptextview")))
        implementation(project(mapOf("path" to ":dotsindicator")))
        debugImplementation("com.squareup.leakcanary:leakcanary-android:2.9.1")
        annotationProcessor("com.github.bumptech.glide:compiler:4.13.2")
        testImplementation("junit:junit:4.13.2")
        androidTestImplementation("androidx.test.ext:junit:1.1.3")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    }