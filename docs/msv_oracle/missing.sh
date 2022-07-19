#!/bin/bash
@echo on
# -e causes the shell to exit if any subcommand or pipeline returns a non-zero status
# -v causes the shell to view each command
set -e -v

version="2011.1"

mkdir oracle-${version}
cd oracle-${version}

project="msv-core"
mkdir -p ./${project}
cd ./${project}
# Only create if not existent
mkdir -p ./src/main/java
cd ./src/main/java
# get latest sources from https://repo1.maven.org/maven2/net/java/dev/msv/
curl https://repo1.maven.org/maven2/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
# extract zip/jar - avoiding any log output by '> /dev/null'
jar -xf ${project}-${version}-sources.jar > /dev/null
rm      ${project}-${version}-sources.jar
rm -rf ./META-INF ## signed hashes will be removed, manifest.mf replaced
# Source Code Normalization: Trying to avoid the common Windows/Linux whitespace problems
find . -type f | xargs dos2unix -q
cd ../../../..

project="msv-generator"
mkdir -p ./${project}
cd ./${project}
# Only create if not existent
mkdir -p ./src/main/java
cd ./src/main/java
# get latest sources from https://repo1.maven.org/maven2/net/java/dev/msv/
curl https://repo1.maven.org/maven2/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
# extract zip/jar - avoiding any log output by '> /dev/null'
jar -xf ${project}-${version}-sources.jar > /dev/null
rm      ${project}-${version}-sources.jar
rm -rf ./META-INF ## signed hashes will be removed, manifest.mf replaced
# Source Code Normalization: Trying to avoid the common Windows/Linux whitespace problems
find . -type f | xargs dos2unix -q
cd ../../../..

project="msv-rngconverter"
mkdir -p ./${project}
cd ./${project}
# Only create if not existent
mkdir -p ./src/main/java
cd ./src/main/java
# get latest sources from https://repo1.maven.org/maven2/net/java/dev/msv/
curl https://repo1.maven.org/maven2/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
# extract zip/jar - avoiding any log output by '> /dev/null'
jar -xf ${project}-${version}-sources.jar > /dev/null
rm      ${project}-${version}-sources.jar
rm -rf ./META-INF ## signed hashes will be removed, manifest.mf replaced
# Source Code Normalization: Trying to avoid the common Windows/Linux whitespace problems
find . -type f | xargs dos2unix -q
cd ../../../..

project="msv-testharness"
mkdir -p ./${project}
cd ./${project}
# Only create if not existent
mkdir -p ./src/main/java
cd ./src/main/java
# get latest sources from https://repo1.maven.org/maven2/net/java/dev/msv/
curl https://repo1.maven.org/maven2/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
# extract zip/jar - avoiding any log output by '> /dev/null'
jar -xf ${project}-${version}-sources.jar > /dev/null
rm      ${project}-${version}-sources.jar
rm -rf ./META-INF ## signed hashes will be removed, manifest.mf replaced
# Source Code Normalization: Trying to avoid the common Windows/Linux whitespace problems
find . -type f | xargs dos2unix -q
cd ../../../..
cd ..
