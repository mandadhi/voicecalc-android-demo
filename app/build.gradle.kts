plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    kotlin("kapt")
}

android {
    namespace = "com.mandadhi.voicecalc"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.mandadhi.voicecalc"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        // For quick testing you can inject your Gemini API key here.
        buildConfigField("String", "GEMINI_API_KEY", "\"\"")
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions { kotlinCompilerExtensionVersion = "1.5.3" }

    packagingOptions {
        resources.excludes.add("META-INF/LICENSE*")
        resources.excludes.add("META-INF/DEPENDENCIES")
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.10.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.compose.ui:ui:1.5.0")
    implementation("androidx.compose.material:material:1.5.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    // math evaluator
    implementation("org.mariuszgromada.math:MathParser.org-mXparser:4.4.2")

    // HTTP
    implementation("com.squareup.okhttp3:okhttp:4.11.0")

    // Room
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // Play Billing skeleton
    implementation("com.android.billingclient:billing:6.0.1")

    // tooling
    debugImplementation("androidx.compose.ui:ui-tooling:1.5.0")
}
