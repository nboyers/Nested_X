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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
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