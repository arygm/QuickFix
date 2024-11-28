import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.ktfmt)
    alias(libs.plugins.sonar)
    alias(libs.plugins.gms)
    id("jacoco")
}

android {
    buildFeatures {
        buildConfig = true
        compose = true
        //noinspection DataBindingWithoutKapt
        dataBinding = true
    }
    namespace = "com.arygm.quickfix"
    compileSdk = 34

    // Load the API key from local.properties
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(FileInputStream(localPropertiesFile))
    }

    val mapsApiKey: String = localProperties.getProperty("MAPS_API_KEY") ?: ""
    val sonarToken: String = localProperties.getProperty("SONAR_TOKEN") ?: ""

    defaultConfig {
        applicationId = "com.arygm.quickfix"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
        manifestPlaceholders["MAPS_API_KEY"] = mapsApiKey
        manifestPlaceholders["SONAR_TOKEN"] = sonarToken
    }

    signingConfigs {
        create("release") {
            storeFile = rootProject.file("signIn.jks")
            storePassword = System.getenv("KEYSTORE_PASSWORD") ?: localProperties.getProperty("KEYSTORE_PASSWORD")
            keyAlias = System.getenv("KEY_ALIAS") ?: localProperties.getProperty("KEY_ALIAS")
            keyPassword = System.getenv("KEY_PASSWORD") ?: localProperties.getProperty("KEY_PASSWORD")
        }
    }



    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("release")
        }

        debug {
            enableUnitTestCoverage = true
            enableAndroidTestCoverage = true
        }
    }

    testCoverage {
        jacocoVersion = "0.8.12"
    }



    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE-notice.md"
            excludes += "META-INF/LICENSE.md"// Exclude conflicting LICENSE.md files
        }
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            isReturnDefaultValues = true
        }
    }

    // Robolectric needs to be run only in debug. But its tests are placed in the shared source set (test)
    // The next lines transfers the src/test/* from shared to the testDebug one
    //
    // This prevent errors from occurring during unit tests
    sourceSets.getByName("testDebug") {
        val test = sourceSets.getByName("test")

        java.setSrcDirs(test.java.srcDirs)
        res.setSrcDirs(test.res.srcDirs)
        resources.setSrcDirs(test.resources.srcDirs)
    }

    sourceSets.getByName("test") {
        java.setSrcDirs(emptyList<File>())
        res.setSrcDirs(emptyList<File>())
        resources.setSrcDirs(emptyList<File>())
    }
}

sonar {
    properties {
        property("sonar.projectKey", "arygm_QuickFix")
        property("sonar.projectName", "QuickFix")
        property("sonar.organization", "quickfix")
        property("sonar.host.url", "https://sonarcloud.io")
        // Check if running locally by looking for the GITHUB_ACTIONS environment variable
        if (System.getenv("GITHUB_ACTIONS") == null) {
            // Get the current branch name directly
            val branchName = ByteArrayOutputStream()
            exec {
                commandLine = listOf("git", "symbolic-ref", "--short", "HEAD")
                standardOutput = branchName
            }
            property("sonar.branch.name", branchName.toString().trim())
        }
        // Comma-separated paths to the various directories containing the *.xml JUnit report files. Each path may be absolute or relative to the project base directory.
        property("sonar.junit.reportPaths", "${project.layout.buildDirectory.get()}/test-results/testDebugunitTest/")
        // Paths to xml files with Android Lint issues. If the main flavor is changed, this file will have to be changed too.
        property("sonar.androidLint.reportPaths", "${project.layout.buildDirectory.get()}/reports/lint-results-debug.xml")
        // Paths to JaCoCo XML coverage report files.
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.layout.buildDirectory.get()}/reports/jacoco/jacocoTestReport/jacocoTestReport.xml")
    }
}

// When a library is used both by robolectric and connected tests, use this function
fun DependencyHandlerScope.globalTestImplementation(dep: Any) {
    androidTestImplementation(dep)
    testImplementation(dep)
}
configurations.configureEach {
    exclude(group = "com.google.protobuf", module = "protobuf-lite")
}

dependencies {

    implementation(libs.okhttp)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.security.crypto)
    testImplementation(libs.json)


    implementation(libs.androidx.core.ktx)
    implementation(files("libs/meow-bottom-navigation-java-1.2.0.aar"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.databinding.runtime)
    implementation(libs.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(platform(libs.compose.bom))
    implementation(libs.androidx.navigation.common.ktx)
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.espresso.intents)
    implementation(libs.mockk.android)
    testImplementation(libs.junit)
    globalTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.mockk)
    androidTestImplementation(libs.mockk.android)
    androidTestImplementation(libs.mockk.agent)
    globalTestImplementation(libs.androidx.espresso.core)

    // ------------- Jetpack Compose ------------------
    val composeBom = platform(libs.compose.bom)
    implementation(composeBom)
    globalTestImplementation(composeBom)

    implementation(libs.compose.ui)
    implementation(libs.compose.ui.graphics)
    // Material Design 3
    implementation(libs.compose.material3)
    implementation(libs.androidx.material.icons.extended)
    // Integration with activities
    implementation(libs.compose.activity)
    // Integration with ViewModels
    implementation(libs.compose.viewmodel)
    // Android Studio Preview support
    implementation(libs.compose.preview)
    debugImplementation(libs.compose.tooling)
    // UI Tests
    globalTestImplementation(libs.compose.test.junit)
    debugImplementation(libs.compose.test.manifest)

    // --------- Kaspresso test framework ----------
    globalTestImplementation(libs.kaspresso)
    globalTestImplementation(libs.kaspresso.compose)

    // ----------       Robolectric     ------------
    testImplementation(libs.robolectric)

    // --------- Google Service and Maps -----------
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)
    implementation(libs.maps.compose.utils)
    implementation(libs.play.services.auth)
    implementation(libs.play.services.location)

    // ------------      Firebase      -------------
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.ui.auth)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.auth)

    // ----------       Cucumber         ------------
    testImplementation(libs.cucumber.junit) // JUnit integration for Cucumber
    testImplementation(libs.cucumber.java)  // Cucumber Java integration
    testImplementation(libs.cucumber.android) // Cucumber Kotlin integration
    androidTestImplementation(libs.cucumber.junit)
    androidTestImplementation(libs.cucumber.java)
    androidTestImplementation(libs.cucumber.android)

    // ----------       Mockito         ------------
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.inline)
    testImplementation(libs.mockito.kotlin)
    globalTestImplementation(libs.mockito.kotlin)
    androidTestImplementation(libs.mockito.android)
}

tasks.withType<Test> {
    // Configure Jacoco for each tests
    configure<JacocoTaskExtension> {
        isIncludeNoLocationClasses = true
        excludes = listOf("jdk.internal.*")
    }
}

tasks.register("jacocoTestReport", JacocoReport::class) {
    mustRunAfter("testDebugUnitTest", "connectedDebugAndroidTest")

    reports {
        xml.required = true
        html.required = true
    }

    val fileFilter = listOf(
        "**/R.class",
        "**/R$*.class",
        "**/BuildConfig.*",
        "**/Manifest*.*",
        "**/*Test*.*",
        "android/**/*.*",
    )

    val debugTree = fileTree("${project.layout.buildDirectory.get()}/tmp/kotlin-classes/debug") {
        exclude(fileFilter)
    }

    val mainSrc = "${project.layout.projectDirectory}/src/main/java"
    sourceDirectories.setFrom(files(mainSrc))
    classDirectories.setFrom(files(debugTree))
    executionData.setFrom(fileTree(project.layout.buildDirectory.get()) {
        include("outputs/unit_test_code_coverage/debugUnitTest/testDebugUnitTest.exec")
        include("outputs/code_coverage/debugAndroidTest/connected/*/coverage.ec")
    })
}

tasks.register("connectedCheckWithEmulators") {
    doLast {
        val osName = System.getProperty("os.name").toLowerCase()
        val isWindows = osName.contains("win")

        exec {
            // Set the working directory to the root project directory
            workingDir = rootProject.projectDir

            if (isWindows) {
                // Windows command with logging
                println("Running on Windows")
                commandLine = listOf(
                    "cmd", "/c",
                    "firebase emulators:exec --debug --inspect-functions --project quickfix-1fd34 --import=./end2end-data --only firestore,auth \"gradlew.bat connectedCheck\""
                )
            } else {
                // Unix-like command (macOS, Linux)
                println("Running on Unix-like OS")
                commandLine = listOf(
                    "/bin/sh", "-c",
                    "firebase emulators:exec --debug --inspect-functions --project quickfix-1fd34 --import=./end2end-data --only firestore,auth './gradlew connectedCheck'"
                )
            }

            standardOutput = System.out
            errorOutput = System.err
        }
    }
}
