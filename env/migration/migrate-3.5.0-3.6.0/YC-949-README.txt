REF: YC-949 Performance of DB connection pool

OVERVIEW:
=========

From HikariCP project page:
"There is nothing faster. There is nothing more correct. HikariCP is a “zero-overhead” production-quality connection pool."

CHANGES:
========

Default DB pool is HikariCP, most configurations are copy over from C3P0 with the exception of few additional ones
recommended by HikariCP for MySQL:

dataSource.cachePrepStmts="true"
dataSource.prepStmtCacheSize="250"
dataSource.prepStmtCacheSqlLimit="2048"
dataSource.useServerPrepStmts="true"

Ref: https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration

Additionally added the validationQuery="SELECT 1" that can solve problems with timeout.

PROD:
=====

This should be a seamless transition. If however problems encountered the following steps can be taken to change
back to C3P0:

- uncomment c3p0 dependency in pom.xml (search for "<groupId>com.mchange</groupId>")
- comment HikariCP dependency in pom.xml (search for "<groupId>com.zaxxer</groupId>")
- uncomment c3p0 version of configuration in config-db*.properties (see blocks with "# C3P0")
- comment HikariCP version of configuration in config-db*.properties (see blocks with "# HikariCP")
