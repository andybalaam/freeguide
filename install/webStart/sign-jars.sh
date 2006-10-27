#!/bin/sh

for i in ../../build/package/lib/*.jar; do jarsigner -keystore myKeystore -storepass "passss" $i myself; done
