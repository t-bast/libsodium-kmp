plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":jni:jvm"))
}

val copyJni by tasks.creating(Sync::class) {
    onlyIf { org.gradle.internal.os.OperatingSystem.current().isMacOsX }
    dependsOn(":jni:jvm:buildNativeHost")
    from(rootDir.resolve("jni/jvm/build/darwin/libsodium-jni.dylib"))
    into(buildDir.resolve("jniResources/org/cybele/sodium/jni/native/darwin-x86_64"))
}

(tasks["processResources"] as ProcessResources).apply {
    onlyIf { org.gradle.internal.os.OperatingSystem.current().isMacOsX }
    dependsOn(copyJni)
    from(buildDir.resolve("jniResources"))
}
