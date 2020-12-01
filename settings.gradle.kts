rootProject.name = "libsodium-kmp"

include(
    ":native",
    ":jni",
    ":jni:android",
    ":jni:jvm",
    ":jni:jvm:darwin",
    ":jni:jvm:linux",
    ":jni:jvm:mingw",
    ":jni:jvm:all",
    ":tests"
)
