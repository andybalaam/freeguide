#!/bin/sh

for i in ../../dist/webstart/lib/*.jar; do jarsigner -keystore myKeystore -storepass "passss" $i myself; done
