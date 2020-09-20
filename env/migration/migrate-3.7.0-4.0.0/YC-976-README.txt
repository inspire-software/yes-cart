REF: YC-976 Upgrades 4.x.x

OVERVIEW
========

Notes to describe upgrade release 4.0.0

1. Lucene upgrade from 8.1.1 to 8.6.2
   - Upgrade required upgrade of the ASM to 7.1+, upgraded to 8.0.1 and consequently cglib to 3.3.0
   - Index is not compatible, it has to be manually deleted and full reindex must be done during deployment

2. Wicket upgrade from 8.5.0 to 8.9.0
   - Not upgraded to 9.x as it requires JDK11
   - Upgrade required few changes to cglib, collections4, wss4j transitive dependencies
   - Additionally upgraded CXF from 3.2.4 to 3.4.0

3. Hibernate upgrade from 5.2.17.Final to 5.4.21.Final
   - few mapping changes and API changes, note that schema updates are required
   - producttypeattrnames.xml has to be amended as there is a breaking change in ProductTypeAttrEntity
   - query.setParameter() API has breaking changes
     * ordinal placeholders have separate API
     * null values must have value type supplied otherwise causes an error during PreparedStatement.setNull() in
       queries of format "where (?1 is null or e.orderNumber = ?1)"
   - many-to-one mapping potentially has a bug with second pass scanning. As a positive consequence product type is
     now decoupled from attributes domain
   - changed derby dialect to DerbyTenSevenDialect

4. MySQL connector/j upgrade from 5.1.43 to 5.1.49 and option to use 8.0.21
   - minor upgrade for driver to support older installations of MySQL
   - those who want to use newer versions of MySQL can build using new mysql8 profile that uses 8.x driver and
     config-db-mysql8.properties

5. Spring upgrade 4.3.x to 5.2.x
   - small refactoring caused by incompatibilities migrating from 4.3.23.RELEASE to 5.2.9.RELEASE
     * commons-dbcp completely removed (test are now directly using hsqldb datasource)
     * cart tuplizers now directly used (without pool)
     * new module added core-module-websupport to enable spring-webmvc on top of core
     * core modules refactored to refrain from using web related APIs and core-io.xml is now moved to webapps
       as a consequence as some IO for resource resolution is servlet context dependent
     * JMX bean exports are commented out, they are not used but if someone needs them they can be added as
       required
   - xstream upgraded from 1.4.11.1 to 1.4.13
   - Spring security upgrade from 4.2.13.RELEASE to 5.4.0
     * removed unnecessary HttpSessionEventPublisher in web.xml as it is not used, we are not using sessions
       and wicket manages sessions on its own
     * small refactoring in cluster connector to add NoopPasswordEncoderImpl as it is now mandatory to set
       encoders in authentication manager beans
   - Thymeleaf integration with spring thymeleaf-spring4 replaced to thymeleaf-spring5
   - FOP upgraded from 2.3 to 2.5
   - POI upgraded from 4.1.0 to 4.1.2
   - Groovy upgraded from 2.4.15 to 2.4.20

6. Groovy upgraded from 2.4.20 to 3.0.5
