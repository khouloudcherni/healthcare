plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.tp7"
    compileSdk = 35

            defaultConfig {
                applicationId = "com.example.tp7"
                minSdk = 33
                targetSdk = 35
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
// Firebase
    implementation ("com.google.firebase:firebase-storage:20.3.0")
    implementation ("com.google.android.material:material:1.11.0")
    implementation ("com.google.firebase:firebase-firestore:24.10.0")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation("com.google.firebase:firebase-database:20.3.0")
    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.1 ")
    implementation ("com.github.bumptech.glide:glide:4.12.0")

// Play Services
            implementation("com.google.android.gms:play-services-safetynet:17.0.0")

// AndroidX
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")

// Material Design
    implementation("com.google.android.material:material:1.10.0")

// Jetpack / Activity KTX
    implementation("androidx.activity:activity:1.7.2")

// Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}