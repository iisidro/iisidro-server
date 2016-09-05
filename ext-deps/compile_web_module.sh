#!/bin/sh
echo "Updating web module"
git submodule update --init --recursive

echo "Initializing"
cd web-module
npm cache clean -f
npm install

echo "Compiling"
gulp

echo "Distributing"
rm -r ../src/main/webapp/dist
mv dist ../src/main/webapp
