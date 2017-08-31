REF: YC-805 Revise roles and permissions scheme

As a business user who owns a shop I would to be able to give fine grained access to my managers.

I would like to be able to assign single roles only.
I would like to be able to give read only access
I would like to have fine grained roles for order transitions.

In order to facilitate fine grain access new roles have been introduced with *USER suffix (as opposed to *ADMIN),
which indicate READ ONLY access to section of JAM.

The following roles are now part of core code (+ - new role, * - changed role):

ROLE_SMADMIN - no change super user, everything is allowed
ROLE_SMSHOPADMIN - same as before full access to all parts of shop
+ ROLE_SMSHOPUSER - read only access ONLY to shop panels
+ ROLE_SMSUBSHOPUSER - read only access to sub shop panels (YCE) + call center screens
* ROLE_SMWAREHOUSEADMIN - this admin now has access only to warehouse/ fulfilment centres related screens
+ ROLE_SMWAREHOUSEUSER - read only access to warehouse/ fulfilment centres/ inventory
* ROLE_SMCALLCENTER - call centre screen, but now restricted to read only mode
+ ROLE_SMCALLCENTERCUSTOMER - add-on for ROLE_SMCALLCENTER for customer management
+ ROLE_SMCALLCENTERORDERAPPROVE - add-on for ROLE_SMCALLCENTER to be able to pre-approve orders (YCE B2B flows)
+ ROLE_SMCALLCENTERORDERCONFIRM - add-on for ROLE_SMCALLCENTER to be able to confirm offline payments and refunds
+ ROLE_SMCALLCENTERORDERPROCESS - add-on for ROLE_SMCALLCENTER to be able to progress order through flow
* ROLE_SMCONTENTADMIN - restricted to CMS screens only
+ ROLE_SMCONTENTAUSER - read only mode for CMS
* ROLE_SMMARKETINGADMIN - restricted to price/tax/promotion related screens only
+ ROLE_SMMARKETINGUSER - read only mode for prices/taxes/promotions
* ROLE_SMSHIPPINGADMIN - this admin now has access only to shipping related screens (exclusing price, you need marketing admin to set price)
+ ROLE_SMSHIPPINGUSER - read only mode for shipping
+ ROLE_SMCATALOGADMIN - catalog management screens (brands/types/catalog)
+ ROLE_SMCATALOGUSER - read only catalog access
+ ROLE_SMPIMADMIN - product information management
+ ROLE_SMPIMUSER - read only PIM access
ROLE_LICENSEAGREED - marker for license agreement

Now fine grain access to functionality can be achieved by assigning only roles that are needed. Note that some roles
(e.g. call centre related ones) are used as add ons and require main role.

Basic approach is to apply multiple roles to accounts in order to specify exactly what access user has and also remove
ambiguity from the roles' function. Now each role allows access to specific section only.
