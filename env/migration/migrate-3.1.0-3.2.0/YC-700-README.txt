Ref YC-700 Improve build configurations

Some configurations that were previously set in Spring context xml files are now contained
in the per environment yc-config.properties

Namely:
1. MD5HashHelperImpl.salt configuration
2. LanguageServiceImpl mappings

Additionally new test method has been added to aid server installations by allowing generation of
password hashes. See MD5HashHelperImplTest.testCreatePasswordForProdServerInitialisation() for
instructions on how to use it.

Logback configurations (logback.xml, logging.properties) have been extracted into environment specific
configurations as well. The files should be put into YES, API and YUM directories corresponding to the
type of application that will use them. Note that filtering is still applied to those files.

Cluster configurations (yc-jgroups-udp.xml for jgroups, yc-ws-cluster.xml for WS) have also been moved
to environment specific directories. The files will be filtered for convenience, although it is anticipated
that these will be manually crafted depending on the cluster setup.

