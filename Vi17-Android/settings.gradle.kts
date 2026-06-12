pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://repo.picovoice.ai/artifactory/public")
        maven("https://jitpack.io")
    }
}

rootProject.name = "Vi-17"
include(":app")
