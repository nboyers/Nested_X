plugins {
    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.androidApplication)

    @Suppress("DSL_SCOPE_VIOLATION")
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.nobosoftware.nestedx.android"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.nobosoftware.nestedx.android"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildToolsVersion = "34.0.0"
}

dependencies {
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(projects.shared)
    implementation(libs.compose.ui)
    implementation(libs.runtime.livedata)
    implementation(libs.compose.ui.tooling.preview)
    implementation(libs.compose.material3)
    implementation(libs.androidx.activity.compose)
    implementation(libs.play.services.mlkit.text.recognition.common)
    debugImplementation(libs.compose.ui.tooling)
    implementation(libs.play.services.ads.v2260)
    implementation(libs.kotlinx.coroutines.android)
}