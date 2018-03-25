#!/bin/sh

#
# YesCart development script
#
# @author Denys Pavlov

mvn clean install -PdevIntellijIDEA,derby,ftEmbededLucene,paymentBase -DskipTests=true