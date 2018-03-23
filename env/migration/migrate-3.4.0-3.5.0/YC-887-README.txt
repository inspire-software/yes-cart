REF: YC-887 Improve modules/plugin registration mechanism

OVERVIEW
========

The goal of this task is to minimise the coupling of optional modules to a minimum, thus allowing easy configuration
of customised builds.

One of the most common examples is payment modules integrations. Currently the integration points for a payment module
are:
1. configuration file (-on/-off) that specifies payment module specific configurations
2. spring context specific to module, which is included in web.xml as additional files for spring context listener
3. Callback-aware payment gateways require configuration of callback filters, which are included in web.xml
4. Maven profile to add dependency jars, which must be set on all web apps

Points 2 and 3 require core code specific placeholders that coupling the build configuration to core code.
I.e. if new payment module is added then web.xml must be updated in all web applications.

The following techniques can be used to make this more generic:
To improve configuration #2 spring context special import directive can be used such as
"<import resource="classpath*:payment-module.xml"/>". This allows spring to include all "payment-module.xml" files
in all jars on classpath. Thus if payment module exposes its configuration in "payment-module.xml" and it is added
as a dependency (see #4) then it automatically is included in all applications, without having to specify specific
spring context xml imports.

To improve configuration #3 filtering technique can be used to consolidate some variables from configuration files to
create a generic placeholder that is used to add necessary filters in web.xml. Thus "config-web-filters.properties" is
added to env/ configurations that combines all filter variables from specific payment modules into two placeholders:
"payment.filter" and "payment.filter-mapping". Therefore if new module is added only "config-web-filters.properties"
needs to be updated without affecting the web.xml

These are just few techniques that will be used to allow extension points for custom modules.

This seamless integration poses a problem to devops as there is no visibility as to current configuration of the running
system. Thus a new ModuleDirector API is added that allows to register "modules" in spring context. ModuleDirector is
then exposed as cluster tool in JAM to provide visibility of all spring contexts loaded in current runtime.

NOTE: as a side effect of this review it was discovered that because some Spring "sub contexts" where importing other
String "sub context" it had an effect increased loading time as in terms of spring there is no de-duplication of
imports (sign of this was log message "Overriding bean definition"). While doing refactoring tasks the offending imports
where removed for a smooth start up.

APPROACH TO EXTENSION
=====================

Going forward the following extension points will be available as wildcard classpath imports in spring contexts:

manager/jam

- adm-applicationContext-ext.xml in applicationContext.xml
- adm-servlet-ext.xml in jam-servlet.xml
- for @Controller the following packages are scanned "org.yes.cart.service.endpoint,org.yes.cart.service.endpoint.impl"

web/support

- websupport-ext.xml in websupport-webapp.xml

web/api

- api-applicationContext-ext.xml in applicationContext.xml
- api-servlet-ext.xml in rest-servlet.xml
- for @Controller the following packages are scanned "org.yes.cart.web.service.rest"

web/store-wicket

- sfw-applicationContext-ext.xml in applicationContext.xml

Each custom module must bundle all code necessary to run its functions and expose its functions viw spring context
extension points described above (xml + @Controller)

To bundle the modules in custom builds maven profile technique is recommended to be used in pom.xml of specific
applications being extended (or direct dependency if it is a permanent core feature).



PRODUCTION
==========

- new "config-web-filters.properties" added to environment specific configurations

- web.xml of custom applications will need to be updated to include new placeholders (see store-wicket web.xml for
  example)

- custom modules should have Module declaration at the botom of each relevant spring context XML file if you wish
  to display these modules in JAM cluster section

- payment.XXXX.context settings no longer needed
