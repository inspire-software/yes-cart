REF: YC-993 Rename REST API endpoints to conform to best practices

SCOPE OF CHANGES:
=================
* Ensure consistent use of GET, POST, PUT and DELETE
* Adhere to describing API as resources (e.g. plural, path variables, sub resources)
* Add annotations for SwaggerUI to have descriptive and developer friendly interface
* Use tags to provide "logical" grouping for more developer friendly UI

KNOWN ISSUES:
=============
* All REST API support JSON and XML and both request content type and response type, however in some cases swagger-ui
  does not render all available content types when it loads
* Auto discovered XML models sometimes do not reflect correct top node (e.g. <RegisterRO> should be <register>)

REST API CHANGES (breaking changes for PROD):
=============================================

PUT /auth/login -> POST /auth/login
PUT /auth/logout -> POST /auth/logout
PUT /auth/register -> POST /auth/register
PUT /auth/guest -> POST /auth/guest
POST /auth/signupnewsletter -> POST /contacts/signupnewsletter
POST /auth/contactus -> POST /contacts/contactus

PUT /cart -> POST /cart
PUT /cart/options/shipping -> POST /cart/options/shipping
GET /rest/cart/options/address/{type} -> GET /rest/cart/options/addresses/{type}
PUT /rest/cart/options/address/{type} -> POST /rest/cart/options/addresses/{type}
PUT /rest/cart/options/payment -> POST /rest/cart/options/payment
GET /rest/cart/validate -> POST /rest/cart/validate
PUT /order/preview -> POST /order/preview

GET /rest/category/menu -> GET /rest/categories/menu
GET /rest/category/view/{id} -> GET /rest/categories/{id}/view
GET /rest/category/menu/{id} -> GET /rest/categories/{id}/menu

GET /rest/content/view/{id} -> GET /rest/content/{id}/view
PUT /rest/content/view/{id} -> POST /rest/content/{id}/view
GET /rest/content/menu/{id} -> GET /rest/content/{id}/menu

PUT /rest/customer/summary -> POST /rest/customer/summary
GET /rest/customer/addressbook/{type}/options/country/{code} -> GET /rest/customer/addressbook/{type}/options/countries/{code}
PUT /rest/customer/addressbook/{type} -> POST /rest/customer/addressbook/{type}
PUT /api/rest/customer/addressbook/{type}

ADMIN CHANGES (should not affect PROD, unless using ADMIN API directly):
========================================================================

GET /attributes/etype/all -> GET /attributes/etypes
GET /attributes/group/all -> GET /attributes/groups
POST /attributes/attribute/filtered -> POST /attributes/search
GET /attributes/attribute/producttype/{code} -> GET /attributes/{code}/producttypes
GET /attributes/attribute/{id} -> GET /attributes/{id}
PUT /attributes/attribute -> POST /attributes
POST /attributes/attribute -> PUT /attributes
DELETE /attributes/attribute/{id} -> DELETE /attributes/{id}

POST /catalog/brand/filtered -> POST /catalog/brands/search
GET /catalog/brand/{id} -> GET /catalog/brands/{id}
PUT /catalog/brand -> POST /catalog/brands
POST /catalog/brand -> PUT /catalog/brands
DELETE /catalog/brand/{id} -> DELETE /catalog/brands/{id}
GET /catalog/brand/attributes/{brandId} -> GET /catalog/brands/{id}/attributes
POST /catalog/brand/attributes -> POST /catalog/brands/attributes

POST /catalog/producttypes/filtered -> POST /catalog/producttypes/search
GET /catalog/producttype/{id} -> GET /catalog/producttypes/{id}
PUT /catalog/producttype -> POST /catalog/producttypes
POST /catalog/producttype -> PUT /catalog/producttypes
DELETE /catalog/producttype/{id} -> DELETE /catalog/producttypes/{id}
GET /catalog/producttype/attributes/{typeId} -> GET /catalog/producttypes/{id}/attributes
POST /catalog/producttype/attributes -> POST /catalog/producttypes/attributes

GET /catalog/productsuppliercatalogs/all -> GET /catalog/productsuppliercatalogs

GET /catalog/category/all -> GET /catalog/categories
GET /catalog/category/branch/{branch} -> GET /catalog/categories/{id}/branches
GET /catalog/category/branchpaths/ -> GET /catalog/categories/branchpaths
POST /catalog/category/filtered -> POST /catalog/categories/search
GET /catalog/category/{id} -> GET /catalog/categories/{id}
PUT /catalog/category -> POST /catalog/categories
POST /catalog/category -> PUT /catalog/categories
DELETE /catalog/category/{id} -> DELETE /catalog/categories/{id}
GET /catalog/category/attributes/{categoryId} -> GET /catalog/categories/{id}/attributes
POST /catalog/category/attributes -> POST /catalog/categories/attributes

GET /content/shop/{shopId} -> GET /shops/{shopId}/content
GET /content/shop/{shopId}/branch/{branch} -> GET /shops/{shopId}/content/{id}/branches
GET /content/shop/{shopId}/branchpaths -> GET /shops/{shopId}/content/branchpaths
POST /content/shop/{shopId}/filtered -> POST /shops/{shopId}/content/search
PUT /content -> POST /content
POST /content -> PUT /content
GET /content/attributes/{contentId} -> GET /content/{id}/attributes
GET /content/body/{contentId} -> GET /content/{id}/body
GET /content/mail/{shopId}/{template} -> GET /shops/{shopId}/mail/{template}/preview

POST /customer/filtered -> POST /customers/search
GET /customer/types/{lang} -> GET /customers/types?lang=
GET /customer/types/{lang}/{customer} -> GET /customers/{id}/types?lang=
GET /customer/{id} -> GET /customers/{id}
PUT /customer -> POST /customers
POST /customer -> PUT /customers
DELETE /customer/{id} -> DELETE /customers/{id}
GET /customer/attributes/{customerId} -> GET /customers/{id}/attributes
POST /customer/attributes -> POST /customers/attributes

POST /customer/reset/{customerId}/{shopId} -> POST /customers/{id}/reset/{shopId}

GET /customer/addressbook/{id}/{formattingShopId}/{lang} -> GET /customers/{id}/addressbook/types?shopId=&lang=
PUT /customer/addressbook -> POST /customers/addressbook
POST /customer/addressbook -> PUT /customers/addressbook
DELETE /customer/addressbook/{id} -> DELETE /customers/addressbook/{id}

GET /reports/dashboard/{lang}/available -> GET /reports/dashboard/available?lang=
POST /reports/dashboard/{lang} -> POST /reports/dashboard?lang=
GET /reports/dashboard/{lang} -> GET /reports/dashboard?lang=
GET /reports/dashboard/{lang}/{widget} -> GET /reports/dashboard/{widget}?lang=

GET /filemanager/list/{mode} -> GET /filemanager/list?mode=
GET /filevault/image/{type}?fileName= GET /filevault/images/{type}?fileName=
GET /filevault/file/{type}?fileName= GET /filevault/files/{type}?fileName=
GET /filevault/sysfile/{type}?fileName= GET /filevault/sysfiles/{type}?fileName=

POST /fulfilment/centre/filtered -> POST /fulfilment/centres/search
GET /fulfilment/centre/shop/{id} -> GET /shops/{id}/centres
GET /fulfilment/centre/{id} -> GET /fulfilment/centres/{id}
PUT /fulfilment/centre -> POST /fulfilment/centres
PUT /fulfilment/centre/shop/{id} -> POST /shops/{id}/centres
POST /fulfilment/centre -> PUT /fulfilment/centres
POST /fulfilment/centre/shop -> PUT /fulfilment/centres/shops
DELETE /fulfilment/centre/{id} -> DELETE /fulfilment/centres/{id}

POST /fulfilment/inventory/centre/filtered -> POST /fulfilment/inventory/search
PUT /fulfilment/inventory -> POST /fulfilment/inventory
POST /fulfilment/inventory -> PUT /fulfilment/inventory

GET /impex/export/groups/{lang} -> GET /impex/export/groups?lang=
GET /impex/import/groups/{lang} -> GET /impex/import/groups?lang=

GET /impex/datagroup/all -> GET /impex/datagroups
GET /impex/datagroup/{id} -> GET /impex/datagroups/{id}
PUT /impex/datagroup -> POST /impex/datagroups
POST /impex/datagroup -> PUT /impex/datagroups
DELETE /impex/datagroup/{id} -> DELETE /impex/datagroups/{id}

GET /impex/datadescriptor/all -> GET /impex/datadescriptors
GET /impex/datadescriptor/{id} -> GET /impex/datadescriptors/{id}
PUT /impex/datadescriptor -> POST /impex/datadescriptors
POST /impex/datadescriptor -> PUT /impex/datadescriptors
DELETE /impex/datadescriptor/{id} -> DELETE /impex/datadescriptors/{id}

GET /location/country/filtered -> GET /locations/countries/search
GET /location/country/{id} -> GET /locations/countries/{id}
PUT /location/country -> POST /location/countries
POST /location/country -> PUT /location/countries
DELETE /location/country/{id} -> DELETE /location/countries/{id}
GET /location/country/state/{id} -> GET /locations/countries/{id}/states

GET /location/country/state/filtered -> GET /locations/states/search
GET /location/state/{id} -> GET /locations/states/{id}
PUT /location/state -> POST /location/states
POST /location/state -> PUT /location/states
DELETE /location/state/{id} -> DELETE /location/states/{id}

POST /organisation/managers/filtered -> POST /organisations/managers/search
GET /organisation/manager/{id} -> GET /organisations/managers/{id}
PUT /organisation/manager -> POST /organisations/managers
POST /organisation/manager -> PUT /organisations/managers
DELETE /organisation/manager/{id} -> DELETE /organisations/managers/{id}
POST /organisation/manager/reset/{id} -> POST /organisations/managers/{id}/reset
POST /organisation/manager/manager/offline/{id}/{state} -> POST /organisations/managers/{id}/account

GET /organisation/role/all -> GET /organisations/roles
PUT /organisation/role -> POST /organisations/roles
POST /organisation/role -> PUT /organisations/roles
DELETE /organisation/role/{code} -> DELETE /organisations/roles/{code}

GET /payment/gateways/all/{lang} -> GET /paymentgateways?lang=&enabledOnly=
GET /payment/gateways/shop/allowed/{lang} -> GET /paymentgateways?lang=&enabledOnly=
GET /payment/gateways/shop/{code}/{lang} -> GET /shops/{code}/paymentgateways?lang=
GET /payment/gateways/configure/all/{lang} -> GET /paymentgateways/details?lang=&includeSecure=
GET /payment/gateways/configure/secure/all/{lang} -> GET /paymentgateways/details?lang=&includeSecure=
GET /payment/gateways/configure/shop/{code}/{lang} -> GET /shops/{code}/paymentgateways/details?lang=&includeSecure=
GET /payment/gateways/configure/secure/shop/{code}/{lang} -> GET /shops/{code}/paymentgateways/details?lang=&includeSecure=
POST /payment/gateways/configure/{label} -> POST /paymentgateways/{label}?includeSecure=
POST /payment/gateways/configure/secure/{label} -> POST /paymentgateways/{label}?includeSecure=
POST /payment/gateways/configure/{label}/{code} -> POST /shops/{code}/paymentgateways/{label}?includeSecure=
POST /payment/gateways/configure/secure/{label}/{code} -> POST /shops/{code}/paymentgateways/{label}?includeSecure=
POST /payment/gateways/offline/{label}/{state} -> POST /paymentgateways/{label}/status
POST /payment/gateways/offline/{code}/{label}/{state} -> POST /shops/{code}/paymentgateways/{label}/status

GET /pim/associations/all -> GET /pim/associations

POST /pim/product/filtered -> POST /pim/products/search
GET /pim/product/{id} -> GET /pim/products/{id}
PUT /pim/product -> POST /pim/products
POST /pim/product -> PUT /pim/products
DELETE /pim/product/{id} -> DELETE /pim/products/{id}
GET /pim/product/attributes/{productId} -> GET /pim/products/{id}/attributes
POST /pim/product/attributes -> POST /pim/products/attributes
GET /pim/product/sku/{productId} -> GET /pim/products/{id}/skus

POST /pim/product/sku/filtered -> POST /pim/skus/search
GET /pim/sku/{id} -> GET /pim/skus/{id}
PUT /pim/sku -> POST /pim/skus
POST /pim/sku -> PUT /pim/skus
DELETE /pim/sku/{id} -> DELETE /pim/skus/{id}
GET /pim/sku/attributes/{productId} -> GET /pim/skus/{id}/attributes
POST /pim/sku/attributes -> POST /pim/skus/attributes

GET /pim/associations/all -> GET /pim/associations
GET /pim/associations/all -> GET /pim/associations

POST /pricing/price/filtered -> POST /pricing/prices/search
GET /pricing/price/{id} -> GET /pricing/prices/{id}
PUT /pricing/price -> POST /pricing/prices
POST /pricing/price -> PUT /pricing/prices
DELETE /pricing/price/{id} -> DELETE /pricing/prices/{id}

POST /pricing/tax/filtered -> POST /pricing/taxes/search
GET /pricing/tax/{id} -> GET /pricing/taxes/{id}
PUT /pricing/tax -> POST /pricing/taxes
POST /pricing/tax -> PUT /pricing/taxes
DELETE /pricing/tax/{id} -> DELETE /pricing/taxes/{id}

POST /pricing/taxconfig/filtered -> POST /pricing/taxconfigs/search
GET /pricing/taxconfig/{id} -> GET /pricing/taxconfigs/{id}
PUT /pricing/taxconfig -> POST /pricing/taxconfigs
POST /pricing/taxconfig -> PUT /pricing/taxconfigs
DELETE /pricing/taxconfig/{id} -> DELETE /pricing/taxconfigs/{id}

POST /pricing/promotion/filtered -> POST /pricing/promotions/search
GET /pricing/promotion/{id} -> GET /pricing/promotions/{id}
PUT /pricing/promotion -> POST /pricing/promotions
POST /pricing/promotion -> PUT /pricing/promotions
DELETE /pricing/promotion/{id} -> DELETE /pricing/promotions/{id}
POST /pricing/promotion/offline/{id}/{state} -> POST /pricing/promotions/{id}/status
POST /pricing/promotion/test/shop/{shopCode}/currency/{currency} -> POST /pricing/promotions/test

POST /pricing/promotioncoupon/filtered -> POST /pricing/promotioncoupons/search
PUT /pricing/promotioncoupon -> POST /pricing/promotioncoupons
DELETE /pricing/promotioncoupon/{id} -> DELETE /pricing/promotioncoupons/{id}

GET /reports/report/all -> GET /reports
POST /reports/report/configure -> POST /reports/configure
POST /reports/report/generate -> POST /reports/generate

POST /shipping/carrier/filtered -> POST /shipping/carriers/search
GET /shipping/carrier/shop/{id} -> GET /shops/{id}/carriers
GET /shipping/carrier/{id} -> GET /shipping/carriers/{id}
PUT /shipping/carrier -> POST /shipping/carriers
PUT /shipping/carrier/shop/{id} -> POST /shops/{id}/carriers
POST /shipping/carrier -> PUT /shipping/carriers
POST /shipping/carrier/shop -> PUT /shops/carriers
DELETE /shipping/carrier/{id} -> DELETE /shipping/carriers/{id}
GET /shipping/carrier/sla/{id} -> POST /shipping/carriers/{id}/slas

POST /shipping/carriersla/filtered -> POST /shipping/carrierslas/search
GET /shipping/carriersla/{id} -> GET /shipping/carrierslas/{id}
PUT /shipping/carriersla -> POST /shipping/carrierslas
POST /shipping/carriersla -> PUT /shipping/carrierslas
DELETE /shipping/carriersla/{id} -> DELETE /shipping/carrierslas/{id}

POST /system/reloadconfigurations -> PUT /system/cluster/configurations
GET /system/query/supported -> GET /system/query
POST /system/cache/on/{name} -> PUT /system/cache/{name}/status
POST /system/cache/off/{name} -> PUT /system/cache/{name}/status
POST /system/cache/warmup -> POST /system/cache
GET /system/index/status/{token} -> GET /system/index/{token}/status
POST /system/index/all -> POST /system/index
POST /system/index/shop/{id} -> POST /system/index/shops/{id}

GET /shop/all -> GET /shops
GET /shop/sub/{id} -> GET /shops/{id}/subs
GET /shop/{id} -> GET /shops/{id}
PUT /shop -> POST /shops
PUT /shop/sub -> POST /shops/subs
POST /shop -> PUT /shops
DELETE /shop/{id} -> DELETE /shops/{id}
GET /shop/summary/{id}/{lang} -> GET /shops/{id}/summary?lang=
GET /shop/localization/{id} -> GET /shops/{id}/seo
POST /shop/localization -> PUT /shops/seo
GET /shop/urls/{shopId} -> GET /shops/{id}/urls
POST /shop/urls -> PUT /shops/urls
GET /shop/aliases/{shopId} -> GET /shops/{id}/aliases
POST /shop/aliases -> PUT /shops/aliases
GET /shop/currencies/{shopId} -> GET /shops/{id}/currencies
POST /shop/currencies -> PUT /shops/currencies
GET /shop/languages/{shopId} -> GET /shops/{id}/languages
POST /shop/languages -> PUT /shops/languages
GET /shop/locations/{shopId} -> GET /shops/{id}/locations
POST /shop/locations -> PUT /shops/locations
GET /shop/categories/{shopId} -> GET /shops/{id}/categories
POST /shop/categories -> PUT /shops/categories
POST /shop/offline/{shopId}/{state} -> POST /shops/{id}/status
GET /shop/attributes/{shopId} -> GET /shops/{id}/attributes?includeSecure=false
GET /shop/attributes/secure/{shopId} -> GET /shops/{id}/attributes?includeSecure=true
POST /shop/attributes -> POST /shops/attributes?includeSecure=false
POST /shop/attributes/secure -> POST /shops/attributes?includeSecure=true



