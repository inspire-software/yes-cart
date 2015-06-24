#!/bin/sh

YCHOME=/development/projects/java/yc
BASE=$YCHOME/env/sampledata/demo-data/icecat/export/freexml.int/pictcache/
SOURCE=$BASE/UK,RU,DE,EN/*
TARGET=$BASE/UK,RU,DE,EN-sml/

for f in $SOURCE
do
    width=`identify -ping -format %w $f`
    height=`identify -ping -format %h $f`
    if [ $width -ge 536 ]
    then
	echo Big image need to resize $(basename $f) is $width x $height
	convert $f -resize 536x $TARGET/$(basename $f)
    else
	echo Correct size simply copy $(basename $f) is $width x $height
	cp $f $TARGET/$(basename $f)
    fi
done