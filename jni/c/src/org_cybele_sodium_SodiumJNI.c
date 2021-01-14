#include <string.h>
#include <stdlib.h>

#include "include/sodium.h"
#include "org_cybele_sodium_SodiumJNI.h"

/*
 * Class:     org_cybele_sodium_SodiumJNI
 * Method:    sodium_init
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_cybele_sodium_SodiumJNI_sodium_1init
  (JNIEnv *penv, jclass clazz)
{
    return (jint) sodium_init();
}

/*
 * Class:     org_cybele_sodium_SodiumJNI
 * Method:    randombytes_random
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_org_cybele_sodium_SodiumJNI_randombytes_1random
  (JNIEnv *penv, jclass clazz)
{
    return (jint) randombytes_random();
}
