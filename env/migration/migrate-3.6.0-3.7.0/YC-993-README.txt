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

