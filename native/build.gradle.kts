import org.gradle.internal.os.OperatingSystem

val currentOs = OperatingSystem.current()
val bash = if (currentOs.isWindows) "bash.exe" else "bash"

val buildLibsodium by tasks.creating { group = "build" }

// Build libsodium for the host platform's architecture (linux/macOS/windows).
val buildLibsodiumHost by tasks.creating(Exec::class) {
    group = "build"
    buildLibsodium.dependsOn(this)

    val target = when {
        currentOs.isLinux -> "linux"
        currentOs.isMacOsX -> "darwin"
        currentOs.isWindows -> "mingw"
        else -> error("Unsupported OS $currentOs")
    }

    inputs.files(projectDir.resolve("build.sh"))
    outputs.dir(projectDir.resolve("build/$target"))

    workingDir = projectDir
    environment("TARGET", target)
    commandLine(bash, "build.sh")
}

val clean by tasks.creating {
    group = "build"
    doLast {
        delete(projectDir.resolve("build"))
    }
}
