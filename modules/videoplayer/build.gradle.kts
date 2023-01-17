plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
}

android {
    compileSdk = libs.versions.compileSdk.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.targetSdk.get().toInt()
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
    api(project(":modules:iplayer"))

    implementation(androidx.corektx)
    implementation(androidx.appcompat)

    implementation(androidx.constraintlayout)
    implementation(androidx.recyclerview)

    implementation(libs.coil.compose)

    //EXO解码器
    api(libs.bundles.exoplayer)

    implementation(libs.cling.library)

    implementation(libs.xpopup)
}