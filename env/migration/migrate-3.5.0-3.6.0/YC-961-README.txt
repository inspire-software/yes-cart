REF: YC-961 Change default paths for web apps

OVERVIEW:
=========

Update public paths for web apps.

CHANGES:
========

Default paths for Admin and REST API will be updated to the following:

* API from /yes-api to /api
* JAM from /yes-manager to /cp (as in "Control Panel")

Additional refactoring:

* webapp.yes.context.path placeholder is renamed to webapp.sf.context.path
* webapp.yes.war.name placeholder is renamed to webapp.sf.war.name
* cluster.config.yes.node_id placeholder is renamed to cluster.config.sf.node_id
* cluster.config.yes.node_config placeholder is renamed to cluster.config.sf.node_config
* cluster.config.yes.lucene_index_disabled placeholder is renamed to cluster.config.sf.lucene_index_disabled
* cluster.config.yes.ws.channel_uri placeholder is renamed to cluster.config.sf.ws.channel_uri

Config changes:

Directories for webapp specific files changed:
* YES -> SF
* JAM -> ADM

DEV:
====

Impact is terms of development is minimal.
For command line builds - no changes, simply access the webapps, using new paths.
For IDE setup, ensure that context paths are changed accordingly

Custom code upgrade keywords:
/yes-api -> /api,
/yes-manager -> /cp,
webapp.yes.context.path -> webapp.sf.context.path,
webapp.yes.war.name -> webapp.sf.war.name
cluster.config.yes.node_id -> cluster.config.sf.node_id
cluster.config.yes.node_config  -> cluster.config.sf.node_config
cluster.config.yes.lucene_index_disabled -> cluster.config.sf.lucene_index_disabled
cluster.config.yes.ws.channel_uri -> cluster.config.sf.ws.channel_uri
YES0 -> SF0
YES1 -> SF1
YES2 -> SF2
Config dir YES -> SF
Config dir JAM -> ADM

PRODUCTION:
===========

For production for clusters using REST API service connector URL needs to be adjusted.
If any redirects are mapped in Apache virtual hosts, those will need to be updated (most likely for Admin).

All changes are contained within config-cluster.properties, namely:
* cluster.config.api.ws.channel_uri=http://localhost:8080/api/services/connector
* webapp.api.context.path=/api
* webapp.admin.context.path=/cp

Build configurations are set in main pom.xml:
* webapp.api.war.name
* webapp.admin.war.name

