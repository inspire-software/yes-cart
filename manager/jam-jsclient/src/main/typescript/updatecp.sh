#!/bin/sh

#
# Script to copy over npm build changes to already running Admin without full re-deploy
#

cd ../../../../jam

mvn validate -Pdev,nodejs,derby,ftEmbededLucene,connREST
