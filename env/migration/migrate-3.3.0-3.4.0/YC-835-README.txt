REF: YC-830 / YC-835 Refactor facets so that we do not use DB for displayable values

This improvements attempt to reduce DB access when working with FT index.

Since facets have only one property for label the display value is now compacted together with value into
taxonomy index. As a result the I18nModel must be a sorted map, so that when compacted it has a predictable
order or locale specific values.

Previously I18n display values were backed by HashMap thus the ordering in DB may be random. This can affect
taxonomy values since facet labels are now: value + displayValue, so if the displayValue is different for objects
with same value we may have duplications in filtered navigation.

New code has been updated to:
- use TreeMap for I18nModel implementation thus guaranteeing the order when compacted
- import descriptors that perform manual insert have been adjusted (productsattributes-demo.xml,
  productskuattributes-demo.xml), column descriptors with column.language property are unaffected

To upgrade the existing system it is required to:
- review all import descriptors that use native insert for displayName/displayValue properties
- run the tool to update you DB entries
  !!! WARNING THIS TOOL WILL CHANGE DATA IN YOUR DB             !!!
  !!! MAKE SURE YOU CREATE FULL DB DUMP BEFORE RUNNING THE TOOL !!!

  E.g. from env/migration/migrate-3.3.0-3.4.0/yc835 directory:
  java -Dfile.encoding=UTF-8 -classpath ".:/path/to/derbyclient-10.8.1.2.jar" org.yes.cart.migrationtools.yc835.AdjustOrderInAVDisplayValue
  java -Dfile.encoding=UTF-8 -classpath ".:/path/to/mysql-connector-java-5.1.43.jar" org.yes.cart.migrationtools.yc835.AdjustOrderInAVDisplayValue
