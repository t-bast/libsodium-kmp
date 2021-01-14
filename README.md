# Libsodium for Kotlin/Multiplatform

Kotlin/Multiplatform wrapper for the [libsodium](https://github.com/jedisct1/libsodium) library.

Targets:

- [ ] JVM
- [ ] Linux native
- [ ] Android
- [ ] iOS

## Building

You can build the project with:

```sh
./gradlew build
```

You can run the test suite for all targets with:

```sh
./gradlew allTests
```

You can list all build and test tasks with:

```sh
./gradlew tasks
```

### Updating JNI headers

If you want to expose a new function from libsodium, you need to update the JNI headers:

1. Add the function definition in `SodiumJNI.java`
2. Run the build script: `./gradlew build`
3. The build script generated a new C header file: `jni/build/generated/jni/org_cybele_sodium_SodiumJNI.h`
4. Replace the existing C headers in `jni/c/headers/java` with the new one
5. Update `jni/c/src/org_cybele_sodium_SodiumJNI.c` with the implementation of the new function
6. Add a new kotlin wrapper for this function in `Sodium.kt`
