REF: YC-1032 Make credentials mechanism more flexible

OVERVIEW
========

This feature introduces/modifies fields on the RegisteredPerson interface:
- login: this becomes new primary authentication (replaces email)
- email: is no longer mandatory at DB level and account can be created without one
- phone: now moved from attribute value CUSTOMER_PHONE to core property as in some countries phone number is preferred
         method of registration

All above fields are proxied via attributes. Attributes proxies are now not on Attribute.code but on Attribute.val,
which is similar mechanism to ADDRESS attributes.

Additionally new checks have been added to improve login/email changes for managers/customers from Admin app.

Customers can also change their login though a secure form that requires entering password.

Newsletter subscriptions now result in Customer records of customerType=EMAIL created.

During implementation substantial refactoring has been performed, inlcuding:
- comprehensive test harness for SFx
- moving of ${ft.config.api.indexBase} and ${ft.config.sf.indexBase} placehodlers from search-lucene-embeded to
  runtimeConstants bean
- extraction of aspect to  websupport-aspects.xml and providing websupport-aspects-ext.xml to extend aspects
- improved TestThemeRepositoryServiceServletContextImpl to leverage themes modules with email templates together
  with CurrentThreadExecutor to ensure that emails are generated in the same thread during test execution.

BREAKING CHANGES
================

- schema-changes.sql
- ehcache.xml
  - "customerService-customerByEmail" replaced by "customerService-customerByLogin"


SEARCH TERMS
============

customerService-customerByEmail