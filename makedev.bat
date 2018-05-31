#!/bin/sh

#
# YesCart development script
#
# @author Denys Pavlov

call mvn clean install -Pdev,derby,ftEmbededLucene,paymentAll,pricerules -DskipTests=true