plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs")
}

android {

    namespace = "win.notoshi.genesec"
    compileSdk = 34

    defaultConfig {
        applicationId = "win.notoshi.genesec"
        minSdk = 27
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures{
        viewBinding = true
    }

}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // ------------------------------------------------------------------------

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.1")

    // ------------------------------------------------------------------------

    implementation("com.google.firebase:firebase-crashlytics-buildtools:2.9.9")

    // ------------------------------------------------------------------------

    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    // ------------------------------------------------------------------------

    // Micronaut tool
    implementation("io.micronaut:micronaut-inject-java:1.1.2")

    // ------------------------------------------------------------------------

    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.4.1")

    // ------------------------------------------------------------------------

    // Fragment Navigation
    val navVersion = "2.7.6"
    implementation("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation("androidx.navigation:navigation-ui-ktx:$navVersion")
    implementation("androidx.navigation:navigation-dynamic-features-fragment:$navVersion")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.navigation:navigation-compose:2.7.6")
    implementation("com.github.clans:fab:1.6.4")

    // ------------------------------------------------------------------------

    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    val secp256k1_version = "0.14.0"
    implementation("fr.acinq.secp256k1:secp256k1-kmp:$secp256k1_version")
    implementation("fr.acinq.secp256k1:secp256k1-kmp-jni-jvm:$secp256k1_version")
    implementation("fr.acinq.bitcoin:bitcoin-kmp:0.18.0")

    // Room Database
    val room_version = "2.6.1"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
    implementation("androidx.room:room-ktx:$room_version")
    implementation("androidx.room:room-rxjava2:$room_version")
    implementation("androidx.room:room-rxjava3:$room_version")
    implementation("androidx.room:room-guava:$room_version")
    testImplementation("androidx.room:room-testing:$room_version")
    implementation("androidx.room:room-paging:$room_version")

    implementation("com.github.thekhaeng:pushdown-anim-click:1.1.1")

}