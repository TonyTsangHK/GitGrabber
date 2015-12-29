#!/bin/sh

if [ -z "$1" ]; then
    echo "Please provide the target folder your want to clone the projects to."
else
    targetFolder=$1

    mkdir -p $targetFolder
    cd $targetFolder

    git clone https://github.com/TonyTsangHK/GitGrabber.git
    git clone https://github.com/TonyTsangHK/Utilities.git
    git clone https://github.com/TonyTsangHK/JSON.git
    git clone https://github.com/TonyTsangHK/ByteUtils.git
    git clone https://github.com/TonyTsangHK/JsonUtils.git
    git clone https://github.com/TonyTsangHK/UIUtils.git

    cd GitGrabber

    echo "Start gradle distZip task"

    gradle distZip

    echo "Done, check your gradle build folder for GitGrabber-(version).zip"
fi