Ref YC-677 Product types attributes management and configuration

In order to simplify the use of attributes in filtered navigation and use ProductTypes in a more generic way
the configurations flags for search and navigation now belong to Attribute entity (as opposed to ProductTypeAttr).
This allows to share these configurations accross all product types and not depend on knowing what individual product
type is. This allows to have mixed product types in category and still benefit from filtered navigation in categories.

Basic use case for this is using a generic product type to setup common filtered navigation blocks for a category
with mixed products.

This task involved MOVING properties "store", "search", "primary" and "navigation" from ProductTypeAttrEntity
(TPRODUCTTYPEATTR) to AttributeEntity (TATTRIBUTE).

There are some SQL that will allow to migrate these flags from product types to attributes.

As a result in YUM the search and navigation flags for product type attributes will appear disabled as they are
read from linked Attribute and thus managed in System > Attributes > PRODUCT.

Some useful things for devs:

If you are adjusting initdata.sql for TATTRIBUTE entries use the following RegEx search and replace:
(TATTRIBUTE(\s+))\((.*)\)
$1($3, STORE, SEARCH, SEARCHPRIMARY, NAV)

