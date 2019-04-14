REF: YC-953 Rework query API in Admin, so that it is pluggable

OVERVIEW:
=========

This task is concerned with refactoring query API, however since all APIs are interconnected some general
improvements/refactoring were made.

CHANGES:
========

As part rework for query API backdoor service has been reworked and now is served under /services/connector

The following files need to be reviewed when building for custom environments:
* config-cluster.properties
* yc-ws-cluster.xml

Additionally SQL updates are necessary to update all system attributes that mention BACKDOOR in CODE.
