import com.android.build.api.dsl.Packaging

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "ru.skillbranch.skillarticles"
    compileSdk = 34

    defaultConfig {
        applicationId = "ru.skillbranch.skillarticles"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "ru.skillbranch.skillarticles.DexopenerJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        compose = false
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

//    implementation("androidx.core:core-ktx:1.9.0")
//    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
//    implementation("androidx.activity:activity-compose:1.7.0")
//    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
//    implementation("androidx.appcompat:appcompat:1.6.1")
//    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")
//    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
//    implementation("com.google.android.material:material:1.10.0")
//    implementation("androidx.datastore:datastore-core:1.0.0")
//    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha08")
//    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
//
//    implementation("com.github.bumptech.glide:glide:4.12.0")
//    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
//
//
//    testImplementation("junit:junit:4.13.2")
//    androidTestImplementation("androidx.test.ext:junit:1.1.5")
//    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
//    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
//    debugImplementation("androidx.compose.ui:ui-tooling")
//    debugImplementation("androidx.compose.ui:ui-test-manifest")
//
//    testImplementation("io.mockk:mockk-android:1.11.0")
//    androidTestImplementation("io.mockk:mockk-android:1.11.0")
//
//    testImplementation("com.github.tmurakami:dexopener:2.0.5")
//
//    testImplementation("junit:junit:4.13.2")
//    debugImplementation("androidx.test.ext:junit:1.1.5")
//    debugImplementation("androidx.fragment:fragment-testing:1.6.2")
//    debugImplementation("androidx.test.espresso:espresso-core:3.5.1")
//    debugImplementation("androidx.test:core:1.5.0")
//    debugImplementation("androidx.test:runner:1.5.2")
//    debugImplementation("androidx.test:rules:1.5.0")
//    androidTestImplementation("com.jraska.livedata:testing-ktx:1.3.0")
//    androidTestImplementation("android.arch.core:core-testing:1.1.1")
//    androidTestImplementation("com.github.tmurakami:dexopener:2.0.5")

    //dependencies
    implementation("androidx.appcompat:appcompat:1.3.1")
    implementation("androidx.core:core-ktx:1.6.0")
    implementation("androidx.constraintlayout:constraintlayout:2.0.4")
    //lifecycle
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.3.1")
    //ui
    implementation("androidx.fragment:fragment-ktx:1.3.6")
    implementation("com.google.android.material:material:1.4.0")
    //coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.5.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1")
    //data store
    implementation("androidx.datastore:datastore-preferences:1.0.0-rc02")
    //glide
    implementation("com.github.bumptech.glide:glide:4.12.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    //navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.4.0-alpha05")
    implementation("androidx.navigation:navigation-ui-ktx:2.4.0-alpha05")

    implementation("androidx.paging:paging-runtime-ktx:3.0.1")
    testImplementation ("junit:junit:4.13.2")
    debugImplementation ("androidx.test.ext:junit:1.1.3")
    debugImplementation("androidx.fragment:fragment-testing:1.3.6")
    debugImplementation ("androidx.test.espresso:espresso-core:3.4.0")
    debugImplementation ("androidx.test:core:1.4.0")
    debugImplementation ("androidx.test:runner:1.4.0")
    debugImplementation ("androidx.test:rules:1.4.0")
    androidTestImplementation ("com.jraska.livedata:testing-ktx:1.3.0")
    androidTestImplementation ("android.arch.core:core-testing:1.1.1")
    androidTestImplementation ("io.mockk:mockk-android:1.12.0")
    androidTestImplementation ("com.github.tmurakami:dexopener:2.0.5")

}