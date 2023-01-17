plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-parcelize")
    id("kotlin-android")
    id("kotlin-kapt")
}

val SUPPORTED_ABIS = setOf("armeabi-v7a", "arm64-v8a")

android {
    namespace = "org.nudt.player"
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "org.nudt.videodowner"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
        versionCode = 2
        versionName = "1.0.1"
        ndk {
            abiFilters += SUPPORTED_ABIS
        }
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        named("release") {
            isShrinkResources = true
            isMinifyEnabled = true
            proguardFiles("proguard-android-optimize.txt", "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }


    buildFeatures {
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
}

dependencies {
    implementation(project(":modules:videoplayer"))
    implementation(project(":modules:downloader"))
    implementation(project(":modules:common"))


    implementation(androidx.corektx)
    implementation(androidx.appcompat)
    implementation(androidx.material)

    implementation(androidx.constraintlayout)
    implementation(androidx.recyclerview)
    implementation(androidx.cardview)
    implementation(androidx.viewpager2)
    implementation(androidx.coordinatorlayout)
    implementation(androidx.swiperefreshlayout)


    // Optional - Integration with activities
    implementation(androidx.compose.activity)
    implementation(androidx.bundles.compose)

    implementation(androidx.bundles.accompanist)

    implementation(libs.lottie)

    implementation(libs.coil.compose)
    implementation(libs.coil.svg)

    //implementation "com.github.AnJiaoDe:TabLayoutNiubility:V1.3.0"
    implementation(libs.easy.navigation)

    implementation(libs.xpopup)

    implementation(libs.bundles.pictureselector)

    implementation(libs.huawei.scanplus)

    implementation(libs.koin.android)

    implementation(libs.coroutines.android)
    implementation(libs.coroutines.core)

    // ViewModel
    implementation(androidx.lifecycle.viewmodelKtx)
    // Optional - Integration with ViewModels
    implementation(androidx.lifecycle.viewmodel.compose)
    // LiveData
    implementation(androidx.lifecycle.viewmodelKtx)

    // Room
    implementation(androidx.bundles.room)
    kapt(androidx.room.compiler)
    implementation(androidx.room.paging)

    // paging
    implementation(androidx.paging.runtime)
    implementation(androidx.paging.compose)

    // test
    testImplementation(libs.junit)
}