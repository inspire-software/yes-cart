#!/bin/sh

#
# YesCart development script
#
# @author Denys Pavlov

call mvn clean install -Pdev,derby,ftEmbededLucene,connWS,paymentAll,pricerules -DskipTests=true