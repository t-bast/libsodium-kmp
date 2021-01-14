#!/usr/bin/env bash

set -e

[[ -z "$TARGET" ]] && echo "Please set the TARGET variable" && exit 1

if [ "$(id -u)" == "0" ]; then
  [[ -z "$TO_UID" ]] && echo "Please set the TO_UID variable" && exit 1
fi

cd "$(dirname "$0")"

CC=gcc
JNI_HEADERS=$TARGET

if [ "$TARGET" == "linux" ]; then
  OUTFILE=libsodium-jni.so
  ADD_LIB=-lgmp
  CC_OPTS="-fPIC"
elif [ "$TARGET" == "darwin" ]; then
  OUTFILE=libsodium-jni.dylib
  ADD_LIB=-lgmp
elif [ "$TARGET" == "mingw" ]; then
  OUTFILE=sodium-jni.dll
  CC=x86_64-w64-mingw32-gcc
fi

mkdir -p build/jni/$TARGET

$CC -shared $CC_OPTS -o build/$TARGET/$OUTFILE ../c/src/org_cybele_sodium_SodiumJNI.c -I../c/headers/ -I../c/headers/java -I../c/headers/$JNI_HEADERS/ -I../../native/libsodium/src/libsodium -lsodium -L../../native/build/$TARGET/ $ADD_LIB

[[ ! -z "$TO_UID" ]] && chown -R $TO_UID:$TO_UID .

echo "Build done for $TARGET"
