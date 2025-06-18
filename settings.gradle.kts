//enableFeaturePreview("VERSION_CATALOGS")
pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    dependencyResolutionManagement {
        repositories {
            google()
            mavenCentral()
        }
    }

}
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
//    repositories {
//        google()
//        mavenCentral()
//    }
//    }


rootProject.name = "SmartPark"
include(":app")
 