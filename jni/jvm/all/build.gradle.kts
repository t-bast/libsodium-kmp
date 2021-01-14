plugins {
    `java-library`
}

dependencies {
    api(project(":jni:jvm:darwin"))
    api(project(":jni:jvm:linux"))
    api(project(":jni:jvm:mingw"))
}
