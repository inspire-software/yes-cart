REF: YC-802 Improved data federation

Overview
========

There is a need for a flexible product federation, which will not overwhelm the system.

Product entities are completely autonomous by design and are meant to be shared by all shops,
however there are many use cases whereby access to product data should be restricted or limited.

In order to solve this problem Product.supplierCatalogCode shall be utilised. If supplier catalog
code is set on an entity that would meant that only user with access privileges to this supplier
catalog code should be able to have access to given record. If however the field is left blank that
means there is no such restriction.

ManagerEntity.productSupplierCatalogsInternal shall maintain a CSV of codes that should be accessible
by a manager. These catalog codes are symbolic and are not enforced by referential integrity thus allowing
organic growth of PIM data.

Another improvement to catalog data federation is removal of redundant construct for additional "catalog shops".
Now Manager entity has ability to have have direct master catalog assignments to indicate which user has access
to which catalog branch without resorting to deduce this from assigned shops. This is backwards compatible
improvement since if there are no direct catalog assignments, shop catalog assignments are used instead.

PRODUCTION:
===========

Requires schema changes and update to existing data.

If you have not used Product.supplierCatalogCode field then you should not be affected by this change.
If however you have used it you will need to run and update SQL query to insert CSV of all supplier
catalog codes you have used, so that all manager users retain access to the products information.

Recommended to remove any additional shops that where used previously just to facilitate access to catalog
and assign specific categories directly to manage accounts.

New caches have been added:
shopService-catalogCategoriesIds

Caches removed:
shopFederationStrategy-shop
shopFederationStrategy-shopId
shopFederationStrategy-shopCode

