plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services") version "4.4.4" apply true //must use apply true
    //if apply false, declare only but don't apply
}

android {
    namespace = "com.example.ewasteapp"
    buildFeatures {
        viewBinding = true
    }
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.ewasteapp"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //import the BoM for the Firebase platform
    implementation(platform("com.google.firebase:firebase-bom:34.8.0"))

    //add dependency for the firebase ai logic library
    implementation("com.google.firebase:firebase-ai")

    //required for one-shot operations (to use ListenableFuture from Guava Android)
    implementation("com.google.guava:guava:31.0.1-android")

    //required for streaming operations (to use 'Publisher' from Reactive Streams)
    implementation("org.reactivestreams:reactive-streams:1.0.4")

    // RecyclerView & CardView
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    // CameraX core library
    implementation("androidx.camera:camera-core:1.5.3")
    implementation("androidx.camera:camera-camera2:1.5.3")
    implementation("androidx.camera:camera-lifecycle:1.5.3")
    implementation("androidx.camera:camera-view:1.5.3")
    implementation(project(":flutter"))
}