#!/bin/sh
echo "Updating web module"
git submodule update --init --recursive

echo "Initializing"
cd web-module
sudo $(which npm) cache clean -f
sudo $(which npm) install
sudo $(which npm) gulp

echo "Compiling"
gulp

echo "Distributing"
rm -r ../src/main/webapp/dist
mv dist ../src/main/webapp
