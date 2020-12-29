#!/bin/sh

#
# YesCart development script
#
# @author inspiresoftware

mvn clean install -PdevIntellijIDEA,derby,ftEmbededLucene,connREST,paymentAll,pricerules -DskipTests=true