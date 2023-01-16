plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = 33

    defaultConfig {
        minSdk = 26
        targetSdk = 33
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
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
}

dependencies {
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.0-rc01")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

    implementation("androidx.recyclerview:recyclerview:1.3.0-rc01")

    implementation("com.gitee.spoomlan.talentmov-master:clinglibrary:1.0.1")

    implementation("com.github.li-xiaojun:XPopup:2.9.2")

    //api 'com.github.hty527.iPlayer:iplayer:2.1.1'
    // 临时引入包，修改BasePlayer 方便布局预览

    implementation("io.coil-kt:coil-compose:2.2.2")

    api(project(":modules:iplayer"))

    //EXO解码器
    api("com.google.android.exoplayer:exoplayer:2.18.2")
    api("com.google.android.exoplayer:exoplayer-core:2.18.2")
    api("com.google.android.exoplayer:exoplayer-dash:2.18.2")
    api("com.google.android.exoplayer:extension-rtmp:2.18.2")
    api("com.google.android.exoplayer:exoplayer-hls:2.18.2")
    api("com.google.android.exoplayer:exoplayer-rtsp:2.18.2")
    api("com.google.android.exoplayer:exoplayer-transformer:2.18.1")
}