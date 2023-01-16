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
    compileSdk = 33

    defaultConfig {
        applicationId = "org.nudt.videodowner"
        minSdk = 26
        targetSdk = 33
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

val koin_version = "3.2.0"
val lifecycle_version = "2.5.1"
val room_version = "2.5.0-rc01"
val coroutinesVersion = "1.6.4"
val paging_version = "3.1.1"
val nav_version = "2.3.5"
val lottieVersion = "5.2.0"
val picture_selector_version = "v3.10.7"
val compose_ui_version = "1.4.0-alpha03"
val accompanist_version = "0.28.0"

dependencies {
    implementation(project(":modules:videoplayer"))
    implementation(project(":modules:downloader"))
    implementation(project(":modules:common"))


    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.0-rc01")
    implementation("com.google.android.material:material:1.8.0-rc01")

    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.0-rc01")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")


    // Optional - Integration with activities
    implementation("androidx.activity:activity-compose:1.6.1")


    // Optional - Integration with LiveData
    implementation("androidx.compose.runtime:runtime-livedata:$compose_ui_version")
    // Android Studio Preview support
    implementation("androidx.compose.foundation:foundation")
    implementation("androidx.compose.ui:ui-tooling:$compose_ui_version")
    implementation("androidx.compose.ui:ui-util:$compose_ui_version")
    implementation("androidx.compose.animation:animation:$compose_ui_version")
    implementation("androidx.compose.animation:animation-graphics:$compose_ui_version")

    implementation("io.coil-kt:coil-compose:2.2.2")
    implementation("io.coil-kt:coil-svg:2.2.2")


    implementation("com.google.accompanist:accompanist-webview:$accompanist_version")
    implementation("com.google.accompanist:accompanist-flowlayout:$accompanist_version")
    implementation("com.google.accompanist:accompanist-permissions:$accompanist_version")
    implementation("com.google.accompanist:accompanist-themeadapter-material:$accompanist_version")
    implementation("com.google.accompanist:accompanist-systemuicontroller:$accompanist_version")
    implementation("com.google.accompanist:accompanist-pager:$accompanist_version")

    implementation("androidx.compose.material:material:$compose_ui_version")


    implementation("com.airbnb.android:lottie:$lottieVersion")

    //implementation "com.github.AnJiaoDe:TabLayoutNiubility:V1.3.0"
    implementation("com.github.Vincent7Wong:EasyNavigation:2.0.5")

    implementation("com.github.li-xiaojun:XPopup:2.9.2")

    // PictureSelector basic (Necessary)
    implementation("io.github.lucksiege:pictureselector:$picture_selector_version")
    // image compress library (Not necessary)
    implementation("io.github.lucksiege:compress:$picture_selector_version")
    // uCrop library (Not necessary)
    implementation("io.github.lucksiege:ucrop:$picture_selector_version")
    // simple camerax library (Not necessary)
    implementation("io.github.lucksiege:camerax:$picture_selector_version")

    implementation("com.huawei.hms:scanplus:2.9.0.300")


    implementation("io.insert-koin:koin-android:$koin_version")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    // Optional - Integration with ViewModels
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    // LiveData
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")

    // Room
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-runtime:$room_version")
    kapt("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-paging:$room_version")

    // paging
    implementation("androidx.paging:paging-runtime-ktx:$paging_version")
    testImplementation("androidx.paging:paging-common-ktx:$paging_version")

    // test
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
}