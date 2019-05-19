REF: YC-950 Upgrade to java SDK 11

DEVELOPMENT + PRODUCTION
========================

1. JAXB - now removed from idk and need explicit dependencies
2. javassisst needs upgrade in order work with JDK11
3. JnpInstantiationStrategyImpl is removed as there is no support for CORBA
4. PropertyInfo cannot be accessed, requires "-release 8" switch during compilation (-source 1.8 -target 1.8 on their
   own do not work). This also required upgrading maven plugins
TODO: 5. WebServices (java.jws) are now removed from JDK11 - strong recommendation to change WS connector channel to be
TODO:    REST API instead
