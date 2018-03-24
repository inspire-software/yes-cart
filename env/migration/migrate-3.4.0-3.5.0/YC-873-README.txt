REF: YC-873 Allow pluggable SearchEngines

This task was effort to completely isolate concreme full text search engine implementation from the YC search APIs.
The goal was to create a "pluggable" mechanism whereby extension points are given in the form of Spring context imports.
Thus if specific import files are on path they are loaded into context making implementation provider available in
runtime. The bundling of provider is configured via maven profiles thus ensuring that jar that exposes search provider
implementation in put to ./libs

The following extension points are defined:

websupport-cronjob-ext.xml
imported by:
- support/websupport-cronjob.xml
exposes trigger beans via trigger extension list:
- reindexDiscontinuedProductsTrigger
- productsGlobalIndexProcessorTrigger
- productInventoryChangedProcessorJobCronTrigger

core-index-sf.xml
imported by:
- support/websupport-services.xml
- store-wicket/applicationContext.xml
- api/applicationContext.xml
exposes beans:
- priceNavigation
- ftQueryFactory

dao-index-admin.xml
imported by:
- jam/applicationContext.xml
exposes beans:
- productDao
- productSkuDao

dao-index-api.xml
imported by:
- api/applicationContext.xml
exposes beans:
- productDao
- productSkuDao

dao-index-sf.xml
imported by:
- store-wicket/applicationContext.xml
exposes beans:
- productDao
- productSkuDao

additional hook for "statics" (threadlocals) was added in SearchUtils thus ensuring any thread local reusable components
are correctly garbage collected in scope of Tomcat's threads. The hook is:
- SearchUtil.addDestroyable()

Default embeded lucene implementation is achieved by invoking "embededLucene" maven profile, whcih is the same behaviour
as in prior versions of YC. This profile ensures that "search-lucene-embeded" module is added as dependency to the
following modules: "support", "store-wicket" and "api" thus enabling full text search support in storefront applications.

Therefore if new provider needs to be implemented the FT search provider module must contain the spring xml context files
described above including specified exposed beans.


PRODUCTION CHANGES:

- "luceneQueryFactory" bean is now "ftQueryFactory"

- if you have custom frontend apps, please follow the change in "store-wicket"/"api" spring context files and pom.xml
