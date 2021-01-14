package org.cybele.sodium

/**
 * Sodium is a modern, easy-to-use software library for encryption, decryption, signatures, password hashing and more.
 * It is a portable, cross-compilable, installable, packageable fork of NaCl, with a compatible API, and an extended API to improve usability even further.
 * Its goal is to provide all of the core operations needed to build higher-level cryptographic tools.
 * The design choices emphasize security and ease of use. But despite the emphasis on high security, primitives are faster across-the-board than most implementations.
 *
 * See https://doc.libsodium.org/
 */
public interface Sodium {
    public fun random(): Int

    public companion object {
        public fun init(): Sodium = initSodium()
    }
}

internal expect fun initSodium(): Sodium
