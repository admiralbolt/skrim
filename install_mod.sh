#!/bin/sh

version=1.1.8

rm ~/Library/Application\ Support/minecraft/mods/skrim*.jar
rm ~/Desktop/minecraft_stuffs/minecraft_server_3/mods/skrim*.jar

cp build/libs/skrim-${version}.jar ~/Library/Application\ Support/minecraft/mods/
cp build/libs/skrim-${version}.jar ~/Desktop/minecraft_stuffs/minecraft_server_3/mods/
