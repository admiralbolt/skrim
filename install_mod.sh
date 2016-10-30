#!/bin/sh

version=1.1.4

rm ~/Library/Application\ Support/minecraft/mods/skrim*.jar
rm ~/Desktop/minecraft_server_2/mods/skrim*.jar

cp build/libs/skrim-${version}.jar ~/Library/Application\ Support/minecraft/mods/
cp build/libs/skrim-${version}.jar ~/Desktop/minecraft_server_2/mods/
