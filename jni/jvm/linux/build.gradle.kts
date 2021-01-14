plugins {
    kotlin("jvm")
}

dependencies {
    implementation(project(":jni:jvm"))
}

val copyJni by tasks.creating(Sync::class) {
    onlyIf { org.gradle.internal.os.OperatingSystem.current().isLinux }
    dependsOn(":jni:jvm:buildNativeHost")
    from(rootDir.resolve("jni/jvm/build/linux/libsodium-jni.so"))
    into(buildDir.resolve("jniResources/org/cybele/sodium/jni/native/linux-x86_64"))
}

(tasks["processResources"] as ProcessResources).apply {
    onlyIf { org.gradle.internal.os.OperatingSystem.current().isLinux }
    dependsOn(copyJni)
    from(buildDir.resolve("jniResources"))
}
