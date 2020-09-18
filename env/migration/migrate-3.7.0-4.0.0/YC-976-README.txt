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
