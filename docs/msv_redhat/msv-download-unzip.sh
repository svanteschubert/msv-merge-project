#!/bin/bash
@echo on
# -e causes the shell to exit if any subcommand or pipeline returns a non-zero status
# -v causes the shell to view each command
set -e -v

# https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/
# msv-core/
# msv-generator/
# msv-rngconverter/
# msv-testharness/
# https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/msv-testharness/2011.1-redhat-2/msv-testharness-2011.1-redhat-2-sources.jar


# https://maven.repository.redhat.com/ga/net/java/dev/msv/msv-testharness/2011.1-redhat-2/

# msv-core/
# msv-testharness/
# https://maven.repository.redhat.com/ga/net/java/dev/msv/msv-testharness/2011.1-redhat-2/msv-testharness-2011.1-redhat-2-sources.jar

# remove all files so updates that drop files will be recognized
rm -rf *
version="2011.1-redhat-1"
access="earlyaccess/all"

mkdir 2011.1-redhat-1-ea
cd 2011.1-redhat-1-ea

project="msv-core"
mkdir -p ./${project}
cd ./${project}
# Only create if not existent
mkdir -p ./src/main/java
cd ./src/main/java
# get latest sources from https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/
curl https://maven.repository.redhat.com/${access}/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
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
# get latest sources from https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/
curl https://maven.repository.redhat.com/${access}/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
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
# get latest sources from https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/
curl https://maven.repository.redhat.com/${access}/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
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
# get latest sources from https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/
curl https://maven.repository.redhat.com/${access}/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
# extract zip/jar - avoiding any log output by '> /dev/null'
jar -xf ${project}-${version}-sources.jar > /dev/null
rm      ${project}-${version}-sources.jar
rm -rf ./META-INF ## signed hashes will be removed, manifest.mf replaced
# Source Code Normalization: Trying to avoid the common Windows/Linux whitespace problems
find . -type f | xargs dos2unix -q
cd ../../../..

cd ..
version="2011.1-redhat-2"
access="earlyaccess/all"
mkdir 2011.1-redhat-2-ea
cd 2011.1-redhat-2-ea
project="msv-core"
mkdir -p ./${project}
cd ./${project}
# Only create if not existent
mkdir -p ./src/main/java
cd ./src/main/java
# get latest sources from https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/
curl https://maven.repository.redhat.com/${access}/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
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
# get latest sources from https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/
curl https://maven.repository.redhat.com/${access}/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
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
# get latest sources from https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/
curl https://maven.repository.redhat.com/${access}/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
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
# get latest sources from https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/
curl https://maven.repository.redhat.com/${access}/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
# extract zip/jar - avoiding any log output by '> /dev/null'
jar -xf ${project}-${version}-sources.jar > /dev/null
rm      ${project}-${version}-sources.jar
rm -rf ./META-INF ## signed hashes will be removed, manifest.mf replaced
# Source Code Normalization: Trying to avoid the common Windows/Linux whitespace problems
find . -type f | xargs dos2unix -q
cd ../../../..
cd ..


version="2011.1-redhat-2"
access="ga"


mkdir 2011.1-redhat-2-ga
cd 2011.1-redhat-2-ga

project="msv-core"
mkdir -p ./${project}
cd ./${project}
# Only create if not existent
mkdir -p ./src/main/java
cd ./src/main/java
# get latest sources from https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/
curl https://maven.repository.redhat.com/${access}/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
# curl https://maven.repository.redhat.com/ga/net/java/dev/msv/msv-testharness/2011.1-redhat-2/msv-testharness-2011.1-redhat-2-sources.jar
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
# get latest sources from https://maven.repository.redhat.com/earlyaccess/all/net/java/dev/msv/
curl https://maven.repository.redhat.com/${access}/net/java/dev/msv/${project}/${version}/${project}-${version}-sources.jar  --output ${project}-${version}-sources.jar
# extract zip/jar - avoiding any log output by '> /dev/null'
jar -xf ${project}-${version}-sources.jar > /dev/null
rm      ${project}-${version}-sources.jar
rm -rf ./META-INF ## signed hashes will be removed, manifest.mf replaced
# Source Code Normalization: Trying to avoid the common Windows/Linux whitespace problems
find . -type f | xargs dos2unix -q
cd ../../../..
cd ..


