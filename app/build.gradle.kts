plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.trackfit2"
    compileSdk = 35
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }

    defaultConfig {
        applicationId = "com.example.trackfit2"
        minSdk = 24
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
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("com.google.android.material:material:1.9.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    testImplementation(libs.junit)
    androidTestImplementation (libs.robolectric)
    testImplementation ("androidx.test:core:1.6.1")
    testImplementation ("org.mockito:mockito-android:5.0.0")
    androidTestImplementation ("org.mockito:mockito-android:5.0.0")
    testImplementation("org.robolectric:robolectric:4.10.3")
    testImplementation (libs.junit.v115)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

}