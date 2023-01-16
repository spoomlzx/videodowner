pluginManagement {
    repositories {
        mavenLocal()
        gradlePluginPortal()
        google()
        maven(url = "https://developer.huawei.com/repo/")
        mavenCentral()
        maven(url = "https://maven.aliyun.com/nexus/content/groups/public/")
        maven(url = "https://maven.aliyun.com/nexus/content/repositories/jcenter")
        maven(url = "https://jitpack.io")
    }
}
dependencyResolutionManagement {
    versionCatalogs {
        create("androidx") {
            from(files("gradle/androidx.versions.toml"))
        }
    }
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven(url = "https://maven.aliyun.com/nexus/content/groups/public/")
        maven(url = "https://developer.huawei.com/repo/")
        maven(url = "https://jitpack.io")
    }
}
rootProject.name = "videodowner"
include(":app")
include(":modules:videoplayer")
include(":modules:downloader")
include(":modules:common")
include(":modules:iplayer")
