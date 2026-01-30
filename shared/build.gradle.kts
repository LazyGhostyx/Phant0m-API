plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "frb.axeron.shared"

    defaultConfig {
        consumerProguardFiles("consumer-rules.pro")

        buildConfigField(
            "String",
            "SERVER_VERSION_NAME",
            "\"${findProperty("api_version_name")}\""
        )
        buildConfigField(
            "int",
            "SERVER_VERSION_CODE",
            "${findProperty("api_version_code")}"
        )
        buildConfigField(
            "int",
            "SERVER_PATCH_CODE",
            "${findProperty("api_version_code")}"
        )
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
}

dependencies {
    implementation(libs.androidx.annotation.jvm)
}

extra["publishLibrary"] = true