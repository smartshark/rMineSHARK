#!/bin/sh
PLUGIN_PATH=$1

cd $PLUGIN_PATH

# Build jar file
./gradlew fatJar

cp build/libs/rMineSHARK*.jar ../build/rMineSHARK.jar
