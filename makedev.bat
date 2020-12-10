#!/bin/sh

#
# YesCart development script
#
# @author inspiresoftware

call mvn clean install -Pdev,derby,ftEmbededLucene,connREST,paymentAll,pricerules -DskipTests=true