plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.musicapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.musicapp"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }

    configurations {
        all {
            exclude("org.jetbrains", "annotations")
        }
    }
}

dependencies {
    implementation(libs.gson.di)
    implementation(libs.glide.di)
    implementation(libs.retrofit.di)
    implementation(libs.gson.converter.di)
    implementation(libs.media3.xoplayer.di)
    implementation(libs.media3.media.session.di)
    implementation(libs.media3.common.di)
    implementation(libs.media3.ui.di)
    implementation(libs.room.runtime.di)
    implementation(libs.room.complier.di)
    implementation(libs.room.rxJava3.di)
    implementation(libs.rxjava3.di)
    implementation(libs.rxandroid.di)

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.legacy.support.v4)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}