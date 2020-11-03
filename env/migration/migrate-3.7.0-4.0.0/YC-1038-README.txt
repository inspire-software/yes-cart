REF: YC-1038 Consolidate use of config.properties file

OVERVIEW
========

Refactoring of use of "classpath:config.properties" in favour of runtimeConstants. All configuration should now be
loaded into this bean as opposed to include the config in all beans that require pre-built configurations.

Runtime constants now has two options of loading the data, either
- a key value map where values can be placeholders replaced by maven
- a list of property file resources

KEYWORDS
========

runtimeConstants
classpath:config.properties