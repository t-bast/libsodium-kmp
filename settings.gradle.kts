rootProject.name = "sodium-kmp"

include(
    ":native",
    ":jni",
    ":jni:jvm",
    ":jni:jvm:darwin",
    ":jni:jvm:linux",
    ":jni:jvm:mingw",
    ":jni:jvm:all"
)