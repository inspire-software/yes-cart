#!/bin/sh

#
# YesCart development script
#
# @author inspiresoftware

mvn clean install -Pdev,derby,ftEmbededLucene,connREST,paymentAll,pricerules -DskipTests=true