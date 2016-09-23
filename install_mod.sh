#!/bin/sh

rm ~/Library/Application\ Support/minecraft/mods/skrim*.jar
rm ~/Desktop/minecraft_server_2/mods/skrim*.jar

cp build/libs/skrim-1.1.0.jar ~/Library/Application\ Support/minecraft/mods/
cp build/libs/skrim-1.1.0.jar ~/Desktop/minecraft_server_2/mods/
