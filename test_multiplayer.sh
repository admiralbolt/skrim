#!/bin/bash
./gradlew build && ./copy_to_client.sh && ./gradlew runServer > mod.log 2>&1
