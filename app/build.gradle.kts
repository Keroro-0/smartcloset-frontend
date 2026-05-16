plugins {
    // Usa los alias que ya tienes definidos en el Version Catalog (libs.versions.toml)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // Para Google Services, si no lo tienes en el TOML, se deja así:
    id("com.google.gms.google-services")
}

android {
    namespace = "com.angel.smartcloset"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.angel.smartcloset"
        minSdk = 26
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    // --- LIBRERÍAS BASE ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    // --- NAVIGATION ---
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // --- FIREBASE (Usa solo una versión del BOM, la más reciente) ---
    implementation(platform("com.google.firebase:firebase-bom:34.12.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // Necesario para que el login de Google funcione
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // --- LIBRERÍAS DE SOPORTE ---
    implementation("androidx.compose.material:material-icons-extended")
    // Esta es VITAL para que el ".await()" de tu ViewModel funcione
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.0")

    // --- CAMERAX ---
    val cameraxVersion = "1.4.0"
    implementation("androidx.camera:camera-core:$cameraxVersion")
    implementation("androidx.camera:camera-camera2:$cameraxVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraxVersion")
    implementation("androidx.camera:camera-view:$cameraxVersion")
    implementation("androidx.camera:camera-extensions:$cameraxVersion")

    // --- RETROFIT ---
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // --- TESTING ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)


    implementation("com.google.android.gms:play-services-location:21.2.0") // Para la ubicación

    implementation("io.coil-kt:coil-compose:2.6.0")

    implementation("com.google.guava:guava:31.1-android")

}