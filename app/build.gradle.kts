import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.androidx.navigation.safeargs)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kotlin.parcelize)
}

val localProperties = Properties()
val localPropertiesFile: File = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

android {
    namespace = "com.dicoding.dicoevent"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.dicoding.dicoevent"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        val baseUrl = localProperties.getProperty("BASE_URL") ?: "KEY_TIDAK_DITEMUKAN"
        buildConfigField("String", "BASE_URL", "\"$baseUrl\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    //Shimmer effect
    implementation(libs.shimmer)
    //Image loading
    implementation(libs.glide)
    //Networking
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    //Logger
    implementation(libs.logging.interceptor)
    //Architecture
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.livedata.ktx)
    //Activity and Fragment KTX
    implementation(libs.androidx.fragment.ktx)
    implementation(libs.androidx.activity.ktx)
    // Navigation Component
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    // HtmlTextView
    implementation(libs.html.textview)
    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.room.compiler)
}