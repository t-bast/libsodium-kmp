import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("jvm")
}

val currentOs = OperatingSystem.current()!!
val bash = if (currentOs.isWindows) "bash.exe" else "bash"

val buildNativeHost by tasks.creating(Exec::class) {
    group = "build"
    dependsOn(":jni:generateHeaders")
    dependsOn(":native:buildLibsodiumHost")

    val target = when {
        currentOs.isLinux -> "linux"
        currentOs.isMacOsX -> "darwin"
        currentOs.isWindows -> "mingw"
        else -> error("Unsupported OS $currentOs")
    }

    inputs.files(projectDir.resolve("build.sh"))
    outputs.dir(buildDir.resolve(target))

    workingDir = projectDir
    environment("TARGET", target)
    commandLine(bash, "build.sh")
}

dependencies {
    api(project(":jni"))
}

afterEvaluate {
    tasks["clean"].doLast {
        delete("$buildDir/build/cmake")
    }
}
