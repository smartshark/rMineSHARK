#!/bin/bash

current=`pwd`
mkdir -p /tmp/rMineSHARK/
cp -R ../src /tmp/rMineSHARK
cp * /tmp/rMineSHARK
cd /tmp/rMineSHARK/

tar -cvf "$current/rMineSHARK_plugin.tar" --exclude=*.tar --exclude=build_plugin.sh *
