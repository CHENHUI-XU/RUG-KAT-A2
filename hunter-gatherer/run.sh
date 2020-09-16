#!/bin/bash
set -e
mkdir -p bin
javac -Xlint:deprecation -d bin ./src/*.java
java -cp bin Simulation
