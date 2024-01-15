plugins {
    id("com.android.application")
}

android {
    namespace = "com.frissco.magister"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.frissco.magister"
        minSdk = 26
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.preference:preference:1.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation ("org.apache.poi:poi:4.1.2")
    implementation ("org.apache.poi:poi-ooxml:4.1.2")
    implementation ("org.apache.poi:poi-ooxml-schemas:4.1.2")

//    implementation("com.ajts.androidmads.SQLite2Excel:library:1.0.4")
//    implementation("com.theartofdev.edmodo:android-image-cropper:2.7.0")
//    implementation("org.apache.commons:commons-csv:1.8")
//    implementation("com.squareup.picasso:picasso:2.71828")
//    implementation("com.github.dhaval2404:imagepicker:2.1")
//    implementation("com.github.florent37:materialtextfield:1.0.7")
//    implementation("com.apache.poi:poi:5.3.1")
//    implementation("org.apache.poi:poi-ooxml:5.3.1")
}