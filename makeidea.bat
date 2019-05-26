#!/bin/sh

#
# YesCart development script
#
# @author Denys Pavlov

call mvn clean install -PdevIntellijIDEA,derby,ftEmbededLucene,connREST,paymentAll,pricerules -DskipTests=true