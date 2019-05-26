REF: YC-950 Upgrade to java SDK 11

DEVELOPMENT + PRODUCTION
========================

1. JAXB - now removed from jdk and need explicit dependencies
2. javassisst needs upgrade in order work with JDK11 (next version in the JDK11 profile only)
3. JnpInstantiationStrategyImpl is removed as there is no support for CORBA
4. PropertyInfo cannot be accessed, requires "-release 8" switch during compilation (-source 1.8 -target 1.8 on their
   own do not work). This also required upgrading maven plugins
5. WebServices (java.jws) are now removed from JDK11 - strong recommendation to change WS connector channel to be
   REST API instead
6. ehcache upgraded to 2.10.6 see changes in ehcache.xml
7. org.apache.cxf was removed from multiple POM files (include core-common-ws if it is needed)

REFACTORING
===========

As part of #5 connector configuration for cluster communication was fully isolated into modules (previously WS was
included as base functionality).

As a consequence some properties in config-cluster.properties were extracted into individual files.
* config-cluster-WS.properties - captures original implementation using WebServices, now obsolete
* config-cluster-REST.properties - RESTful service implementation (new default)
* config-cluster-JGIPv4.properties + config-cluster-JGIPv6.properties - JGroups multicasting configuration

This refactoring included removal of jaxws (cfx) dependencies from core code (now exists only as a dependency
in core-common-ws).

Renamed properties:
CHANNEL_URI -> CHANNEL
cluster.config.sf.ws.channel_uri -> cluster.config.sf.channel_uri
cluster.config.api.ws.channel_uri -> cluster.config.api.channel_uri

Files to check:
ehcache.xml
config-cluster.properties
context.xml
yc-ws-cluster.xml
web.xml

Maven builds:
new profiles have been added to control dependencies for cluster config:
* connWS - original WS channel that has dependency on WS libraries (not part of JDK11)
* connREST - new (recommended) REST API channel (if you use WebServices you need to include core-common-ws)
* connJGIPv4/connJGIPv6 - Jgroups implementation via multicasting
