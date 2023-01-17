// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        classpath(libs.com.huawei.agconnect)
    }
}

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(androidx.plugins.application) apply false
    alias(androidx.plugins.library) apply false
    alias(libs.plugins.android) apply  false
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}