plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    explicitApi()
}

dependencies {
    api(rootProject)
}

val generateHeaders by tasks.creating(JavaCompile::class) {
    group = "build"
    classpath = sourceSets["main"].compileClasspath
    destinationDir = file("${buildDir}/generated/jni")
    source = sourceSets["main"].java
    options.compilerArgs = listOf(
        "-h", file("${buildDir}/generated/jni").absolutePath,
        "-d", file("${buildDir}/generated/jni-tmp").absolutePath
    )
    doLast {
        delete(file("${buildDir}/generated/jni-tmp"))
    }
}
