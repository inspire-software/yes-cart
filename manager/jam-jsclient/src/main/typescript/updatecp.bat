#!/bin/sh

#
# Script to run SPA in "local" mode so that there is no dependency on the re-deploy of the Admin app
#

cd ../../../../jam

call mvn validate -Pdev,nodejs,derby,ftEmbededLucene,connREST
