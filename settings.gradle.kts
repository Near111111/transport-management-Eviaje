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
    }

//    versionCatalogs {
//        create("libs") {
//            from("gradle/libs.versions.toml")  // Use this instead of from(files())
//        }
//    }
}

rootProject.name = "WelcomScreen"
include(":app")
