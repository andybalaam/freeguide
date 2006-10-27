#!/bin/sh

keytool -genkey -keystore myKeystore -storepass "passss" -alias myself
keytool -selfcert -alias myself -keystore myKeystore -storepass "passss"
