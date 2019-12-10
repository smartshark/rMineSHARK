#!/bin/sh
PLUGIN_PATH=$1
NEW_UUID=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 32 | head -n 1)

mkdir "dev/shm/$NEW_UUID"

COMMAND="java -jar $PLUGIN_PATH/build/rMineSHARK.jar --project ${2} --db-hostname $3 --db-port $4 --db-database $5"

if [ ! -z ${6+x} ] && [ ${6} != "None" ]; then
	COMMAND="$COMMAND --db-user ${6}"
fi

if [ ! -z ${7+x} ] && [ ${7} != "None" ]; then
	COMMAND="$COMMAND --db-password ${7}"
fi

if [ ! -z ${8+x} ] && [ ${8} != "None" ]; then
	COMMAND="$COMMAND --db-authentication ${8}"
fi

if [ ! -z ${9+x} ] && [ ${9} != "None" ]; then
	COMMAND="$COMMAND -ssl"
fi


$COMMAND

rm -rf "/dev/shm/$NEW_UUID"
