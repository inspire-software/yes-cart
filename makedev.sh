#!/bin/sh

#
# YesCart development script
#
# @author Denys Pavlov

mvn clean install -Pdev,derby,ftEmbededLucene,paymentAll,pricerules -DskipTests=true