REF: YC-978 Remove Etype entity and create type property on attribute
     YC-979 Remove AttributeGroup entity and create group property on attribute

OVERVIEW:
=========

In order to make that domain model remains lean it has been identified that we do not need
referential integrity to group/etype and these relation can be implicit and only exist at UI level
in admin. The benefit is that we reduce strain on persistence layer.

PRODUCTION:
===========

In terms of existing data the transition should be relatively easy through schema changes scripts provided.

There is an impact on the following areas in terms of custom integrations:
- impex descriptors (replace usages of attributeGroup.code with attributeGroup and etype.businesstype with etype)
- custom frontend code should not be impacted uless your code details with groups and etypes (generaly it should not as
  these are internal API domain objects, one exception being dynamic forms in YCE where it should be a simple search
  and replace)
- custom persistence tests that use ATTRIBUTE entity

SEARCH KEYWORDS:
================
attributeGroup.attributegroupId
attributeGroup.code
etype.etypeId
etype.businesstype
