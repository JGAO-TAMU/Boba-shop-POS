#!/bin/bash

# Remove old class files
find src -name "*.class" -type f -delete

# Compile
javac -cp "lib/postgresql-42.2.24.jar" -d bin bobaapp/**/*.java

# Run
java -cp "bin:lib/postgresql-42.2.24.jar" com.bobaapp.Main
