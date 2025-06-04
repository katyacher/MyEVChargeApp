plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.myapplication"
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

    // Настройка для Java-компилятора
    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf(
            "-Xlint:deprecation",  // Показывает устаревшие API
            "-Xlint:unchecked",    // Показывает предупреждения о "raw types"
          //  "-Werror"              // Превращает предупреждения в ошибки (опционально)
        ))
    }

    // Добавьте это для Java-кода:
    tasks.withType<JavaCompile> {
        options.compilerArgs.addAll(listOf("-Xlint:deprecation", "-Xlint:unchecked"))
    }
}

dependencies {

    implementation(libs.appcompat)
    // Material Design
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.google.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Для работы с картами (Yandex Maps)
    implementation(libs.yandex.maps)
    // Retrofit (для API погоды)
    implementation(libs.retrofit)
    implementation(libs.retrofit.gson)
    // Room (для локальной БД)
    implementation(libs.room.runtime)
    annotationProcessor(libs.room.compiler)
    // Навигация между экранами
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("androidx.fragment:fragment:1.6.2")
    // RecyclerView
    implementation ("androidx.recyclerview:recyclerview:1.3.2")
    // ViewModel и LiveData
    implementation ("androidx.lifecycle:lifecycle-viewmodel:2.6.1")
    implementation ("androidx.lifecycle:lifecycle-livedata:2.6.1")

}
