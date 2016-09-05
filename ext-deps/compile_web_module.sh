#!/bin/sh
echo "Updating web module"
git submodule update --init --recursive

echo "Initializing"
cd web-module
sudo $(which npm) cache clean -f
npm install -g n

echo "Compiling"
gulp

echo "Distributing"
rm -r ../src/main/webapp/dist
mv dist ../src/main/webapp
