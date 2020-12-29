#!/bin/sh

#
# YesCart development script
#
# @author inspiresoftware

call mvn clean install -PdevIntellijIDEA,derby,ftEmbededLucene,connREST,paymentAll,pricerules -DskipTests=true