plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// FOSS/direct-distribution build: use Gradle’s standard debug signing key for the
// release variant. No private keystore or GitHub signing secrets are required.

android {
    namespace = "com.infinity.comicreader"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.infinity.comicreader"
        minSdk = 26
        targetSdk = 34
        versionCode = 12
        versionName = "2.1"
        vectorDrawables { useSupportLibrary = true }
    }


    buildTypes {
        release {
            // Signed for direct installation without a private release keystore.
            // For durable app upgrades across different machines, use a stable private key.
            signingConfig = signingConfigs.getByName("debug")
            isMinifyEnabled = true
            isShrinkResources = true
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

    packaging {
        resources.excludes += setOf(
            "META-INF/DEPENDENCIES",
            "META-INF/LICENSE*",
            "META-INF/NOTICE*",
            "META-INF/*.kotlin_module"
        )
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.viewpager2:viewpager2:1.1.0")
    implementation("androidx.preference:preference-ktx:1.2.1")
    implementation("androidx.activity:activity-ktx:1.9.2")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("androidx.documentfile:documentfile:1.0.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Archive support
    implementation("com.github.junrar:junrar:8.1.2")          // RAR / CBR (RAR4 + RAR5/7)
    implementation("org.apache.commons:commons-compress:1.26.2") // 7z / CB7
    implementation("commons-io:commons-io:2.16.1")
    implementation("org.tukaani:xz:1.9")
    implementation("org.slf4j:slf4j-nop:2.0.17")              // silence junrar/slf4j on Android
    // PDF uses the platform android.graphics.pdf.PdfRenderer (no dependency)
}
