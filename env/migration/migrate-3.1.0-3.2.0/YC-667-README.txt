Ref YC-667 Add standard property to Customer object to define customer type

In order make registration process more flexible we introduced new property to CustomerEntity customerType.
This requires some schema and cofiguration changes in order to upgrade the existing customer registration API.

Please consult the schema-changes.sql file for examples.

In essence the change is to allow each shop owner to define customer types which currently affect how registration form
and address forms will look.

In order to setup allowed customer types the shop owner needs to configure SHOP_CUSTOMER_TYPES attribute that
defines a comma separated list of users. e.g. B2C,B2B,B2E etc. Please keep this type short. In I18n (display
values) shop owner can define localised values (again separated by comma) for each type.

Once types are defined the following attributes can be defined to configure per type registration and profile forms:
1. SHOP_CREGATTRS_[TYPE] (e.g. SHOP_CREGATTRS_B2C) replaces attribute SHOP_CUSTOMER_REGISTRATION_ATTRIBUTES
2. SHOP_CPROFATTRS_VISIBLE_[TYPE] (e.g. SHOP_CPROFATTRS_VISIBLE_B2C) replaces attribute SHOP_CUSTOMER_PROFILE_ATTRIBUTES_VISIBLE
3. SHOP_CPROFATTRS_READONLY_[TYPE] (e.g. SHOP_CPROFATTRS_READONLY_B2C) replaces attribute SHOP_CUSTOMER_PROFILE_ATTRIBUTES_READONLY

Note that customer type attribute will also work as prefix for Address form definitions (see YC-653)