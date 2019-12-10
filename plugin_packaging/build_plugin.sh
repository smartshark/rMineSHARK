#!/bin/bash

current=`pwd`
mkdir -p /tmp/rMineSHARK/
cp -R ../gradle /tmp/rMineSHARK
cp -R ../src /tmp/rMineSHARK
cp -R ../libs /tmp/rMineSHARK
cp ../build.gradle /tmp/rMineSHARK 
cp ../settings.gradle /tmp/rMineSHARK
cp ../gradlew /tmp/rMineSHARK
cp ../gradlew.bat /tmp/rMineSHARK
cp ../gradle.properties /tmp/rMineSHARK
cp * /tmp/rMineSHARK
cd /tmp/rMineSHARK/

tar -cvf "$current/rMineSHARK_plugin.tar" --exclude=*.tar --exclude=build_plugin.sh *