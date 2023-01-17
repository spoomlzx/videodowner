plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
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
    //协程
    implementation(libs.coroutines.android)

    // Retrofit
    implementation(libs.bundles.retrofit)


    //room
    implementation(androidx.bundles.room)
    kapt(androidx.room.compiler)

    implementation(androidx.lifecycle.service)
    implementation(androidx.lifecycle.runtimektx)
    implementation(androidx.lifecycle.viewmodelKtx)
}