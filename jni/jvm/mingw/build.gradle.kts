plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":jni:jvm"))
}

val copyJni by tasks.creating(Sync::class) {
    onlyIf { org.gradle.internal.os.OperatingSystem.current().isWindows }
    dependsOn(":jni:jvm:buildNativeHost")
    from(rootDir.resolve("jni/jvm/build/mingw/sodium-jni.dll"))
    into(buildDir.resolve("jniResources/org/cybele/sodium/jni/native/mingw-x86_64"))
}

(tasks["processResources"] as ProcessResources).apply {
    onlyIf { org.gradle.internal.os.OperatingSystem.current().isWindows }
    dependsOn(copyJni)
    from(buildDir.resolve("jniResources"))
}
