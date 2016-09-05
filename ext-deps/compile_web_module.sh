#!/bin/sh
echo "Updating web module"
git submodule update --init --recursive

echo "Distributing"
mv web-module/* src/main/webapp/
