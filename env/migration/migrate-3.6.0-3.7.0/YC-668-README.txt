REF: YC-668 Allow configuration of fulfilment centres for Products

OVERVIEW:
=========

In order to provide a flexible configuration whereby marketplace setup is easy and manageable the data model
was re-designed.

Major change is to put more power to fulfilment centres (Warehouse) relationship by moving all "inventory &
fulfilment" configurations from product model to inventory model (SkuWarehouse).

The architecture is following:
- Product - abstract entity that describes a product that has certain features
- ProductSku - specific product entity that can be purchased
- SkuWarehouse - offer from a fulfilment centre that contains details on SKU inventory and fulfilment specifics

This change fundamentally means that the "offer" (SkuWarehouse) is defining sellable product entity for Shop,
which inherits properties from SKU and product definitions.

As a consequence if Shop works with multiple fulfiment centres each SKU can be offered differently. In order to make
this distinction search index is now storing product and SKU documents which are fulfilment centre specific.
The outcome of this is eBay/Amazon style product listing whereby same product may appear multiple times on the
same page because it is offered by multiple suppliers (fulfilment centres).

This task aims to move attributes related to fulfilment of product model to inventory record, namely:

- disabled, availablefrom and availableto - as it is fulfilment centres that dictate when SKU is/will be available
- availability - mode of availability also should be controlled at the fulfilment centre level
- featured - this flag does not belong on product model as product information can be shared by multiple shops.
  Although inventory records also could be shared as they belong to fulfilment centre rather than a specific shop,
  this construct better maps to "shop specific" flag
- minOrderQuantity, maxOrderQuantity and stepOrderQuantity will also be dictated by fulfilment centre that packages it

This refactoring will allow for a more flexible fulfiment process controlled by a specific fulfilment centre, rather
than impose globally by shared product record.

Product model is now taking more focused role of PIM.

This refactoring also opens up additional opportunites as:
- fulfilment options are done at the inventory record and are thus now per SKU
- ability to hide SKU not provided by specific fulfiment centres
- foundation for fulfilment centre specific pricing

CHANGES:
========

The changes will require:
- data migration
  * TPRODUCT:
    ** removed DISABLED, AVAILABLEFROM, AVAILABLETO, AVAILABILITY, FEATURED, MIN_ORDER_QUANTITY, MAX_ORDER_QUANTITY,
       STEP_ORDER_QUANTITY
  * TSKUWAREHOUSE:
    ** added DISABLED, AVAILABLEFROM, AVAILABLETO, AVAILABILITY, FEATURED, MIN_ORDER_QUANTITY, MAX_ORDER_QUANTITY,
       STEP_ORDER_QUANTITY, RELEASEDATE
  * TCUSTOMERWISHLIST:
    ** removed SKU_ID
    ** added SKU_CODE, SUPPLIER_CODE
- note that XML impex functionality will be incompatible between 3.6.x and 3.7.x
- removed ws.cron.reindexDiscontinuedProductsJob
- removed support for CategoryImageRetrieveStrategy, now only image attached to category will render, as a consequence
  APIs for product by category and accompanying caching is removed ("productService-randomProductByCategory",
  "productService-productQtyByCategoryId")
- AVAILABILITY_PREORDER (2) is now removed as a separate availability mode and is now controlled by
  TSKUWAREHOUSE.RELEASEDATE. If this date is in the future the product is considered as preorder, thus providing
  flexibility of using all STANDARD, BACKORDER and ALWAYS in PREORDER mode.
- FULL REINDEX MUST BE DONE AFTER UPGRADE

SEARCH KEYWORDS:
================

- Schema:
  * DISABLED
  * AVAILABLEFROM
  * AVAILABLETO
  * AVAILABILITY
  * FEATURED
  * MIN_ORDER_QUANTITY
  * MAX_ORDER_QUANTITY
  * STEP_ORDER_QUANTITY

- Ehcache:
  * productService-randomProductByCategory
  * productService-productQtyByCategoryId
  * skuWarehouseService-productOnWarehouse


