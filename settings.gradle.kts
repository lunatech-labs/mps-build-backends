plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version ("0.5.0")
}

rootProject.name = "mps-build-backends"

include("execute-generators")
include("project-loader")
include("modelcheck")
include("integration-tests")

dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            library("mps", "com.jetbrains:mps:2021.2.5")
        }
    }
}
