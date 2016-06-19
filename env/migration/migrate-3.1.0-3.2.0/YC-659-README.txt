Ref YC-659 Wicket context param for configuration mode

Wicket application an be configured to run in two modes 'deployment' (i.e. prod) or 'development'.

In order to configure this per environment new variable is added to config-cluster.properties
'wicket.configuration' which set corresponding <context-param> in web.xml for store-wicket.

All config-cluster.properties should be updated with correct value.