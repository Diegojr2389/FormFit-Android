import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
}

// Read local.properties
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localPropertiesFile.inputStream().use { localProperties.load(it) }
}
val baseUrl = localProperties["BASE_URL"] as String? ?: "http://10.0.0.101:8000/"

android {
    namespace = "com.example.formfit"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.formfit"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField("String", "BASE_URL", "\"${baseUrl}\"")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

val camerax_version = "1.5.3"
dependencies {
    implementation("androidx.camera:camera-core:${camerax_version}")
    implementation("androidx.camera:camera-camera2:${camerax_version}")
    implementation("androidx.camera:camera-lifecycle:${camerax_version}")
    implementation("androidx.camera:camera-video:${camerax_version}")
    implementation("androidx.camera:camera-view:${camerax_version}")
    implementation("androidx.camera:camera-extensions:${camerax_version}")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    implementation("androidx.navigation:navigation-compose:2.9.7")
    // base sdk
//    implementation("com.google.mlkit:pose-detection:18.0.0-beta5")
    // accurate sdk
    implementation("com.google.mlkit:pose-detection-accurate:18.0.0-beta5")
    // For more icons
    implementation("androidx.compose.material:material-icons-extended")

    // for wrapping
    implementation("com.google.accompanist:accompanist-flowlayout:0.36.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:3.0.0")

    // Gson Converter
    implementation("com.squareup.retrofit2:converter-gson:3.0.0")

    // For Navigation Bar styling
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.36.0")

    // Preferences DataStore (SharedPreferences like APIs)
    implementation("androidx.datastore:datastore-preferences:1.2.1")

    // Coil library - Standard way of displaying images in Compose
    implementation("io.coil-kt.coil3:coil-compose:3.4.0")

    // Fetching images from the network
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.4.0")
}