import org.gradle.internal.os.OperatingSystem
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    kotlin("multiplatform") version "1.4.20"
}

buildscript {
    repositories {
        google()
        jcenter()
    }
}

allprojects {
    group = "org.cybele.sodium"
    version = "0.1.0"

    repositories {
        jcenter()
        google()
    }
}

val currentOs = OperatingSystem.current()!!

kotlin {
    // Turn on additional compiler checks for library authors:
    // https://kotlinlang.org/docs/reference/whatsnew14.html#explicit-api-mode-for-library-authors
    explicitApi()

    val commonMain by sourceSets.getting
    val commonTest by sourceSets.getting {
        dependencies {
            implementation(kotlin("test-common"))
            implementation(kotlin("test-annotations-common"))
        }
    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "1.8"
        }
    }

    val jvmMain by sourceSets.getting
    val jvmTest by sourceSets.getting {
        dependencies {
            implementation(project(":jni:jvm:all"))
            implementation(kotlin("test-junit"))
        }
    }

    fun KotlinNativeTarget.libsodiumCInterop(target: String) {
        compilations["main"].cinterops {
            val libsodium by creating {
                includeDirs.headerFilterOnly(
                    project.file("native/libsodium/src/libsodium/include/"),
                    // https://youtrack.jetbrains.com/issue/KT-43501
                    project.file("/usr/include/")
                )
                tasks[interopProcessingTaskName].dependsOn(":native:buildLibsodium${target.capitalize()}")
            }
        }
    }

    val nativeMain by sourceSets.creating { dependsOn(commonMain) }

    linuxX64("linux") {
        libsodiumCInterop("host")
        compilations["main"].defaultSourceSet.dependsOn(nativeMain)
        // https://youtrack.jetbrains.com/issue/KT-39396
        compilations["main"].kotlinOptions.freeCompilerArgs += listOf("-include-binary", "$rootDir/native/build/linux/libsodium.a")
    }

    sourceSets.all {
        // We want to restrict the use of experimental annotations as much as possible.
        languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
    }
}

// We disable cross compilation and will instead build on each platform in the CI.
// This is more robust, as cross-compilation could easily be broken and we wouldn't notice it until we run the tests on the broken platforms.
allprojects {
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        afterEvaluate {
            val targets = when {
                currentOs.isLinux -> listOf()
                currentOs.isMacOsX -> listOf("linux")
                currentOs.isWindows -> listOf("linux")
                else -> listOf("linux")
            }.mapNotNull {
                kotlin.targets.findByName(it) as? KotlinNativeTarget
            }

            configure(targets) {
                compilations.all {
                    cinterops.all {
                        tasks[interopProcessingTaskName].enabled = false
                    }
                    compileKotlinTask.enabled = false
                    tasks[processResourcesTaskName].enabled = false
                }
                binaries.all { linkTask.enabled = false }
            }
        }
    }
}
