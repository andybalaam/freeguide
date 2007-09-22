#!/bin/sh

keytool   -genkey -keystore myKeystore -storepass "passss" -keypass "passss" -alias myself
keytool -selfcert -keystore myKeystore -storepass "passss" -keypass "passss" -alias myself 
