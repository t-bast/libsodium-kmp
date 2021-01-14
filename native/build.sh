#!/usr/bin/env bash

set -e

[[ -z "$TARGET" ]] && echo "Please set the TARGET variable" && exit 1

if [ "$(id -u)" == "0" ]; then
  [[ -z "$TO_UID" ]] && echo "Please set the TO_UID variable" && exit 1
fi

cd "$(dirname "$0")"

cd libsodium

if [ "$TARGET" == "mingw" ]; then
  CONF_OPTS="CFLAGS=-fPIC --host=x86_64-w64-mingw32"
elif [ "$TARGET" == "linux" ]; then
  CONF_OPTS="CFLAGS=-fPIC"
elif [ "$TARGET" == "darwin" ]; then
  CONF_OPTS="--host=x86_64-w64-darwin"
else
  echo "Unknown TARGET=$TARGET"
  exit 1
fi

./autogen.sh
# See https://github.com/jedisct1/libsodium/issues/292: disable-pie is necessary on some gcc versions on linux to ensure shared objects can link properly.
./configure $CONF_OPTS --enable-shared=no --disable-pie
make clean
make

[[ ! -z "$TO_UID" ]] && chown -R $TO_UID:$TO_UID .

cd ..

mkdir -p build/$TARGET
cp -v libsodium/src/libsodium/.libs/libsodium.a build/$TARGET/

[[ ! -z "$TO_UID" ]] && chown -R $TO_UID:$TO_UID build

echo "Build done for $TARGET"
