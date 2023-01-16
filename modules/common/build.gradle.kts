plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "org.nudt.common"
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = true
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }
}

dependencies {
    api("androidx.core:core-ktx:1.9.0")
    api("androidx.appcompat:appcompat:1.6.0-rc01")

    api("io.github.androidktx:android-ktx:1.0.0")

    api("com.tencent:mmkv:1.2.13")

    // Retrofit
    api("com.squareup.retrofit2:retrofit:2.9.0")
    api("com.squareup.retrofit2:converter-gson:2.9.0")
    api("com.squareup.okhttp3:okhttp:4.10.0")
    api("com.squareup.okhttp3:logging-interceptor:4.10.0")
    api("com.squareup.okio:okio:3.2.0")
}