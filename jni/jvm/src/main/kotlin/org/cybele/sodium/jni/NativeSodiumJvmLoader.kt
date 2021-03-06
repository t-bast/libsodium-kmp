package org.cybele.sodium.jni

import org.cybele.sodium.NativeSodium
import org.cybele.sodium.Sodium
import java.io.*
import java.util.*

public object NativeSodiumJvmLoader {
    private var extracted = false

    /**
     * Loads sodium native library.
     *
     * @return True if sodium native library is successfully loaded; false otherwise.
     * @throws Exception if loading fails
     */
    @JvmStatic
    @Synchronized
    @Throws(Exception::class)
    public fun load(): Sodium {
        // only cleanup before the first extract
        if (!extracted) {
            cleanup()
        }
        loadSodiumNativeLibrary()
        return NativeSodium
    }

    private val tempDir: File get() = File(System.getProperty("org.cybele.sodium.tmpdir", System.getProperty("java.io.tmpdir")))

    /**
     * Deleted old native libraries e.g. on Windows the DLL file is not removed on VM-Exit (bug #80)
     */
    @JvmStatic
    public fun cleanup() {
        val tempFolder = tempDir.absolutePath
        val dir = File(tempFolder)
        val nativeLibFiles = dir.listFiles(object : FilenameFilter {
            private val searchPattern = "sodium-"
            override fun accept(dir: File, name: String): Boolean {
                return name.startsWith(searchPattern) && !name.endsWith(".lck")
            }
        })
        if (nativeLibFiles != null) {
            for (nativeLibFile in nativeLibFiles) {
                val lckFile = File(nativeLibFile.absolutePath + ".lck")
                if (!lckFile.exists()) {
                    try {
                        nativeLibFile.delete()
                    } catch (e: SecurityException) {
                        System.err.println("Failed to delete old native lib" + e.message)
                    }
                }
            }
        }
    }

    @Throws(IOException::class)
    private fun InputStream.contentsEquals(other: InputStream): Boolean {
        val bufThis = this as? BufferedInputStream ?: BufferedInputStream(this)
        val bufOther = other as? BufferedInputStream ?: BufferedInputStream(other)
        var ch = bufThis.read()
        while (ch != -1) {
            val ch2 = bufOther.read()
            if (ch != ch2) return false
            ch = bufThis.read()
        }
        val ch2 = bufOther.read()
        return ch2 == -1
    }

    /**
     * Extracts and loads the specified library file to the target folder
     *
     * @param libDir library path.
     * @param libFileName library name.
     * @param targetDirectory target folder.
     */
    private fun extractAndLoadLibraryFile(libDir: String, libFileName: String, targetDirectory: String): Boolean {
        val libPath = "$libDir/$libFileName"
        // Include architecture name in temporary filename in order to avoid conflicts
        // when multiple JVMs with different architectures running at the same time
        val uuid = UUID.randomUUID().toString()
        val extractedLibFileName = String.format("sodium-%s-%s", uuid, libFileName)
        val extractedLckFileName = "$extractedLibFileName.lck"
        val extractedLibFile = File(targetDirectory, extractedLibFileName)
        val extractedLckFile = File(targetDirectory, extractedLckFileName)
        return try {
            // Extract a native library file into the target directory
            val reader = NativeSodiumJvmLoader::class.java.getResourceAsStream(libPath)
            if (!extractedLckFile.exists()) {
                FileOutputStream(extractedLckFile).close()
            }
            val writer = FileOutputStream(extractedLibFile)
            try {
                val buffer = ByteArray(8192)
                var bytesRead = reader.read(buffer)
                while (bytesRead != -1) {
                    writer.write(buffer, 0, bytesRead)
                    bytesRead = reader.read(buffer)
                }
            } finally {
                // Delete the extracted lib file on JVM exit.
                extractedLibFile.deleteOnExit()
                extractedLckFile.deleteOnExit()
                writer.close()
                reader.close()
            }

            // Set executable (x) flag to enable Java to load the native library
            extractedLibFile.setReadable(true)
            extractedLibFile.setWritable(true, true)
            extractedLibFile.setExecutable(true)

            // Check whether the contents are properly copied from the resource folder
            NativeSodiumJvmLoader::class.java.getResourceAsStream(libPath).use { nativeIn ->
                FileInputStream(extractedLibFile).use { extractedLibIn ->
                    if (!nativeIn.contentsEquals(extractedLibIn)) {
                        throw RuntimeException(String.format("Failed to write a native library file at %s", extractedLibFile))
                    }
                }
            }

            loadNativeLibrary(targetDirectory, extractedLibFileName)
        } catch (e: IOException) {
            System.err.println(e.message)
            false
        }
    }

    /**
     * Loads native library using the given path and name of the library.
     *
     * @param path path of the native library.
     * @param name name of the native library.
     * @return True for successful loading; false otherwise.
     */
    private fun loadNativeLibrary(path: String, name: String): Boolean {
        val libPath = File(path, name)
        return if (libPath.exists()) {
            try {
                System.load(File(path, name).absolutePath)
                true
            } catch (e: UnsatisfiedLinkError) {
                System.err.println("Failed to load native library:$name. osinfo: ${OSInfo.nativeSuffix}")
                System.err.println(e)
                false
            }
        } else {
            false
        }
    }

    /**
     * Loads sodium native library using given path and name of the library.
     */
    private fun loadSodiumNativeLibrary() {
        if (extracted) {
            return
        }

        val libraryPath = System.getProperty("org.cybele.sodium.lib.path")
        val libraryName = System.getProperty("org.cybele.sodium.lib.name") ?: System.mapLibraryName("sodium-jni")
        if (libraryPath != null) {
            if (loadNativeLibrary(libraryPath, libraryName)) {
                extracted = true
                return
            }
        }

        // Load the os-dependent library from the jar file
        val packagePath = NativeSodiumJvmLoader::class.java.getPackage().name.replace("\\.".toRegex(), "/")
        val embeddedLibraryPath = "/$packagePath/native/${OSInfo.nativeSuffix}"
        val hasNativeLib = NativeSodiumJvmLoader::class.java.getResource("$embeddedLibraryPath/$libraryName") != null
        if (!hasNativeLib) {
            error("No native library found: at $embeddedLibraryPath/$libraryName")
        }

        // Try extracting the library from jar
        if (extractAndLoadLibraryFile(embeddedLibraryPath, libraryName, tempDir.absolutePath)) {
            extracted = true
            return
        }
        extracted = false
        return
    }

}