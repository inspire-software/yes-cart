REF: YC-840 Sortable Brand facet

Separate support for brand filtering has now been removed since it can be
handled via attribute navigation. In order to do this a new PRODUCT attribute
with code "brand" is created of type "Locked" so that it cannot be altered as
it is populated by brand property (and not actual attribute value).

As a result to enable brand navigation user must add "brand" attribute to the
product type. And also can adjust position via rank (or normal alpha).

Change for production environments:
- column on TCATEGORY is dropped
- All categories that used to have brand navigation now need attribute navigation
  with brand attribute assigned to the product type
- adjust import descriptors that load navigationByBrand column

TIP: it is recommended to have separate product types for categories that are just
     used for the filtered nav and do not represent actual product types (same
     is for productType attribute)

