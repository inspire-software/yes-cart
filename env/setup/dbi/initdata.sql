-- Igor Azarny iazarny@yahoo.com.
-- Initial data.
--

-- SET character_set_client=utf8;
-- SET character_set_connection=utf8;

INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, GUID, NAME, DESCRIPTION)  VALUES (1, 'accessories', 'accessories' , 'Accessories' , 'Product accessories');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, GUID, NAME, DESCRIPTION)  VALUES (2, 'up',          'up' , 'Up sell' , 'Up sell');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, GUID, NAME, DESCRIPTION)  VALUES (3, 'cross',       'cross' , 'Cross sell' , 'Cross sell');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, GUID, NAME, DESCRIPTION)  VALUES (4, 'buywiththis', 'buywiththis' , 'Buy with this products' , 'Shoppers also buy with this product');
INSERT INTO TASSOCIATION(ASSOCIATION_ID, CODE, GUID, NAME, DESCRIPTION)  VALUES (5, 'expendable',  'expendable' , 'Expendable materials' , 'Expendable materials. Example inc for printer');


INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1000, 'java.lang.String', 'String', 'String');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1001, 'java.lang.String', 'URI', 'URI');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1002, 'java.lang.String', 'URL', 'URL');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1003, 'java.lang.String', 'Image', 'Image');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1004, 'java.lang.String', 'CommaSeparatedList', 'CommaSeparatedList');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1005, 'java.lang.Float', 'Float', 'Float');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1006, 'java.lang.Integer', 'Integer', 'Integer');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1007, 'java.lang.String', 'Phone', 'Phone');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1008, 'java.lang.Boolean', 'Boolean', 'Boolean');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1009, 'java.util.Date', 'Date', 'Date');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1010, 'java.lang.String', 'Email', 'Email');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1011, 'java.lang.String', 'HTML', 'HTML');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1012, 'java.util.String', 'Properties', 'Properties');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1013, 'java.lang.String', 'File', 'File');
INSERT INTO TETYPE (ETYPE_ID, JAVATYPE, BUSINESSTYPE, GUID) VALUES (1014, 'java.lang.String', 'SystemFile', 'SystemFile');

INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, GUID, CODE, NAME, DESCRIPTION) VALUES (1000, 'SYSTEM',   'SYSTEM', 'System settings.', 'System wide settings');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, GUID, CODE, NAME, DESCRIPTION) VALUES (1001, 'SHOP',     'SHOP', 'Shop settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, GUID, CODE, NAME, DESCRIPTION) VALUES (1002, 'CATEGORY', 'CATEGORY', 'Category settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, GUID, CODE, NAME, DESCRIPTION) VALUES (1003, 'PRODUCT',  'PRODUCT', 'Product settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, GUID, CODE, NAME, DESCRIPTION) VALUES (1005, 'BRAND',    'BRAND', 'Brand settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, GUID, CODE, NAME, DESCRIPTION) VALUES (1006, 'CUSTOMER', 'CUSTOMER', 'Customer settings.', '');
INSERT INTO TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID, GUID, CODE, NAME, DESCRIPTION) VALUES (1007, 'ADDRESS', 'ADDRESS', 'Customer address settings.', '');

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10999,  'CURRENCY', 'CURRENCY',  0,  NULL,  'Currencies',  'Supported currencies by shop. First one is the default',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10995,  'SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL', 'SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL',  0,  NULL,  'Active payment modules',  'Active payment modules',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10966,  'SHOP_PG_ALLOWED_IPS_REGEX', 'SHOP_PAYMENT_GATEWAYS_ALLOWED_IPS_REGEX',  0,  NULL,  'Payment Gateway: Allowed IPs regular expression',
'Regular expression to determine if PG callback is allowed from IP.
Blank means that all IPs are allowed.
If not blank allowed IP should match "regex.matcher(ip).matches()"
E.g. "^((192.168.0.)([0-9]){1,3})$" will match all IPs starting with "192.168.0."
WARNING: be careful with IPv4 vs IPv6',  1000, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10997,  'COUNTRY_SHIP', 'COUNTRY_SHIP',  0,  NULL,  'Countries (Shipping)',  'Supported shipping countries by shop.',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10996,  'COUNTRY_BILL', 'COUNTRY_BILL',  0,  NULL,  'Countries (Billing)',  'Supported billing countries by shop.',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10994,  'SUPPORTED_LANGUAGES', 'SUPPORTED_LANGUAGES',  0,  NULL,  'Languages',  'Supported shop languages',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10993,  'CART_ADD_ENABLE_QTY_PICKER', 'CART_ADD_ENABLE_QTY_PICKER',  0,  NULL,  'Checkout: enable quantity picker for products',  'Enables quantity picker component on product pages',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10992,  'INCLUDE_SUBCATEGORIES_IN_SEARCH_SHOP', 'INCLUDE_SUBCATEGORIES_IN_SEARCH_SHOP',  0,  NULL,  'Search: include sub categories',
    'Allow search to be performed including sub categories. If set to false product are searched only in the immediate category',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10990,  'SHOP_NEW_ARRIVAL_DAYS_OFFSET', 'SHOP_NEW_ARRIVAL_DAYS_OFFSET',  0,  NULL,  'Search: newarrival tag days offset',
   'Dynamic newarrival tag setting. New products are products with date befor "now() - days offset"',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10988,  'SHOP_CHECKOUT_ENABLE_ORDER_MSG', 'SHOP_CHECKOUT_ENABLE_ORDER_MSG',  0,  NULL,  'Checkout: enable order message',
   'Enable order message capture. This can be used for many different things. e.g. allow gift messaging, delivery instructions',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10987,  'SHOP_CHECKOUT_ENABLE_COUPONS', 'SHOP_CHECKOUT_ENABLE_COUPONS',  0,  NULL,  'Checkout: enable coupons',
   'Enable "Add coupon" section on the shopping cart page',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10986,  'SHOP_ADDRESS_FORMATTER', 'SHOP_ADDRESS_FORMATTER',  0,  NULL,  'Address: address format (default)',
'Placeholders:
{{salutation}} {{firstname}} {{middlename}} {{lastname}}
{{addrline1}} {{addrline2}} {{postcode}} {{city}} {{countrycode}} {{statecode}}
{{phone1}} {{phone2}} {{mobile1}} {{mobile2}} {{email1}} {{email2}}
{{custom0}} {{custom1}} {{custom2}} {{custom3}} {{custom4}}
{{custom5}} {{custom6}} {{custom7}} {{custom8}} {{custom9}}
For country/type/language specific formatting add attributes with suffixes _[code], _[type] or _[lang]',  1011, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10985,  'SHOP_CUSTOMER_FORMATTER', 'SHOP_CUSTOMER_FORMATTER',  0,  NULL,  'Customer: name format',
   'Placeholders: {{salutation}} {{firstname}} {{middlename}} {{lastname}}',  1011, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10984,  'PRODUCT_DISPLAY_MAN_CODE_SHOP', 'PRODUCT_DISPLAY_MANUFACTURER_CODE_SHOP',  0,  NULL,  'Product: show manufacturer code',
    'Flag whether to use manufacturer code or seller code as primary UI property',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10880,  'SHOP_CREGATTRS_B2C', 'SHOP_CREGATTRS_B2C',  0,  NULL,  'Customer (B2C): registration form attributes (CSV)',
    'List of customer attributes separated by comma to be shown on registration form',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10780,  'SHOP_CREGATTRS_B2G', 'SHOP_CREGATTRS_B2G',  0,  NULL,  'Customer (B2G): registration form attributes (CSV)',
    'List of customer attributes separated by comma to be shown on registration form',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10881,  'SHOP_CPROFATTRS_VISIBLE_B2C', 'SHOP_CPROFATTRS_VISIBLE_B2C',  0,  NULL,  'Customer (B2C): profile form attributes (CSV)',
    'List of customer attributes separated by comma to be shown on profile form',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10882,  'SHOP_CPROFATTRS_READONLY_B2C', 'SHOP_CPROFATTRS_READONLY_B2C',  0,  NULL,  'Customer (B2C): profile form attributes - read only (CSV)',
    'List of customer attributes separated by comma to be shown on profile form but not editable',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10883,  'SHOP_B2B_ADDRESSBOOK', 'SHOP_B2B_ADDRESSBOOK',  0,  NULL,  'Shop: B2B addressbook mode enable',
    'Disables customer addressbook access, all customers can only use B2B shop addressbook',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10884,  'SHOP_B2B_STRICT_PRICE', 'SHOP_B2B_STRICT_PRICE',  0,  NULL,  'Shop: B2B strict price mode enable',
    'Disables main shop look up. Only sub shop prices are considered',  1008, 1001, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10980,  'CART_SESSION_EXPIRY_SECONDS', 'CART_SESSION_EXPIRY_SECONDS',  0,  NULL,  'Customer: session expiry in seconds',
    'Cart session expiry in seconds. Invalidates login when session expires. Default: 21600s (6h)',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10979,  'SHOP_COOKIE_POLICY_ENABLE', 'SHOP_COOKIE_POLICY_ENABLE',  0,  NULL,  'Shop: Cookie policy enable',
    'Enables notification for use of cookie on this site (search the Internet for ''The EU cookie law (e-Privacy Directive)'').',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10978,  'SHOP_CUSTOMER_PASSWORD_RESET_CC', 'SHOP_CUSTOMER_PASSWORD_RESET_CC',  0,  NULL,  'Customer: Password reset token',
    'Authorisation token to reset password immediately by call center operative.',  1000, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10977,  'SHOP_CUSTOMER_TOKEN_EXPIRY_SECONDS', 'SHOP_CUSTOMER_TOKEN_EXPIRY_SECONDS',  0,  NULL,  'Customer: Password reset token validity (s)',
    'Authorisation token validity if reset is requested by customer in second. Default is 86400s (1 day)',  1006, 1001, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10976,  'SHOP_MAIL_SERVER_CUSTOM_ENABLE', 'SHOP_MAIL_SERVER_CUSTOM_ENABLE',  0,  NULL,  'Mail: use custom mail server settings',
    'Enable custom mail server settings for this shop',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10975,  'SHOP_MAIL_SERVER_HOST', 'SHOP_MAIL_SERVER_HOST',  0,  NULL,  'Mail: custom mail server host',
    'Custom mail server host e.g. mail.somedomain.com',  1000, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10974,  'SHOP_MAIL_SERVER_PORT', 'SHOP_MAIL_SERVER_PORT',  0,  NULL,  'Mail: custom mail server port',
    'Custom mail server port e.g. 587',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10973,  'SHOP_MAIL_SERVER_USERNAME', 'SHOP_MAIL_SERVER_USERNAME',  0,  NULL,  'Mail: custom mail server username',
    'Custom mail server username. Required if SMTP-AUTH is enabled',  1000, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10972,  'SHOP_MAIL_SERVER_PASSWORD', 'SHOP_MAIL_SERVER_PASSWORD',  0,  NULL,  'Mail: custom mail server password',
    'Custom mail server password. Required if SMTP-AUTH is enabled',  1000, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10971,  'SHOP_MAIL_SERVER_SMTPAUTH_ENABLE', 'SHOP_MAIL_SERVER_SMTPAUTH_ENABLE',  0,  NULL,  'Mail: use custom mail server SMTP-AUTH enable',
    'Enable SMTP authentication on custom mail server (Require username and password)',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10970,  'SHOP_MAIL_SERVER_STARTTLS_ENABLE', 'SHOP_MAIL_SERVER_STARTTLS_ENABLE',  0,  NULL,  'Mail: use custom mail server TLS encryption enable',
    'Enable TLS encryption on custom mail server (Must be supported)',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10969,  'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO',  0,  NULL,  'Tax: Enable price tax information for customer types (CSV)',
  'Enables price tax information on search results and product pages',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10968,  'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_N', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET',  0,  NULL,  'Tax: Show net prices for customer types (CSV)',
  'Additional configuration to configure showing net prices (without tax) if tax information is enabled, otherwise gross prices are shown',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10967,  'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_A', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT',  0,  NULL,  'Tax: Show tax amount for customer types (CSV)',
  'Additional configuration to configure showing tax amount if tax information is enabled, otherwise percentage is shown',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10964,  'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_C', 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE',  0,  NULL,  'Customer: Allow changing price tax information view for customer types',
  'Allow changing price tax information view for customer types. Blank value is treated as no customer can change tax information view. To reference guests use B2G, blank types are treated as B2C.',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10963,  'SHOP_DELIVERY_ONE_ADDRESS_DISABLE', 'SHOP_DELIVERY_ONE_ADDRESS_DISABLE',  0,  NULL,  'Customer: Disable same address for billing',
  'Disable use of same address feature for customer types. Blank value is treated as enabled. To reference guests use B2G, blank types are treated as B2C.',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10962,  'SHOP_PRODUCT_HIDE_PRICES', 'SHOP_PRODUCT_HIDE_PRICES',  0,  NULL,  'Customer: hide prices',
  'Hide prices for customer types. Blank value is treated as show prices. To reference guests use B2G, blank types are treated as B2C.
This setting must be used together with Block checkout feature to prevent going through checkout for customer types that cannot see prices',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11040,  'SHOP_B2B', 'SHOP_B2B',  1,  NULL,  'Shop: B2B profile enable',  'B2B profile flag for this shop',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11041, 'SHOP_ADMIN_EMAIL',  'SHOP_ADMIN_EMAIL',  1,  NULL,  'Shop: primary shop admin email',  'Email used for all CC messages (e.g. order updates)',  1010, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8001,  'SHOP_CUSTOMER_TYPES', 'SHOP_CUSTOMER_TYPES',  0,  NULL,  'Customer: supported customer types',  'Supported shop customer types CSV
  E.g. value=B2B,B2C, display value=Private,Company',  1000, 1001, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8002,  'SHOP_CHECKOUT_ENABLE_GUEST', 'SHOP_CHECKOUT_ENABLE_GUEST',  0,  NULL,  'Checkout: enable guest checkout',
  'Enables guest checkout customerType=B2G',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5000,  'SHOP_SEARCH_ENABLE_COMPOUND', 'SHOP_SEARCH_ENABLE_COMPOUND',  0,  NULL,  'Search: compound search enable',
  'Enable compound search. If set to true preserved previously searched phrases until they are explicitly removed',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8003,  'SHOP_CATEGORY_FILTERNAV_LIMIT', 'SHOP_CATEGORY_FILTERNAV_LIMIT',  0,  NULL,  'Shop: filter navigation records limit',
  'Filter navigation records limit per group. Default is 25',  1006, 1001, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8004,  'SHOP_SF_REQUIRE_LOGIN', 'SHOP_SF_REQUIRE_LOGIN',  1,  NULL,  'Customer: login required',  'Anonymous browsing for this shop is prohibited',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8005,  'SHOP_SF_REQUIRE_REG_AT', 'SHOP_SF_REQUIRE_REG_APPROVE_TYPES',  0,  NULL,  'Customer: registration types that require approval email (CSV)',  'CSV of customer types that must be approved by shop admin
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8006,  'SHOP_SF_REQUIRE_REG_NT', 'SHOP_SF_REQUIRE_REG_NOTIFY_TYPES',  0,  NULL,  'Customer: registration types that require notification email (CSV)',  'CSV of customer types for which shop admin is notified
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8010,  'SHOP_SF_REPEAT_ORDER_T', 'SHOP_SF_REPEAT_ORDER_TYPES',  0,  NULL,  'Customer: repeat order feature enabled (CSV)',  'CSV of customer types which can repeat order
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8011,  'SHOP_SF_SHOPLIST_T', 'SHOP_SF_SHOPPING_LIST_TYPES',  0,  NULL,  'Customer: shopping list feature enabled (CSV)',  'CSV of customer types which can create shopping lists
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8012,  'SHOP_SF_B2B_LINEREMARK_T', 'SHOP_SF_B2B_LINE_REMARKS_TYPES',  0,  NULL,  'Customer: line remarks feature enabled (CSV)',  'CSV of customer types which can leave remarks per line
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8013,  'SHOP_SF_B2B_ORDFORM_T', 'SHOP_SF_B2B_ORDER_FORM_TYPES',  0,  NULL,  'Customer: B2B form feature enabled (CSV)',  'CSV of customer types which can add B2B information on the order
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5010,  'SHOP_SEARCH_ENABLE_SUGGEST', 'SHOP_SEARCH_ENABLE_SUGGEST',  0,  NULL,  'Search: search suggest enable',
  'Enable search suggest. If set to true will perform "as you type" product look up under search box',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5011,  'SHOP_SEARCH_SUGGEST_MIN_CHARS', 'SHOP_SEARCH_SUGGEST_MIN_CHARS',  0,  NULL,  'Search: search suggest min chars',
  'Minimum number of characters in search box that trigger search suggest (default is 3)',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5012,  'SHOP_SEARCH_SUGGEST_MAX_ITEMS', 'SHOP_SEARCH_SUGGEST_MAX_ITEMS',  0,  NULL,  'Search: search suggest max items',
  'Maximum number of suggested products (default is 10)',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5013,  'SHOP_RFQ_CUSTOMER_TYPES', 'SHOP_RFQ_CUSTOMER_TYPES',  0,  NULL,  'Customer: types that eligible to send RFQ',
  'CSV of customer types eligible for request for quote
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5014,  'SHOP_SF_REQORDERAPPROVE_TYPES', 'SHOP_SF_REQUIRE_ORDER_APPROVE_TYPES',  0,  NULL,  'Customer: types that require order approval',
  'CSV of customer types that require order approval (e.g. B2B setup with supervisor approval)
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5015,  'SHOP_SF_CANNOTCHECKOUT_TYPES', 'SHOP_SF_CANNOT_PLACE_ORDER_TYPES',  0,  NULL,  'Customer: types that cannot place orders',
  'CSV of customer types that cannot place orders (e.g. B2B setup with browse catalog only)
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  5016,  'SHOP_ADRDIS_CUSTOMER_TYPES', 'SHOP_ADDRESSBOOK_DISABLED_CUSTOMER_TYPES',  0,  NULL,  'Customer: types that cannot modify address book',
  'CSV of customer types that cannot modify address book (e.g. B2B setup with admin authorised addresses only)
  E.g. B2B,B2E',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8007,  'SHOP_SF_PAGE_TRACE', 'SHOP_SF_PAGE_TRACE',  0,  NULL,  'Maintenance: enable page render trace',
  'If this is enabled html rendered will contain information about how this page was constructed (CMS includes, resources and cache info)',  1008, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8008,  'SHOP_PRODUCT_STORED_ATTRIBUTES', 'SHOP_PRODUCT_STORED_ATTRIBUTES',  0,  NULL,  'Product: stored attributes to copy to order',
  'Attributes that should be copied to order lines',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  8009,  'ORDER_EXP_MAIL_SUP', 'ORDER_EXPORTER_MAIL_SUPPORTED_SUPPLIERS',  0,  NULL,  'Export Orders\\Auto mail notification',
    'Property mapping for supplier codes and corresponding emails (CSV). This is email notification export to suppliers upon successful/authorised payment of the order.
E.g. MAIN.INITPAID=sales@warehouse.com,admin@warehouse.com
SECOND.INITPAID=sales@wahouse2.com',  1012, 1001, 0, 0, 0, 0);



INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11000,  'SYSTEM_DEFAULT_SHOP', 'SYSTEM_DEFAULT_SHOP',  1,  NULL,  'SF\\Behaviour Default shop URL',
  'This value will be used for redirects when shop can not be resolved by http request', 1002,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11017,  'SYSTEM_PREVIEW_URL_TEMPLATE', 'SYSTEM_PREVIEW_URL_TEMPLATE',  1,  NULL,  'Admin\\CMS preview URL template',
  'This template is used to adjust URLs in content (<img src=""/> and <a href=""/>). For example: http://{primaryShopURL}:8080/ where {primaryShopURL} is a placeholder', 1000,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11018,  'SYSTEM_PREVIEW_URI_CSS', 'SYSTEM_PREVIEW_URI_CSS',  1,  NULL,  'Admin\\CMS preview CSS URI',
  'This URI points to preview CSS. For example on SFW: "yes-shop/wicket/resource/org.yes.cart.web.page.HomePage/::/::/::/::/::/style/yc-preview.css"', 1000,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11029,  'SYSTEM_PANEL_HELP_DOCS', 'SYSTEM_PANEL_HELP_DOCS',  1,  NULL,  'Admin\\Help doc link',
  'Help doc link in Admin help section', 1002,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11030,  'SYSTEM_PANEL_HELP_COPYRIGHT', 'SYSTEM_PANEL_HELP_COPYRIGHT',  1,  NULL,  'Admin\\Copyright note',
  'Copyright note in Admin help section', 1011,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11031,  'SYSTEM_PANEL_LABEL', 'SYSTEM_PANEL_LABEL',  1,  NULL,  'Admin\\System label',
  'Label on the side panel to warn of the type of environment you are on', 1011,  1000, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11042,  'IMPORT_JOB_LOG_SIZE', 'IMPORT_JOB_LOG_SIZE',  1,  NULL,  'Admin\\Import Import log file size',
  'Size in characters of tail of actual log file to display in Admin during import', 1006,  1000, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11043,  'IMPORT_JOB_TIMEOUT_MS', 'IMPORT_JOB_TIMEOUT_MS',  1,  NULL,  'Admin\\Import job timeout',
  'Timeout in ms during which no ping action performed by import', 1006,  1000, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11044,  'SYSTEM_BACKDOOR_TIMEOUT_MS', 'SYSTEM_BACKDOOR_TIMEOUT_MS',  1,  NULL,  'Admin\\Communication timeout',
  'Timeout in ms for cluster calls', 1006,  1000, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11045,  'SYSTEM_BACKDOOR_PRODB_IDX_TIMEOUT_MS', 'SYSTEM_BACKDOOR_PRODB_IDX_TIMEOUT_MS',  1,  NULL,
  'Admin\\Indexing Bulk product index timeout', 'Timeout in ms for cluster calls', 1006,  1000, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11046,  'SYSTEM_BACKDOOR_PRODS_IDX_TIMEOUT_MS', 'SYSTEM_BACKDOOR_PRODS_IDX_TIMEOUT_MS',  1,  NULL,
  'Admin\\Indexing Single product index timeout', 'Timeout in ms for cluster calls', 1006,  1000, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11047,  'SYSTEM_BACKDOOR_SQL_TIMEOUT_MS', 'SYSTEM_BACKDOOR_SQL_TIMEOUT_MS',  1,  NULL,
  'Admin\\System SQL, HSQL, FTQL timeout', 'Timeout in ms for cluster calls', 1006,  1000, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11048,  'SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS', 'SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS',  1,  NULL,
  'Admin\\System Clear cache timeout', 'Timeout in ms for cluster calls', 1006,  1000, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11049,  'SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS', 'SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS',  1,  NULL,
  'Admin\\Import Image operation timeout', 'Timeout in ms for cluster calls', 1006,  1000, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10099,  'SYSTEM_ALLOWED_IMAGE_SIZES', 'SYSTEM_ALLOWED_IMAGE_SIZES',  1,  NULL,
  'SF\\Image service: allowed image sizes', 'Image resolutions allowed to be processed by shop
  E.g. 40x40,50x50,60x60,80x80,200x200,160x160,360x360,120x120,280x280,240x240 ', 1004,  1000, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11098,  'SYSTEM_ETAG_CACHE_IMAGES_TIME', 'SYSTEM_ETAG_CACHE_IMAGES_TIME',  0,  NULL,  'SF\\Behaviour Expiration value for images in minutes',
  'Expiration value for images in minutes. ETag', 1000,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11099,  'SYSTEM_IMAGE_VAULT', 'SYSTEM_IMAGE_VAULT',  1,  NULL,  'System\\Image Root directory for image repository',
  'Root directory for image repository.
Default: context://../imagevault
Recommended: file:///home/yc/server/share/imagevault', 1001,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11120,  'SYSTEM_FILE_VAULT', 'SYSTEM_FILE_VAULT',  1,  NULL,  'System\\File Root directory for image repository',
  'Root directory for file repository.
Default: context://../filevault
Recommended: file:///home/yc/server/share/filevault', 1001,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11121,  'SYSTEM_SYSFILE_VAULT', 'SYSTEM_SYSFILE_VAULT',  1,  NULL,  'System\\System File Root directory for image repository',
  'Root directory for secure file repository (no storefont access)
Default: context://../sysfilevault
Recommended: file:///home/yc/server/share/sysfilevault', 1001,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11100,  'SEARCH_ITEMS_PER_PAGE', 'SEARCH_ITEMS_PER_PAGE',  1,  NULL,  'SF\\Behaviour Search items per page ',
  'Search items per page (e.g. "10,20,40")', 1004,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11102,  'SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL', 'SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL',  1,  NULL,  'System\\Payment Active payment modules ',
  'Active payment modules', 1004,  1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11103,  'CART_ABANDONED_TIMEOUT_SECONDS', 'CART_ABANDONED_TIMEOUT_SECONDS',  0,  NULL,  'SF\\Behaviour Cart: abandonment in seconds',
    'Cart abandonment seconds. All abandoned carts are deleted by bulk job. Default: 2592000s (30 days)',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11104,  'JOB_SEND_MAIL_PAUSE', 'JOB_SEND_MAIL_PAUSE',  0,  NULL,  'Job\\Mail: pause mail processing',
    'Pause email sending job',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11106,  'JOB_LOCAL_FILE_IMPORT_PAUSE', 'JOB_LOCAL_FILE_IMPORT_PAUSE',  0,  NULL,  'Job\\Auto Import: pause import listener',
    'Pause local file system import listener',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11206,  'JOB_EXPIRE_GUESTS_PAUSE', 'JOB_EXPIRE_GUESTS_PAUSE',  0,  NULL,  'Job\\Expired Guest Accounts Clean Up: pause clean up',
    'Pause guest accounts deletion (guest checkouts)',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11207,  'JOB_CUSTOMER_TAG_PAUSE', 'JOB_CUSTOMER_TAG_PAUSE',  0,  NULL,  'Job\\Customer Tagging: pause tagging',
    'Pause customer tagging (batch processing of tagging promotions)',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11208,  'JOB_ABANDONED_CARTS_PAUSE', 'JOB_ABANDONED_CARTS_PAUSE',  0,  NULL,  'Job\\Abandoned Shopping Cart State Clean Up: pause clean up',
    'Pause abandoned cart clean up (batch removal of old carts)',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11209,  'JOB_EMPTY_CARTS_PAUSE', 'JOB_EMPTY_CARTS_PAUSE',  0,  NULL,  'Job\\Empty Anonymous Shopping Cart State Clean Up: pause clean up',
    'Pause empty cart clean up (batch removal of empty carts)',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11210,  'JOB_PRODINVUP_PAUSE', 'JOB_PRODINVUP_PAUSE',  0,  NULL,  'Job\\Inventory Change Detection: pause reindex',
    'Pause product change detection job (stops re-indexing of changed inventory)',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11211,  'JOB_GLOBALREINDEX_PAUSE', 'JOB_GLOBALREINDEX_PAUSE',  0,  NULL,  'Job\\Reindex All Products: pause reindex',
    'Pause full products reindex job',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11212,  'JOB_EXPIREREINDEX_PAUSE', 'JOB_EXPIREREINDEX_PAUSE',  0,  NULL,  'Job\\Reindex Discontinued Products: pause reindex',
    'Pause discontinued products reindex job',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11213,  'JOB_DELWAITINV_PAUSE', 'JOB_DELIVERY_WAIT_INVENTORY_PAUSE',  0,  NULL,  'Job\\Inventory Awaiting Delivery Processing: pause',
    'Pause inventory awaiting delivery processing job',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11214,  'JOB_DELINFOUPDT_PAUSE', 'JOB_DELIVERY_INFO_UPDATE_PAUSE',  0,  NULL,  'Job\\Order Delivery Information Update Processing: pause',
    'Pause order delivery information update processing job',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11215,  'JOB_ORDERAUTOEXP_PAUSE', 'JOB_ORDER_AUTO_EXPORT_PAUSE',  0,  NULL,  'Job\\Order Auto Export Processing: pause',
    'Pause order auto export processing job',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11107,  'JOB_LOCAL_FILE_IMPORT_FS_ROOT', 'JOB_LOCAL_FILE_IMPORT_FS_ROOT',  0,  NULL,  'Job\\Auto Import: listener directory root',
    'Directory root for listener to check for updates',  1001, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11108,  'JOB_DEL_WAITING_INV_LAST_RUN', 'JOB_DEL_WAITING_INV_LAST_RUN',  0,  NULL,  'Job\\Inventory Reservation: Last run timestamp',
    'Timestamp of last run of the inventory job. Used to check inventory changes.',  1009, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11109,  'CART_EMPTY_ANONYMOUS_TIMEOUT_SECONDS', 'CART_EMPTY_ANONYMOUS_TIMEOUT_SECONDS',  0,  NULL,  'Cart: empty anonymous in seconds',
    'Cart empty anonymous seconds. All empty anonymous carts are deleted by bulk job. Default: 86400s (1 days)',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11110,  'SHOP_CATEGORY_ITEMS_FEATURED', 'SHOP_CATEGORY_ITEMS_FEATURED',  0,  NULL,  'Category: Quantity of featured items to show on category page',
   'How many featured items need to show',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11111,  'SHOP_CATEGORY_ITEMS_NEW_ARRIVAL', 'SHOP_CATEGORY_ITEMS_NEW_ARRIVAL',  0,  NULL,  'Category: Quantity of new arrival items to show on category page',
   'Quantity of new arrival items to show on category page',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11112,  'SHOP_CATEGORY_PRODUCTS_COLUMNS', 'SHOP_CATEGORY_PRODUCTS_COLUMNS',  0,  NULL,  'Category: Quantity of product pods in one row on category page',
   'Quantity of product pods in one row to show on category page',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11113,  'SHOP_CATEGORY_SUBCATEGORIES_COLUMNS', 'SHOP_CATEGORY_SUBCATEGORIES_COLUMNS',  0,  NULL,  'Category: Quantity of category pods in one row on category page',
   'Quantity of product pods in one row to show on category page',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11114,  'JOB_LOCAL_IMAGEVAULT_SCAN_PAUSE', 'JOB_LOCAL_IMAGEVAULT_SCAN_PAUSE',  0,  NULL,  'Job\\Image vault scan: pause image vault scanning',
    'Pause local file system image vault scanner',  1008, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11115,  'GUESTS_EXPIRY_TIMEOUT_SECONDS', 'GUESTS_EXPIRY_TIMEOUT_SECONDS',  0,  NULL,  'SF\\Behaviour Cart: guests expiry in seconds',
    'Guest account expiry seconds. All expired guest accounts are deleted by bulk job. Default: 86400s (1 day)',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11116,  'JOB_REINDEX_PRODUCT_BATCH_SIZE', 'JOB_REINDEX_PRODUCT_BATCH_SIZE',  0,  NULL,  'Job\\Product re-index: batch size',
    'Number of products to reindex in single batch.',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11117,  'JOB_PRODINVUP_DELTA', 'JOB_PRODINVUP_DELTA',  0,  NULL,  'Job\\Inventory Change Detection: max delta after delay',
    'Number of inventory records that had changed after a second the job started (default is 100). If changes exceed this number then re-indexing is postponed.',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11118,  'JOB_PRODINVUP_FULL', 'JOB_PRODINVUP_FULL',  0,  NULL,  'Job\\Inventory Change Detection: changes for full reindex',
    'Number of inventory records that should trigger full re-index rather than partial. Default is 1000',  1006, 1000, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11119,  'JOB_PRODINVUP_DELTA_S', 'JOB_PRODINVUP_DELTA_S',  0,  NULL,  'Job\\Inventory Change Detection: delta delay in seconds',
    'Delay for delta check. Default 15s.',  1006, 1000, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11301,  'SHOP_IMAGE0',  'SHOP_IMAGE0',  1,  NULL,  'Shop: Shop image default',  null,  1003, 1001, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11302,  'SHOP_IMAGE0_ru',  'SHOP_IMAGE0_ru',  1,  NULL,  'Shop: Картинка магазина по умолчанию (RU)',  null,  1003, 1001, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11303,  'SHOP_IMAGE0_en',  'SHOP_IMAGE0_en',  1,  NULL,  'Shop: Shop image default (EN)',  null,  1003, 1001, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11304,  'SHOP_IMAGE0_uk',  'SHOP_IMAGE0_uk',  1,  NULL,  'Shop: Картинка магазину за замовчуванням  (UK)',  null,  1003, 1001, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11305,  'SHOP_IMAGE0_de',  'SHOP_IMAGE0_de',  1,  NULL,  'Shop: Shop standard image  (DE)',  null,  1003, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11306,  'SHOP_FILE0',  'SHOP_FILE0',  1,  NULL,  'Shop: Shop file',  null,  1013, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11307,  'SHOP_SYSFILE0',  'SHOP_SYSFILE0',  1,  NULL,  'Shop: Shop system file',  null,  1014, 1001, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11001,  'BRAND_IMAGE0', 'BRAND_IMAGE0',  1,  NULL,  'Brand image',  null,  1003, 1005, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11311,  'BRAND_IMAGE0_ru',  'BRAND_IMAGE0_ru',  1,  NULL,  'Картинка ТМ по умолчанию (RU)',  null,  1003, 1005, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11312,  'BRAND_IMAGE0_en',  'BRAND_IMAGE0_en',  1,  NULL,  'Brand image default (EN)',  null,  1003, 1005, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11313,  'BRAND_IMAGE0_uk',  'BRAND_IMAGE0_uk',  1,  NULL,  'Картинка ТМ за замовчуванням  (UK)',  null,  1003, 1005, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11310,  'BRAND_IMAGE0_de',  'BRAND_IMAGE0_de',  1,  NULL,  'Marke standard image  (DE)',  null,  1003, 1005, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11309,  'BRAND_FILE0', 'BRAND_FILE0',  1,  NULL,  'Brand file',  null,  1013, 1005, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11002,  'CATEGORY_ITEMS_PER_PAGE', 'CATEGORY_ITEMS_PER_PAGE',  0,  NULL,  'Category: item per page settings (CSV)',
   'Category item per page settings with fail over. Default is: 10,20,30',  1004, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11003,  'SHOP_CATEGORY_ITEMS_PER_PAGE',  'SHOP_CATEGORY_ITEMS_PER_PAGE',  0,  NULL,  'Category: item per page settings (CSV)',
   'Category item per page settings with fail over. Default is: 10,20,30',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11004,  'CATEGORY_IMAGE0', 'CATEGORY_IMAGE0',  0,  NULL,  'Category image',   'Category image',  1003, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11314,  'CATEGORY_IMAGE0_ru',  'CATEGORY_IMAGE0_ru',  0,  NULL,  'Картинка категории по умолчанию (RU)',   null,  1003, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11315,  'CATEGORY_IMAGE0_en',  'CATEGORY_IMAGE0_en',  0,  NULL,  'Category image default (EN)',   null,  1003, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11316,  'CATEGORY_IMAGE0_uk',  'CATEGORY_IMAGE0_uk',  0,  NULL,  'Картинка категорії за замовчуванням  (UK)',   null,  1003, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11105,  'CATEGORY_IMAGE0_de',  'CATEGORY_IMAGE0_de',  0,  NULL,  'Kategorie standard image  (DE)',   null,  1003, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11320,  'CATEGORY_FILE0', 'CATEGORY_FILE0',  0,  NULL,  'Category file',   'Category file',  1003, 1002, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11317,  'CATEGORY_SORT_OPTIONS', 'CATEGORY_SORT_OPTIONS',  0,  NULL,  'Category: sortable fields (CSV)',
    'List of sort fields separated by comma with fail over. Default is: displayName,sku,basePrice. All supported: name,displayName,basePrice,productCode,manufacturerCode,sku,brand,availability,created',  1004, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11318,  'SHOP_CATEGORY_SORT_OPTIONS', 'SHOP_CATEGORY_SORT_OPTIONS',  0,  NULL,  'Category: sortable fields (CSV)',
    'List of sort fields separated by comma with fail over. Default is: displayName,sku,basePrice. All supported: name,displayName,basePrice,productCode,manufacturerCode,sku,brand,availability,created',  1004, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11319,  'CATEGORY_FILTERNAV_LIMIT', 'CATEGORY_FILTERNAV_LIMIT',  0,  NULL,  'Filter navigation records limit',
  'Filter navigation records limit per group. Default is 25',  1006, 1002, 0, 0, 0, 0);



INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11005,  'CATEGORY_IMAGE_RETRIEVE_STRATEGY', 'CATEGORY_IMAGE_RETRIEVE_STRATEGY',  0,  NULL,  'Strategy to retrieve image',
  'Strategy to retrieve images. Allowed values: [ATTRIBUTE] i.e. use CATEGORY_IMAGE attribute or [RANDOM_PRODUCT] i.e. random product image will be used',  1000, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11006,  'CATEGORY_DESCRIPTION_en', 'CATEGORY_DESCRIPTION_en',  0,  NULL,  'Category Description (en)',
  'Category Description in English (en)',  1011, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11007,  'CATEGORY_DESCRIPTION_ru', 'CATEGORY_DESCRIPTION_ru',  0,  NULL,  'Описание Категории (ru)',
  'Описание Категории на Русском (ru)',  1011, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11014,  'CATEGORY_DESCRIPTION_uk', 'CATEGORY_DESCRIPTION_uk',  0,  NULL,  'Опис Категорії (uk)',
  'Опис Категорії Українською (uk)',  1011, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11015,  'CATEGORY_DESCRIPTION_de', 'CATEGORY_DESCRIPTION_de',  0,  NULL,  'Kategorie beschreibung (de)',
  'Kategorie Beschreibung in Deutsch (de)',  1011, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11058,  'CATEGORY_ITEMS_FEATURED', 'CATEGORY_ITEMS_FEATURED',  0,  NULL,  'Quantity of featured items to show on category page',
   'How many featured items need to show',  1006, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11059,  'CATEGORY_ITEMS_NEW_ARRIVAL', 'CATEGORY_ITEMS_NEW_ARRIVAL',  0,  NULL,  'Quantity of new arrival items to show on category page',
   'Quantity of new arrival items to show on category page',  1006, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11056,  'CATEGORY_SUBCATEGORIES_COLUMNS', 'CATEGORY_SUBCATEGORIES_COLUMNS',  0,  NULL,  'Quantity of category pods in one row on category page',
   'Quantity of category pods in one row to show on category page',  1006, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11057,  'CATEGORY_PRODUCTS_COLUMNS', 'CATEGORY_PRODUCTS_COLUMNS',  0,  NULL,  'Quantity of product pods in one row on category page',
   'Quantity of product pods in one row to show on category page',  1006, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10991,  'INCLUDE_SUBCATEGORIES_IN_SEARCH_CAT', 'INCLUDE_SUBCATEGORIES_IN_SEARCH_CAT',  0,  NULL,  'Search: include sub categories',  'Search: include sub categories',  1008, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  10989,  'CATEGORY_NEW_ARRIVAL_DAYS_OFFSET', 'CATEGORY_NEW_ARRIVAL_DAYS_OFFSET',  0,  NULL,  'Search: newarrival tag days offset',
   'Search: newarrival tag days offset',  1006, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11060,  'CONTENT_BODY_en_1', 'CONTENT_BODY_en_1',  0,  NULL,  'Content body (en)',
  'Content body in English (en). 1st 4000 characters',  1011, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11061,  'CONTENT_BODY_en_2', 'CONTENT_BODY_en_2',  0,  NULL,  'Content body (en)',
  'Content body in English (en). 2nd 4000 characters',  1011, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11070,  'CONTENT_BODY_ru_1', 'CONTENT_BODY_ru_1',  0,  NULL,  'Текст контента (ru)',
  'Текст контента на Русском (ru). 1-е 4000 символов.',  1011, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11071,  'CONTENT_BODY_ru_2', 'CONTENT_BODY_ru_2',  0,  NULL,  'Текст контента (ru)',
  'Текст контента на Русском (ru). 2-е 4000 символов.',  1011, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11080,  'CONTENT_BODY_uk_1', 'CONTENT_BODY_uk_1',  0,  NULL,  'Текст контенту (uk)',
  'Текст контенту Українською (uk). 1-ші 4000 символів.',  1011, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11081,  'CONTENT_BODY_uk_2', 'CONTENT_BODY_uk_2',  0,  NULL,  'Текст контенту (uk)',
  'Текст контенту Українською (uk). 2-гі 4000 символів.',  1011, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11090,  'CONTENT_BODY_de_1', 'CONTENT_BODY_de_1',  0,  NULL,  'Inhalt (de)',
  'Inhalt in Deutsch (de). erste 4000 Zeichen',  1011, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11091,  'CONTENT_BODY_de_2', 'CONTENT_BODY_de_2',  0,  NULL,  'Inhalt (de)',
  'Inhalt in Deutsch (de). zweite 4000 Zeichen',  1011, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  12000,  'PRODUCT_IMAGE_WIDTH', 'PRODUCT_IMAGE_WIDTH',  0,  NULL,  'Product image width in category',   'Product image width in category',  1006, 1002, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  12001,  'PRODUCT_IMAGE_HEIGHT', 'PRODUCT_IMAGE_HEIGHT',  0,  NULL,  'Product image height in category',   'Product image height in category',  1006, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  12004,  'SHOP_PRODUCT_IMAGE_WIDTH',  'SHOP_PRODUCT_IMAGE_WIDTH',  0,  NULL,  'Category: Product image width in category',   'Product image width in category',  1006, 1001, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  12005,  'SHOP_PRODUCT_IMAGE_HEIGHT',  'SHOP_PRODUCT_IMAGE_HEIGHT',  0,  NULL,  'Category: Product image height in category',   'Product image height in category',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  12050,  'PRODUCT_IMAGE_THUMB_WIDTH', 'PRODUCT_IMAGE_THUMB_WIDTH',  0,  NULL,  'Product thumbnail image width',   'Product thumbnail image width',  1006, 1002, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  12051,  'PRODUCT_IMAGE_THUMB_HEIGHT', 'PRODUCT_IMAGE_THUMB_HEIGHT',  0,  NULL,  'Product thumbnail image height',   'Product thumbnail image height',  1006, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  12006,  'SHOP_PRODUCT_IMAGE_THUMB_WIDTH',  'SHOP_PRODUCT_IMAGE_THUMB_WIDTH',  0,  NULL,  'Category: Product thumbnail image width',   'Product thumbnail image width',  1006, 1001, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  12007,  'SHOP_PRODUCT_IMAGE_THUMB_HEIGHT',  'SHOP_PRODUCT_IMAGE_THUMB_HEIGHT',  0,  NULL,  'Category: Product thumbnail image height',   'Product thumbnail image height',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11998,  'CATEGORY_IMAGE_WIDTH', 'CATEGORY_IMAGE_WIDTH',  0,  NULL,  'Category image  width ',   'Category image width thumbnail ',  1006, 1002, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11999,  'CATEGORY_IMAGE_HEIGHT', 'CATEGORY_IMAGE_HEIGHT',  0,  NULL,  'Category image   height',   'Category image height thumbnail ',  1006, 1002, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  12002,  'SHOP_CATEGORY_IMAGE_WIDTH',  'SHOP_CATEGORY_IMAGE_WIDTH',  0,  NULL,  'Category: Category image  width ',   'Category image width thumbnail ',  1006, 1001, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  12003,  'SHOP_CATEGORY_IMAGE_HEIGHT',  'SHOP_CATEGORY_IMAGE_HEIGHT',  0,  NULL,  'Category: Category image   height',   'Category image height thumbnail ',  1006, 1001, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11008,  'IMAGE0', 'IMAGE0',  1,  NULL,  'Product default image',  'Product default image',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11009,  'IMAGE1', 'IMAGE1',  0,  NULL,  'Product alternative image 1',  'Product alternative image 1',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11010,  'IMAGE2', 'IMAGE2',  0,  NULL,  'Product alternative image 2',  'Product alternative image 2',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11011,  'IMAGE3', 'IMAGE3',  0,  NULL,  'Product alternative image 3',  'Product alternative image 3',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11012,  'IMAGE4', 'IMAGE4',  0,  NULL,  'Product alternative image 4',  'Product alternative image 4',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11013,  'IMAGE5', 'IMAGE5',  0,  NULL,  'Product alternative image 5',  'Product alternative image 5',  1003, 1003, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11350,  'IMAGE0_ru',  'IMAGE0_ru',  1,  NULL,  'Картинка товара по умолчанию (RU)',  'Картинка товара по умолчанию (RU)',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11351,  'IMAGE1_ru',  'IMAGE1_ru',  0,  NULL,  'Альтернативная картинка товара 1',  'Альтернативная картинка товара 1',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11352,  'IMAGE2_ru',  'IMAGE2_ru',  0,  NULL,  'Альтернативная картинка товара 2',  'Альтернативная картинка товара 2',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11353,  'IMAGE3_ru',  'IMAGE3_ru',  0,  NULL,  'Альтернативная картинка товара 3',  'Альтернативная картинка товара 3',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11354,  'IMAGE4_ru',  'IMAGE4_ru',  0,  NULL,  'Альтернативная картинка товара 4',  'Альтернативная картинка товара 4',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11355,  'IMAGE5_ru',  'IMAGE5_ru',  0,  NULL,  'Альтернативная картинка товара 5',  'Альтернативная картинка товара 5',  1003, 1003, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11356,  'IMAGE0_en',  'IMAGE0_en',  1,  NULL,  'Product default image (EN)',  'Product default image (EN)',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11357,  'IMAGE1_en',  'IMAGE1_en',  0,  NULL,  'Product alternative image 1 (EN)',  'Product alternative image 1 (EN)',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11358,  'IMAGE2_en',  'IMAGE2_en',  0,  NULL,  'Product alternative image 2 (EN)',  'Product alternative image 2 (EN)',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11359,  'IMAGE3_en',  'IMAGE3_en',  0,  NULL,  'Product alternative image 3 (EN)',  'Product alternative image 3 (EN)',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11360,  'IMAGE4_en',  'IMAGE4_en',  0,  NULL,  'Product alternative image 4 (EN)',  'Product alternative image 4 (EN)',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11361,  'IMAGE5_en',  'IMAGE5_en',  0,  NULL,  'Product alternative image 5 (EN)',  'Product alternative image 5 (EN)',  1003, 1003, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11362,  'IMAGE0_uk',  'IMAGE0_uk',  1,  NULL,  'Картинка товару за замовчуванням  (UK)',  'Картинка товару за замовчуванням  (UK)',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11363,  'IMAGE1_uk',  'IMAGE1_uk',  0,  NULL,  'Альтернативна картинка товару 1',  'Альтернативна картинка товару 1',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11364,  'IMAGE2_uk',  'IMAGE2_uk',  0,  NULL,  'Альтернативна картинка товару 2',  'Альтернативна картинка товару 2',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11365,  'IMAGE3_uk',  'IMAGE3_uk',  0,  NULL,  'Альтернативна картинка товару 3',  'Альтернативна картинка товару 3',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11366,  'IMAGE4_uk',  'IMAGE4_uk',  0,  NULL,  'Альтернативна картинка товару 4',  'Альтернативна картинка товару 4',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11367,  'IMAGE5_uk',  'IMAGE5_uk',  0,  NULL,  'Альтернативна картинка товару 5',  'Альтернативна картинка товару 5',  1003, 1003, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11368,  'IMAGE0_de',  'IMAGE0_de',  1,  NULL,  'Produkt standard image  (DE)',  'Produkt standard image  (DE)',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11369,  'IMAGE1_de',  'IMAGE1_de',  0,  NULL,  'Produkt alternative image 1',  'Produkt alternative image 1',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11370,  'IMAGE2_de',  'IMAGE2_de',  0,  NULL,  'Produkt alternative image 2',  'Produkt alternative image 2',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11371,  'IMAGE3_de',  'IMAGE3_de',  0,  NULL,  'Produkt alternative image 3',  'Produkt alternative image 3',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11372,  'IMAGE4_de',  'IMAGE4_de',  0,  NULL,  'Produkt alternative image 4',  'Produkt alternative image 4',  1003, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11373,  'IMAGE5_de',  'IMAGE5_de',  0,  NULL,  'Produkt alternative image 5',  'Produkt alternative image 5',  1003, 1003, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11020,  'PRODUCT_DESCRIPTION_en', 'PRODUCT_DESCRIPTION_en',  0,  NULL,  'Product Description (en)',  'Product Description in English (en)',  1011, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11021,  'PRODUCT_DESCRIPTION_ru', 'PRODUCT_DESCRIPTION_ru',  0,  NULL,  'Описание Продукта (ru)',  'Описание Продукта на Русском (ru)',  1011, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11022,  'PRODUCT_DESCRIPTION_uk', 'PRODUCT_DESCRIPTION_uk',  0,  NULL,  'Опис Продукту (uk)',  'Опис Продукту Українською (uk)',  1011, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11023,  'PRODUCT_DESCRIPTION_de', 'PRODUCT_DESCRIPTION_de',  0,  NULL,  'Produkt beschreibung (de)',  'Produkt beschreibung in Deutsch (de)',  1011, 1003, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11024,  'PRODUCT_WEIGHT_KG', 'PRODUCT_WEIGHT_KG',  0,  NULL,  'Product weight KG',  'Product weight KG',  1005, 1003, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11025,  'PRODUCT_VOLUME_M3', 'PRODUCT_VOLUME_M3',  0,  NULL,  'Product volume m3',  'Product volume m3',  1005, 1003, 0, 0, 0, 0);


INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11050,  'CUSTOMER_PHONE', 'CUSTOMER_PHONE',  1,  NULL,  'Phone',  'Phone', 1007,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11051,  'MARKETING_OPT_IN', 'MARKETING_OPT_IN',  0,  NULL,  'Marketing Opt in',  'If true then customer opted in for marketing contact', 1008,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, CHOICES, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11052,  'salutation', 'salutation',  0,  NULL,  'Salutation',  'Salutation CSV options
e.g. "en|Mr-Mr,Mrs-Mrs,Dr-Dr"', 'en#~#Mrs-Mrs,Miss-Miss,Mr-Mr#~#uk#~#Пані-Пані,Пан-Пан#~#ru#~#-#~#de#~#Frau-Frau,Herr-Herr', 1004,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11053,  'firstname', 'firstname',  1,  NULL,  'First name',  'First name', 1000,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11054,  'lastname', 'lastname',  1,  NULL,  'Last name',  'Last name', 1000,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11055,  'middlename', 'middlename',  0,  NULL,  'Middle name',  'Middle name', 1000,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11062,  'customertype', 'customertype',  1,  NULL,  'Customer Type',  'Customer Type', 1000,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11063,  'pricingpolicy', 'pricingpolicy',  1,  NULL,  'Customer Pricing Policy',  'Customer Pricing Policy', 1000,  1006, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11064,  'CUSTOMER_B2B_REF', 'CUSTOMER_B2B_REF',  1,  NULL,  'B2B Ref',  'Default customer reference on placed orders', 1000,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11065,  'CUSTOMER_B2B_EMPID', 'CUSTOMER_B2B_EMPLOYEE_ID',  1,  NULL,  'B2B Employee ID',  'B2B Employee ID, used when placing order', 1000,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11066,  'CUSTOMER_B2B_CHRGID', 'CUSTOMER_B2B_CHARGE_ID',  1,  NULL,  'B2B Charge to ID',  'B2B Charge to ID, used when placing order', 1000,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11067,  'CUSTOMER_B2B_REQAPPR', 'CUSTOMER_B2B_REQUIRE_APPROVE',  1,  NULL,  'B2B Require order approval',  'If set to true orders of this customer will need to be approved by supervisor', 1008,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11068,  'CUSTOMER_BLOCK_CHKOUT', 'CUSTOMER_BLOCK_CHECKOUT',  1,  NULL,  'Block checkout',  'If set to true customer cannot proceed with checkout', 1008,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11150,  'CUSTOMER_B2B_REQAPPR_X', 'CUSTOMER_B2B_REQUIRE_APPROVE_X',  1,  NULL,  'B2B Require order approval over X',  'Order amount that will trigger approval flag on this order. e.g. 1000', 1006,  1006, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11151,  'CUSTOMER_BLOCK_CHKOUT_X', 'CUSTOMER_BLOCK_CHECKOUT_X',  1,  NULL,  'Block checkout over X',  'Order amount that blocks checkou. e.g. 10000t', 1006,  1006, 0, 0, 0, 0);

INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11200,  'default_addressform', 'default_addressform',  0,  NULL,  'Customer: "default_" address form (CSV)',
    'List of address form attributes separated by comma.
Available fields:
salutation, firstname, middlename, lastname
addrline1, addrline2, postcode, city, countrycode, statecode
phone1, phone2, mobile1, mobile2
email1, email2
custom0, custom1, custom2, custom3, custom4
custom5, custom6, custom7, custom8, custom9',  1004, 1007, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, CHOICES, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11201,  'default_salutation', 'default_salutation',  0,  'salutation',  'Salutation',  'Salutation CSV options
e.g. "en|Mr-Mr,Mrs-Mrs,Dr-Dr"', 'en#~#Mrs-Mrs,Miss-Miss,Mr-Mr#~#uk#~#Пані-Пані,Пан-Пан#~#ru#~#-#~#de#~#Frau-Frau,Herr-Herr', 1004,  1007, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11202,  'default_firstname', 'default_firstname',  1,  'firstname',  'First name',  'First name', 1000,  1007, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11203,  'default_lastname', 'default_lastname',  1,  'lastname',  'Last name',  'Last name', 1000,  1007, 0, 0, 0, 0);
INSERT INTO TATTRIBUTE (ATTRIBUTE_ID, GUID, CODE, MANDATORY, VAL, NAME, DESCRIPTION, ETYPE_ID, ATTRIBUTEGROUP_ID, STORE, SEARCH, SEARCHPRIMARY, NAV)
  VALUES (  11204,  'default_middlename', 'default_middlename',  0,  'middlename',  'Middle name',  'Middle name', 1000,  1007, 0, 0, 0, 0);



INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID, GUID, NAME, DESCRIPTION, SERVICE, ENSEMBLE, SHIPPABLE)
  VALUES (500,'Default Product', 'Default Product','Default Product', 0,0,1);
INSERT INTO TPRODUCTTYPE (PRODUCTTYPE_ID, GUID, NAME, DESCRIPTION, SERVICE, ENSEMBLE, SHIPPABLE)
  VALUES (501,'Default Accessory', 'Default Accessory','Default Accessory', 0,0,1);


INSERT INTO TSYSTEM (SYSTEM_ID, GUID, CODE, NAME, DESCRIPTION)  VALUES (100, 'YC', 'SYSTEM','Yes e-commerce platform', 'System table');

INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1000,'http://localhost:8080/yes-shop','SYSTEM_DEFAULT_SHOP',100, 'YC_SYSTEM_DEFAULT_SHOP');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1002,'10,20,40','SEARCH_ITEMS_PER_PAGE',100, 'YC_SEARCH_ITEMS_PER_PAGE');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1003,'context://../imagevault','SYSTEM_IMAGE_VAULT',100, 'YC_SYSTEM_IMAGE_VAULT');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1024,'context://../filevault','SYSTEM_FILE_VAULT',100, 'YC_SYSTEM_FILE_VAULT');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1025,'context://../sysfilevault','SYSTEM_SYSFILE_VAULT',100, 'YC_SYSTEM_SYSFILE_VAULT');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1012,'testPaymentGatewayLabel,courierPaymentGatewayLabel,inStorePaymentGatewayLabel,prePaymentGatewayLabel','SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL',100, 'YC_SYSTEM_ACTIVE_PAYMENT_GATEWAYS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1014,'60','SYSTEM_ETAG_CACHE_IMAGES_TIME',100, 'YC_SYSTEM_ETAG_CACHE_IMAGES_TIME');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1015,'10000','IMPORT_JOB_LOG_SIZE',100, 'YC_IMPORT_JOB_LOG_SIZE');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1016,'60000','IMPORT_JOB_TIMEOUT_MS',100, 'YC_IMPORT_JOB_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1017,'60000','SYSTEM_BACKDOOR_TIMEOUT_MS',100, 'YC_SYSTEM_BACKDOOR_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1018,'60000','SYSTEM_BACKDOOR_PRODB_IDX_TIMEOUT_MS',100, 'YC_SYSTEM_BDOOR_PRODB_IDX_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1019,'60000','SYSTEM_BACKDOOR_PRODS_IDX_TIMEOUT_MS',100, 'YC_SYSTEM_BDOOR_PRODS_IDX_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1020,'60000','SYSTEM_BACKDOOR_SQL_TIMEOUT_MS',100, 'YC_SYSTEM_BACKDOOR_SQL_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1021,'60000','SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS',100, 'YC_SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1022,'60000','SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS',100, 'YC_SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1023,'40x40,50x50,60x60,80x80,200x200,160x160,360x360,120x120,280x280,240x240','SYSTEM_ALLOWED_IMAGE_SIZES',100, 'YC_SYSTEM_ALLOWED_IMAGE_SIZES');
INSERT INTO TSYSTEMATTRVALUE ( ATTRVALUE_ID,  VAL,  CODE, SYSTEM_ID, GUID)  VALUES (1027,'<span class="label label-success">DEVELOPMENT ENVIRONMENT</span>','SYSTEM_PANEL_LABEL',100, 'YC_SYSTEM_PANEL_LABEL');


INSERT INTO TSHOP (SHOP_ID, NAME, DESCRIPTION, FSPOINTER, CODE, GUID)  VALUES (10, 'YesCart shop', 'YesCart shop', 'ycdemo', 'SHOP10', 'SHOP10');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (10, 'USD,EUR,UAH', 'CURRENCY', 10, 'USD,EUR,UAH');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (13, 'AU,AT,BE,BG,CA,CN,HR,CY,CZ,DK,EE,FI,FR,DE,GR,HK,HU,IE,IM,IT,JP,LV,LT,LU,NL,NZ,NO,PL,PT,RO,RU,SE,CH,TR,UA,GB,US', 'COUNTRY_SHIP', 10, 'COUNTRY_SHIP_10');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (14, 'AU,AT,BE,BG,CA,CN,HR,CY,CZ,DK,EE,FI,FR,DE,GR,HK,HU,IE,IM,IT,JP,LV,LT,LU,NL,NZ,NO,PL,PT,RO,RU,SE,CH,TR,UA,GB,US', 'COUNTRY_BILL', 10, 'COUNTRY_BILL_10');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (12, 'admin@at-shop-10.com', 'SHOP_ADMIN_EMAIL', 10, 'admin-email-guid');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (9, 'testPaymentGatewayLabel,courierPaymentGatewayLabel,inStorePaymentGatewayLabel,prePaymentGatewayLabel','SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL', 10, 'SHOP10_ACTIVE_PAYMENT_GATEWAYS_LABEL');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (15, 'true','SHOP_CHECKOUT_ENABLE_COUPONS', 10, 'SHOP10_SHOP_CHECKOUT_ENABLE_COUPONS');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (16, 'true','SHOP_CHECKOUT_ENABLE_ORDER_MSG', 10, 'SHOP10_SHOP_CHECKOUT_ENBL_ORDER_MSG');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (17, 'true','CART_ADD_ENABLE_QTY_PICKER', 10, 'SHOP10_CART_ADD_ENABLE_QTY_PICKER');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (18, 'true','SHOP_COOKIE_POLICY_ENABLE', 10, 'SHOP_COOKIE_POLICY_ENABLE');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (8, 'true','SHOP_CHECKOUT_ENABLE_GUEST', 10, 'SHOP_CHECKOUT_ENABLE_GUEST');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (19, 'r@nD()mTok3n4Pa$$Re$3+','SHOP_CUSTOMER_PASSWORD_RESET_CC', 10, 'SHOP_CUSTOMER_PASSWORD_RESET_CC');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (20, 'B2G,B2C,B2B,B2E','SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO', 10, 'SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (23, 'salutation,firstname,middlename,lastname,CUSTOMER_PHONE,MARKETING_OPT_IN','SHOP_CREGATTRS_B2C', 10, 'SHOP_CUSTOMER_REGISTRATION_10');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (7, 'firstname,lastname','SHOP_CREGATTRS_B2G', 10, 'SHOP_CUSTOMER_REGGUEST_10');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,CODE,SHOP_ID, GUID)  VALUES (24, 'salutation,firstname,middlename,lastname,CUSTOMER_PHONE,MARKETING_OPT_IN','SHOP_CPROFATTRS_VISIBLE_B2C', 10, 'SHOP_CUSTOMER_PROFILE_10');
INSERT INTO TSHOPATTRVALUE(ATTRVALUE_ID,VAL,DISPLAYVAL,CODE,SHOP_ID, GUID)  VALUES (25, 'B2C', 'en#~#B2C#~#uk#~#B2C#~#ru#~#B2C#~#de#~#B2C', 'SHOP_CUSTOMER_TYPES', 10, 'SHOP_CUSTOMER_TYPES_10');


INSERT INTO TWAREHOUSE (WAREHOUSE_ID, GUID, CODE, NAME, DESCRIPTION,  MULTI_SHIP_SUPPORTED) VALUES (1, 'Main', 'Main', 'Main warehouse', null, 1);
INSERT INTO TSHOPWAREHOUSE (SHOPWAREHOUSE_ID, SHOP_ID, WAREHOUSE_ID, RANK, GUID )
  VALUES (10, 10, 1, 10, 'SHOP10_Main' );

INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL, GUID )  VALUES (10, 10, 'testdevshop.yes-cart.org', 'SHOP10_10');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL, THEME_CHAIN, GUID )  VALUES (11, 10, 'demo.yes-cart.org', 'ycdemolive;ycdemo', 'SHOP10_11');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL, GUID )  VALUES (12, 10, 'localhost', 'SHOP10_12');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL, THEME_CHAIN, GUID )  VALUES (13, 10, 'demo-journal.yes-cart.org', 'journal;ycdemolive;ycdemo', 'SHOP10_13');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL, THEME_CHAIN, GUID )  VALUES (14, 10, 'demo-yeti.yes-cart.org', 'yeti;ycdemolive;ycdemo', 'SHOP10_14');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL, THEME_CHAIN, GUID )  VALUES (15, 10, 'demo-united.yes-cart.org', 'united;ycdemolive;ycdemo', 'SHOP10_15');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL, THEME_CHAIN, GUID )  VALUES (16, 10, 'demo-superhero.yes-cart.org', 'superhero;ycdemolive;ycdemo', 'SHOP10_16');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL, THEME_CHAIN, GUID )  VALUES (17, 10, 'demo-simplex.yes-cart.org', 'simplex;ycdemolive;ycdemo', 'SHOP10_17');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL, THEME_CHAIN, GUID )  VALUES (18, 10, 'demo-slate.yes-cart.org', 'slate;ycdemolive;ycdemo', 'SHOP10_18');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL, THEME_CHAIN, GUID )  VALUES (19, 10, 'demo-sandstone.yes-cart.org', 'sandstone;ycdemolive;ycdemo', 'SHOP10_19');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL, THEME_CHAIN, GUID )  VALUES (20, 10, 'demo-darkly.yes-cart.org', 'darkly;ycdemolive;ycdemo', 'SHOP10_20');
INSERT INTO TSHOPURL (STOREURL_ID, SHOP_ID, URL, THEME_CHAIN, GUID )  VALUES (21, 10, 'demo-cyborg.yes-cart.org', 'cyborg;ycdemolive;ycdemo', 'SHOP10_21');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID) VALUES (100, 100, 0, 'root', 'Master category','default', '100');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID) VALUES (10000, 0, 0, 'SHOP10 Content', 'SHOP10 Content','content', 'SHOP10');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID) VALUES (10100, 10000, 0, 'Page Templates', 'Page Templates','include', 'SHOP10-10100');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID) VALUES (10200, 10100, 0, 'General Components', 'General Components','include', 'SHOP10-10200');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID) VALUES (10201, 10200, 0, 'Header Component', 'Header Component','include', 'SHOP10-10201');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID) VALUES (10202, 10200, 0, 'Footer Component', 'Footer Component','include', 'SHOP10-10202');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID) VALUES (10250, 10100, 0, 'Shopping Cart Template', 'Shopping Cart Template','include', 'SHOP10-10250');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID) VALUES (10251, 10100, 0, 'Profile Template', 'Profile Template','include', 'SHOP10-10251');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID) VALUES (10300, 10000, 0, 'Microsites', 'Microsites','include', 'SHOP10-10300');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID) VALUES (10400, 10300, 0, 'Main site', 'Main site','include', 'SHOP10-10400');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID) VALUES (10500, 10100, 0, 'Payment Result Page', 'Payment Result Page','include', 'SHOP10-10500');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID, URI) VALUES (10510, 10500, 0, 'Internal payment gateway', 'Internal payment gateway','dynocontent', 'SHOP10-10510', 'SHOP10_paymentpage_message');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12510,'CONTENT_BODY_en_1','
<h2>Order Payment</h2>

<% if (result) { %>
   <p>
      Your order has been successfully created. You will receive confirmation by e-mail.
   </p>
   <a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Continue shopping</a>
   <% if (binding.hasVariable(''order'') && order.customer != null) { %>
      <a href="/yes-shop/orders" class="btn btn-primary" rel="nofollow">Check order status</a>
   <% } %>
<% } else {
   if (missingStock !=null) { %>
      <p>
         Item ${product} with code ${sku} has just gone out of stock. Please try to buy similar product
      </p>
      <a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Back to Home page</a>
   <% } else if (exception !=null) { %>
      <p>
         An error occurred while trying to create your order. Please try again.
      </p>
      <a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Back to Home page</a>
   <% } %>
<% } %>

',10510,'12510_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12511,'CONTENT_BODY_ru_1','
<h2>Оплата заказа</h2>

<% if (result) { %>
   <p>
      Ваш заказ был успешно оформлен. Вы получите уведомление на электронный адрес.
   </p>
   <a href="/yes-shop" class="btn btn-primary2" rel="bookmark">За новыми покупками</a>
   <% if (binding.hasVariable(''order'') && order.customer != null) { %>
       <a href="/yes-shop/orders" class="btn btn-primary" rel="nofollow">Проверить статус заказа</a>
   <% } %>
<% } else {
   if (missingStock !=null) { %>
      <p>
         Недостаточное количество ${product} (код ${sku}) на складе. Попробуйте купить похожий продукт. Приносим свои извинения
      </p>
      <a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Перейти на главную</a>
   <% } else if (exception !=null) { %>
      <p>
         Произошла ошибка при создании Вашего заказа. Попробуйте еще раз.
      </p>
      <a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Перейти на главную</a>
   <% } %>
<% } %>

',10510,'12511_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12513,'CONTENT_BODY_uk_1','
<h2>Оплата замовлення</h2>

<% if (result) { %>
   <p>
      Ваше замовлення було успішно оформлено. Ви отримаєте повідомлення на електронну адресу.
   </p>
   <a href="/yes-shop" class="btn btn-primary2" rel="bookmark">За новими покупками</a>
   <% if (binding.hasVariable(''order'') && order.customer != null) { %>
      <a href="/yes-shop/orders" class="btn btn-primary" rel="nofollow">Перевірити статус замовлення</a>
   <% } %>
<% } else {
   if (missingStock !=null) { %>
      <p>
         Недостатня кількість ${product} (код ${sku}) на складі. Спробуйте купити схожий товар. Приносимо вибачення
      </p>
      <a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Повернутися на головну</a>
   <% } else if (exception !=null) { %>
      <p>
         Сталася помилка при створені Вашого замовлення. Спробуйте ще раз.
      </p>
      <a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Повернутися на головну</a>
   <% } %>
<% } %>

',10510,'12513_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12514,'CONTENT_BODY_de_1','
<h2>Order Payment</h2>

<% if (result) { %>
   <p>
      Ihre Bestellung wurde erfolgreich erstellt. Sie erhalten eine Bestätigung per E-Mail.
   </p>
   <a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Weiter mit Einkaufen</a>
   <% if (binding.hasVariable(''order'') && order.customer != null) { %>
     <a href="/yes-shop/orders" class="btn btn-primary" rel="nofollow">Status der Bestellung überprüfen</a>
   <% } %>
<% } else {
   if (missingStock !=null) { %>
      <p>
         Leider ist der Artikel in der gewünschten Anzahl ${product} mit Artikel Nummer ${sku} nicht an Lager. Versuchen Sie ein vergleichbares Produkt zu kaufen.
      </p>
      <a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Zurück zur Startseite</a>
   <% } else if (exception !=null) { %>
      <p>
         Beim Erstellen Ihrer Bestellung ist ein Fehler aufgetreten. Bitte versuchen Sie es nochmals.
      </p>
      <a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Zurück zur Startseite</a>
   <% } %>
<% } %>

',10510,'12514_CAV');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID, URI) VALUES (10520, 10500, 0, 'External payment gateway', 'External payment gateway','dynocontent', 'SHOP10-10520', 'SHOP10_resultpage_message');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12520,'CONTENT_BODY_en_1','
<h2>Payment result</h2>
<%
def _status = binding.hasVariable(''status'') ? status : (binding.hasVariable(''hint'') ? hint : "");
if (_status.equals("ok")) { %>
	<p>Order successfully placed</p>
	<a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Continue shopping</a>
  <% if (binding.hasVariable(''order'') && order.customer != null) { %>
  	<a href="/yes-shop/orders" class="btn btn-primary" rel="nofollow">Check order status</a>
  <% } %>
<% } else if (_status.equals("cancel")) { %>
	<p>Order was cancelled. This maybe due to payment failure or insufficient stock</p>
	<a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Continue shopping</a>
<% } else { %>
	<p>Errors in payment</p>
	<a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Back to Homepage</a>
<% } %>

',10520,'12520_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12521,'CONTENT_BODY_ru_1','
<h2>Результат оплаты</h2>
<%
def _status = binding.hasVariable(''status'') ? status : (binding.hasVariable(''hint'') ? hint : "");
if (_status.equals("ok")) { %>
	<p>Заказ успешно оформлен</p>
	<a href="/yes-shop" class="btn btn-primary2" rel="bookmark">За новыми покупками</a>
  <% if (binding.hasVariable(''order'') && order.customer != null) { %>
  	<a href="/yes-shop/orders" class="btn btn-primary" rel="nofollow">Проверить статус заказа</a>
  <% } %>
<% } else if (_status.equals("cancel")) { %>
	<p>Заказ отменен. Возможная причина - это ошибка при оплате, либо недостаточное кол-во товара на складе</p>
	<a href="/yes-shop" class="btn btn-primary2" rel="bookmark">За новыми покупками</a>
<% } else { %>
	<p>Ошибки при оплате</p>
	<a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Перейти на главную</a>
<% } %>

',10520,'12521_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12522,'CONTENT_BODY_uk_1','
<h2>Результат оплати</h2>
<%
def _status = binding.hasVariable(''status'') ? status : (binding.hasVariable(''hint'') ? hint : "");
if (_status.equals("ok")) { %>
	<p>Замовлення успішно оформлене</p>
	<a href="/yes-shop" class="btn btn-primary2" rel="bookmark">За новими покупками</a>
  <% if (binding.hasVariable(''order'') && order.customer != null) { %>
	  <a href="/yes-shop/orders" class="btn btn-primary" rel="nofollow">Перевірити статус замовлення</a>
	<% } %>
<% } else if (_status.equals("cancel")) { %>
	<p>Замовлення скасовано. Можлива причина - це помилка при оплаті, або недостатня кількість товару на складі</p>
	<a href="/yes-shop" class="btn btn-primary2" rel="bookmark">За новими покупками</a>
<% } else { %>
	<p>Помилка при оплаті</p>
	<a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Повернутися на головну</a>
<% } %>

',10520,'12522_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12523,'CONTENT_BODY_de_1','
<h2>Resultat des Zahlungsvorgangs</h2>
<%
def _status = binding.hasVariable(''status'') ? status : (binding.hasVariable(''hint'') ? hint : "");
if (_status.equals("ok")) { %>
	<p>Bestellung erfolgreich getätigt</p>
	<a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Weiter Einkaufen / Zur Startseite</a>
  <% if (binding.hasVariable(''order'') && order.customer != null) { %>
   	<a href="/yes-shop/orders" class="btn btn-primary" rel="nofollow">Status der Bestellung verfolgen</a>
  <% } %>
<% } else if (_status.equals("cancel")) { %>
	<p>Die Bestellung wurde annuliert oder die Artikel ist nicht mehr an Lager. Das kann der Grund für den Abbruch der Zahlung sein</p>
	<a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Weiter Einkaufen / Zur Startseite</a>
<% } else { %>
	<p>Fehler bei der Zahlung</p>
	<a href="/yes-shop" class="btn btn-primary2" rel="bookmark">Zurück zur Startseite</a>
<% } %>

',10520,'12523_CAV');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10001, 10400, 0, 'License', 'License Page','content', 'License','license');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12010,'CONTENT_BODY_en_1','<pre>Copyright 2009 Denys Pavlov, Igor Azarnyi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
</pre>',10001,'12010_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12011,'CONTENT_BODY_ru_1','<pre>Copyright 2009 Denys Pavlov, Igor Azarnyi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
</pre>',10001,'12011_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12014,'CONTENT_BODY_uk_1','<pre>Copyright 2009 Denys Pavlov, Igor Azarnyi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
</pre>',10001,'12014_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12017,'CONTENT_BODY_de_1','<pre>Copyright 2009 Igor Azarnyi, Denys Pavlov

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        <a href="http://www.apache.org/licenses/LICENSE-2.0">http://www.apache.org/licenses/LICENSE-2.0</a>

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
</pre>',10001,'12017_CAV');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10002, 10400, 0, 'Sitemap', 'Dynamic Content Site Map Page','dynocontent', 'Sitemap','sitemap');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12012,'CONTENT_BODY_en_1','
<p>This page demonstrates dynamic content features</p>

<p>Links:
<ul>
  <li><a href="${contentURL(''license'')}">License page (content link)</a></li>
  <li><a href="${categoryURL(''notebooks'')}">Notebooks (category link)</a></li>
  <li><a href="${URL('''')}">Home (plain link)</a></li>
</ul>
</p>

<p>Dynamic variable: ${datetime}</p>

<p>Dynamic include:</p>

${include(''license'')}


',10002,'12012_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12013,'CONTENT_BODY_ru_1','
<p>Данная страница демонстрирует использование динамического контента</p>

<p>Ссылки:
<ul>
  <li><a href="${contentURL(''license'')}">Страница Лицензии (ссылка на контент)</a></li>
  <li><a href="${categoryURL(''notebooks'')}">Категория "Ноутбуки" (ссылка на категорию)</a></li>
  <li><a href="${URL('''')}">Домашняя страничка (простая ссылка)</a></li>
</ul>
</p>

<p>Динамическая переменная: ${datetime}</p>

<p>Динамическая вставка суб-контента:</p>

${include(''license'')}

',10002,'12013_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12015,'CONTENT_BODY_uk_1','
<p>Дана сторінка демонструє використання динамічного контенту</p>

<p>Посилання:
<ul>
  <li><a href="${contentURL(''license'')}">Сторінка Ліцензії (посилання на контент)</a></li>
  <li><a href="${categoryURL(''notebooks'')}">Категорія "Ноутбуки" (посилання на категорію)</a></li>
  <li><a href="${URL('''')}">Домашня сторінка (просте посилання)</a></li>
</ul>
</p>

<p>Динамічна змінна: ${datetime}</p>

<p>Динамічна вставка суб-контенту:</p>

${include(''license'')}

',10002,'12015_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12016,'CONTENT_BODY_de_1','
<p>Diese Seite zeigt dynamische Inhalte Features</p>

<p>Links:
<ul>
  <li><a href="${contentURL(''license'')}">Seite Lizenz (Inhalt Link)</a></li>
  <li><a href="${categoryURL(''notebooks'')}">Notebooks (Kategorie Link) </a></li>
  <li><a href="${URL('''')}">Start (Normal Link)</a></li>
</ul>
</p>

<p>Dynamische Variable: ${datetime}</p>

<p>Dynamische beinhalten:</p>

${include(''license'')}

',10002,'12016_CAV');
INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10003, 10201, 0, 'header_include', 'Header include for SHOP10','include', 'SHOP10_header_include','SHOP10_header_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12022,'CONTENT_BODY_en_1','
    <meta name="google-site-verification" content="rHZLga_ppoOy7iVYFQgRVDZOLa7fuT7cGs2t8TY4m6c" />
',10003,'12022_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12023,'CONTENT_BODY_ru_1','
    <meta name="google-site-verification" content="rHZLga_ppoOy7iVYFQgRVDZOLa7fuT7cGs2t8TY4m6c" />
',10003,'12023_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12025,'CONTENT_BODY_uk_1','
    <meta name="google-site-verification" content="rHZLga_ppoOy7iVYFQgRVDZOLa7fuT7cGs2t8TY4m6c" />
',10003,'12025_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12026,'CONTENT_BODY_de_1','
    <meta name="google-site-verification" content="rHZLga_ppoOy7iVYFQgRVDZOLa7fuT7cGs2t8TY4m6c" />
',10003,'12026_CAV');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10004, 10202, 0, 'footer_include', 'Footer include for SHOP10','include', 'SHOP10_footer_include','SHOP10_footer_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12032,'CONTENT_BODY_en_1','
    <script type="text/javascript">

        var _gaq = _gaq || [];
        _gaq.push([''_setAccount'', ''UA-4983157-12'']);
        _gaq.push([''_setDomainName'', ''yes-cart.org'']);
        _gaq.push([''_trackPageview'']);

        (function() {
            var ga = document.createElement(''script''); ga.type = ''text/javascript''; ga.async = true;
            ga.src = (''https:'' == document.location.protocol ? ''https://ssl'' : ''http://www'') + ''.google-analytics.com/ga.js'';
            var s = document.getElementsByTagName(''script'')[0]; s.parentNode.insertBefore(ga, s);
        })();

    </script>',10004,'12032_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12033,'CONTENT_BODY_ru_1','
    <script type="text/javascript">

        var _gaq = _gaq || [];
        _gaq.push([''_setAccount'', ''UA-4983157-12'']);
        _gaq.push([''_setDomainName'', ''yes-cart.org'']);
        _gaq.push([''_trackPageview'']);

        (function() {
            var ga = document.createElement(''script''); ga.type = ''text/javascript''; ga.async = true;
            ga.src = (''https:'' == document.location.protocol ? ''https://ssl'' : ''http://www'') + ''.google-analytics.com/ga.js'';
            var s = document.getElementsByTagName(''script'')[0]; s.parentNode.insertBefore(ga, s);
        })();

    </script>',10004,'12033_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12035,'CONTENT_BODY_uk_1','
    <script type="text/javascript">

        var _gaq = _gaq || [];
        _gaq.push([''_setAccount'', ''UA-4983157-12'']);
        _gaq.push([''_setDomainName'', ''yes-cart.org'']);
        _gaq.push([''_trackPageview'']);

        (function() {
            var ga = document.createElement(''script''); ga.type = ''text/javascript''; ga.async = true;
            ga.src = (''https:'' == document.location.protocol ? ''https://ssl'' : ''http://www'') + ''.google-analytics.com/ga.js'';
            var s = document.getElementsByTagName(''script'')[0]; s.parentNode.insertBefore(ga, s);
        })();

    </script>',10004,'12035_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12036,'CONTENT_BODY_de_1','
    <script type="text/javascript">

        var _gaq = _gaq || [];
        _gaq.push([''_setAccount'', ''UA-4983157-12'']);
        _gaq.push([''_setDomainName'', ''yes-cart.org'']);
        _gaq.push([''_trackPageview'']);

        (function() {
            var ga = document.createElement(''script''); ga.type = ''text/javascript''; ga.async = true;
            ga.src = (''https:'' == document.location.protocol ? ''https://ssl'' : ''http://www'') + ''.google-analytics.com/ga.js'';
            var s = document.getElementsByTagName(''script'')[0]; s.parentNode.insertBefore(ga, s);
        })();

    </script>',10004,'12036_CAV');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10005, 10201, 0, 'header_search_include', 'Header search include for SHOP10','include', 'SHOP10_header_search_include','SHOP10_header_search_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12042,'CONTENT_BODY_en_1','
<span class="glyphicon glyphicon-envelope themecolor"></span>
<a href="http://www.yes-cart.org/#contact" target="_blank">Contact form</a>
',10005,'12042_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12043,'CONTENT_BODY_ru_1','
<span class="glyphicon glyphicon-envelope themecolor"></span>
<a href="http://www.yes-cart.org/index-ru.html#contact" target="_blank">Контактная форма</a>
',10005,'12043_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12045,'CONTENT_BODY_uk_1','
<span class="glyphicon glyphicon-envelope themecolor"></span>
<a href="http://www.yes-cart.org/index-uk.html#contact" target="_blank">Контактна форма</a>
',10005,'12045_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12046,'CONTENT_BODY_de_1','
<span class="glyphicon glyphicon-envelope themecolor"></span>
<a href="http://www.yes-cart.org/#contact" target="_blank">Kontaktformular</a>
',10005,'12046_CAV');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10006, 10250, 0, 'shopping_cart_checkout_include', 'Shopping cart checkout include for SHOP10','include', 'SHOP10_s_cart_checkout_include','SHOP10_shopping_cart_checkout_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12052,'CONTENT_BODY_en_1','
<div class="section-title"><h2>Shopping cart</h2></div>
',10006,'12052_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12053,'CONTENT_BODY_ru_1','
<div class="section-title"><h2>Корзина</h2></div>
',10006,'12053_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12055,'CONTENT_BODY_uk_1','
<div class="section-title"><h2>Кошик</h2></div>
',10006,'12055_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12056,'CONTENT_BODY_de_1','
<div class="section-title"><h2>Warenkorb</h2></div>
',10006,'12056_CAV');


INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10007, 10250, 0, 'shopping_cart_coupons_include', 'Shopping cart coupons include for SHOP10','include', 'SHOP10_shopping_cart_coupons_include','SHOP10_shopping_cart_coupons_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12062,'CONTENT_BODY_en_1','
<div class="section-title"><h2>Add Coupons</h2></div>
',10007,'12062_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12063,'CONTENT_BODY_ru_1','
<div class="section-title"><h2>Добавить промо-код</h2></div>
',10007,'12063_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12065,'CONTENT_BODY_uk_1','
<div class="section-title"><h2>Додати промо-код</h2></div>
',10007,'12065_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12066,'CONTENT_BODY_de_1','
<div class="section-title"><h2>Gutschein zufügen</h2></div>
',10007,'12066_CAV');



INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10008, 10250, 0, 'shopping_cart_message_include', 'Shopping cart message include for SHOP10','include', 'SHOP10_shopping_cart_message_include','SHOP10_shopping_cart_message_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12072,'CONTENT_BODY_en_1','
<div class="section-title"><h2>Order Message</h2></div>
<div class="clearfix"><% if (shoppingCart.orderMessage != null) { %>
Current message: "${shoppingCart.orderMessage}"<% } else { %>Add order message<% } %></div>
',10008,'12072_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12073,'CONTENT_BODY_ru_1','
<div class="section-title"><h2>Сообщение</h2></div>
<div class="clearfix"><% if (shoppingCart.orderMessage != null) { %>
Сообщение в заказе: "${shoppingCart.orderMessage}"<% } else { %>Добавьте сообщение<% } %></div>
',10008,'12073_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12075,'CONTENT_BODY_uk_1','
<div class="section-title"><h2>Повідомлення</h2></div>
<div class="clearfix"><% if (shoppingCart.orderMessage != null) { %>
Повідомлення у замовленні: "${shoppingCart.orderMessage}"<% } else { %>Додайте повідомлення<% } %></div>
',10008,'12075_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12076,'CONTENT_BODY_de_1','
<div class="section-title"><h2>Info zur Bestellung</h2></div>
<div class="clearfix"><% if (shoppingCart.orderMessage != null) { %>
aktuelle Meldung: "${shoppingCart.orderMessage}"<% } else { %>Bitte Text einfügen<% } %></div>
',10008,'12076_CAV');


INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10009, 10201, 0, 'cookie_policy_include', 'Cookie policy message include for SHOP10','include', 'SHOP10_cookie_policy_include','SHOP10_cookie_policy_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12080,'CONTENT_BODY_en_1','
<small><b>Cookie Policy</b><br>
 We may store information about you using cookies (files which are sent by us to your computer or other access device) which
 we can access when you visit our site in future. We do this to enhance user experience. If you want to delete any cookies that
 are already on your computer, please refer to the instructions for your file management software to locate the file or directory
 that stores cookies. Our cookies will have the file names JSESSIONID, yc and yccookiepolicy. Information on deleting or controlling
 cookies is available at <a href="http://www.AboutCookies.org" target="_blank">www.AboutCookies.org</a>. Please note that by
 deleting our cookies or disabling future cookies you may not be able to access certain areas or features of our site.</small>
',10009,'12080_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12081,'CONTENT_BODY_ru_1','
<small><b>Cookie Policy</b><br>
 We may store information about you using cookies (files which are sent by us to your computer or other access device) which
 we can access when you visit our site in future. We do this to enhance user experience. If you want to delete any cookies that
 are already on your computer, please refer to the instructions for your file management software to locate the file or directory
 that stores cookies. Our cookies will have the file names JSESSIONID, yc and yccookiepolicy. Information on deleting or controlling
 cookies is available at <a href="http://www.AboutCookies.org" target="_blank">www.AboutCookies.org</a>. Please note that by
 deleting our cookies or disabling future cookies you may not be able to access certain areas or features of our site.</small>
',10009,'12081_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12082,'CONTENT_BODY_uk_1','
<small><b>Cookie Policy</b><br>
 We may store information about you using cookies (files which are sent by us to your computer or other access device) which
 we can access when you visit our site in future. We do this to enhance user experience. If you want to delete any cookies that
 are already on your computer, please refer to the instructions for your file management software to locate the file or directory
 that stores cookies. Our cookies will have the file names JSESSIONID, yc and yccookiepolicy. Information on deleting or controlling
 cookies is available at <a href="http://www.AboutCookies.org" target="_blank">www.AboutCookies.org</a>. Please note that by
 deleting our cookies or disabling future cookies you may not be able to access certain areas or features of our site.</small>
',10009,'12082_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12083,'CONTENT_BODY_de_1','
<small><b>Cookie Richtlinien</b><br>
 We may store information about you using cookies (files which are sent by us to your computer or other access device) which
 we can access when you visit our site in future. We do this to enhance user experience. If you want to delete any cookies that
 are already on your computer, please refer to the instructions for your file management software to locate the file or directory
 that stores cookies. Our cookies will have the file names JSESSIONID, yc and yccookiepolicy. Information on deleting or controlling
 cookies is available at <a href="http://www.AboutCookies.org" target="_blank">www.AboutCookies.org</a>. Please note that by
 deleting our cookies or disabling future cookies you may not be able to access certain areas or features of our site.</small>
',10009,'12083_CAV');

INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10010, 10251, 0, 'profile_wishlist_owner_include', 'Profile wishlist owner include for SHOP10','include', 'SHOP10_profile_wishlist_o_include','SHOP10_profile_wishlist_owner_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12090,'CONTENT_BODY_en_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<div class="col-xs-12"><ul class="wl-tag-cloud jsWishlistTagCloud"></ul></div>
</div>
',10010,'12090_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12091,'CONTENT_BODY_ru_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<div class="col-xs-12"><ul class="wl-tag-cloud jsWishlistTagCloud"></ul></div>
</div>
',10010,'12091_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12092,'CONTENT_BODY_uk_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<div class="col-xs-12"><ul class="wl-tag-cloud jsWishlistTagCloud"></ul></div>
</div>
',10010,'12092_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12093,'CONTENT_BODY_de_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<div class="col-xs-12"><ul class="wl-tag-cloud jsWishlistTagCloud"></ul></div>
</div>
',10010,'12093_CAV');


INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10011, 10251, 0, 'profile_wishlist_viewer_include', 'Profile wishlist viewer include for SHOP10','include', 'SHOP10_profile_wishlist_v_include','SHOP10_profile_wishlist_viewer_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12095,'CONTENT_BODY_en_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<p>You are viewing a shared wish list</p>
</div>
',10011,'12095_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12096,'CONTENT_BODY_ru_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<p>Вы просматриваете открытый список пожеланий</p>
</div>
',10011,'12096_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12097,'CONTENT_BODY_uk_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<p>Ви переглядаєте відкритий список побажань</p>
</div>
',10011,'12097_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12098,'CONTENT_BODY_de_1','
<div class="col-xs-12 no-padding">
<h2 class="profile-title">&nbsp;</h2>
<p>Sie sehen einen gemeinsamen Wunschzettel</p>
</div>
',10011,'12098_CAV');


INSERT INTO TCATEGORY(CATEGORY_ID, PARENT_ID, RANK, NAME, DESCRIPTION, UITEMPLATE, GUID,URI) VALUES (10012, 10202, 0, 'newsletter_newsletterform_content_include', 'Newsletter form include for SHOP10','include', 'SHOP10_newsletter_newsletter_include','SHOP10_newsletter_newsletterform_content_include');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12102,'CONTENT_BODY_en_1','
<div><h3>Newsletter</h3><div>Sign up for newsletter</div></div>
',10012,'12102_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12103,'CONTENT_BODY_ru_1','
<div><h3>Рассылка новостей</h3><div>Подпишитесь на рассылку новостей</div></div>
',10012,'12103_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12104,'CONTENT_BODY_uk_1','
<div><h3>Розсилка новин</h3><div>Підписатися на розсилку новин</div></div>
',10012,'12104_CAV');
INSERT INTO TCATEGORYATTRVALUE(ATTRVALUE_ID, CODE,VAL, CATEGORY_ID, GUID) VALUES (12105,'CONTENT_BODY_de_1','
<div><h3>Newsletter</h3><div>Anmeldung für Newsletter</div></div>
',10012,'12105_CAV');


INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (1, 'ROLE_SMADMIN',         'ROLE_SMADMIN', 'System admin');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (2, 'ROLE_SMSHOPADMIN',     'ROLE_SMSHOPADMIN', 'Shop manager');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (3, 'ROLE_SMWAREHOUSEADMIN','ROLE_SMWAREHOUSEADMIN', 'Inventory manager');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (4, 'ROLE_SMCALLCENTER',    'ROLE_SMCALLCENTER', 'Call centre operator');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (5, 'ROLE_SMCONTENTADMIN',  'ROLE_SMCONTENTADMIN', 'Content manager');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (6, 'ROLE_SMMARKETINGADMIN', 'ROLE_SMMARKETINGADMIN', 'Marketing manager');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (7, 'ROLE_SMSHIPPINGADMIN',  'ROLE_SMSHIPPINGADMIN', 'Shipping manager');
INSERT INTO TROLE (ROLE_ID, GUID, CODE, DESCRIPTION) VALUES (999, 'ROLE_LICENSEAGREED',  'ROLE_LICENSEAGREED', 'User agreed to license');

-- default admin password 1234567
INSERT INTO TMANAGER (GUID, MANAGER_ID, EMAIL, FIRSTNAME, LASTNAME, PASSWORD, ENABLED) VALUES ('YCADMIN', 1, 'admin@yes-cart.com', 'Yes', 'Admin', 'd89c77010dedf89c10d1293bd02b53c7', 1);

INSERT INTO TMANAGERROLE (MANAGERROLE_ID, EMAIL, CODE, GUID) VALUES (1, 'admin@yes-cart.com', 'ROLE_SMADMIN', 'YCADMIN_ROLE');


-- Default Data Import Groups and Descriptors


INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1000, 'YC DEMO: Initial Data', 'IMPORT', '
shopcurrencies-demo.xml,
countrynames-demo.xml,
statenames-demo.xml,
carriernames-demo.xml,
carriershopnames-demo.xml,
carrierslanames-demo.xml,
promotionnames-demo.xml,
promotioncouponnames-demo.xml,
skuprices-demo.xml,
taxnames-demo.xml,
taxconfignames-demo.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1001, 'YC DEMO: IceCat Catalog', 'IMPORT', '
brandnames-demo.xml,
attributenames-demo.xml,
attributegroupnames-demo.xml,
producttypenames-demo.xml,
categorynames-demo.xml,
shopcategory-demo.xml,
productypeattributeviewgroupnames-demo.xml,
producttypeattrnames-demo.xml,
productnames-demo.xml,
productsku-demo.xml,
productsattributes-demo.xml,
productskuattributes-demo.xml,
skuinventory-demo.xml,
skuprices-demo.xml,
productcategorynames-demo.xml,
productaccessories-demo.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1002, 'YC DEMO: Product images (IceCat)', 'IMPORT', 'productimages.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1003, 'Catalog import (Categories and attribute definitions)', 'IMPORT', '
brandnames.xml,
attributenames.xml,
attributegroupnames.xml,
producttypenames.xml,
categorynames.xml,
shopcategory.xml,
productypeattributeviewgroupnames.xml,
producttypeattrnames.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1004, 'Product import (Products, SKUs and attribute values)', 'IMPORT', '
productandcategorynames.xml,
productnames.xml,
productcategorynames.delete.xml,
productcategorynames.xml,
productsku.xml,
productsattributes.delete.all.xml,
productsattributes.delete.xml,
productsattributes.xml,
productskuattributes.delete.all.xml,
productskuattributes.delete.xml,
productskuattributes.xml,
productaccessories.delete.xml,
productaccessories.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1005, 'Inventory import', 'IMPORT', 'skuinventory.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1006, 'Prices, Promotions and Taxes import', 'IMPORT', '
skuprices.xml,
promotionnames.xml,
promotioncouponnames.xml,
taxnames.xml,
taxconfignames.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1007, 'Content import', 'IMPORT', 'contentnames.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1008, 'Images: Products and SKU', 'IMPORT', 'productimages.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1009, 'Images: Category and Content', 'IMPORT', 'categoryimages.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1010, 'Images: Brands', 'IMPORT', 'brandimages.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1011, 'Images: Shop', 'IMPORT', 'shopimages.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1012, 'Location import', 'IMPORT', 'countrynames.xml,statenames.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1013, 'Carriers and SLA import', 'IMPORT', 'carriernames.xml,carriershopnames.xml,carrierslanames.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (1014, 'Shops and Warehouses import', 'IMPORT', 'shop.xml,warehouse.xml');


INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1000, 'attributegroupnames-demo.xml', 'WEBINF_XML', 'attributegroupnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1001, 'attributegroupnames.xml', 'WEBINF_XML', 'attributegroupnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1002, 'attributenames-demo.xml', 'WEBINF_XML', 'attributenames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1003, 'attributenames.xml', 'WEBINF_XML', 'attributenames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1004, 'brandimages.xml', 'WEBINF_XML', 'brandimages.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1005, 'brandnames-demo.xml', 'WEBINF_XML', 'brandnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1006, 'brandnames.xml', 'WEBINF_XML', 'brandnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1007, 'carriernames-demo.xml', 'WEBINF_XML', 'carriernames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1008, 'carriernames.xml', 'WEBINF_XML', 'carriernames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1009, 'carriershopnames-demo.xml', 'WEBINF_XML', 'carriershopnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1010, 'carriershopnames.xml', 'WEBINF_XML', 'carriershopnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1011, 'carrierslanames-demo.xml', 'WEBINF_XML', 'carrierslanames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1012, 'carrierslanames.xml', 'WEBINF_XML', 'carrierslanames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1013, 'categoryimages.xml', 'WEBINF_XML', 'categoryimages.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1014, 'categorynames-demo.xml', 'WEBINF_XML', 'categorynames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1015, 'categorynames.xml', 'WEBINF_XML', 'categorynames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1016, 'contentnames.xml', 'WEBINF_XML', 'contentnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1017, 'countrynames-demo.xml', 'WEBINF_XML', 'countrynames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1018, 'countrynames.xml', 'WEBINF_XML', 'countrynames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1019, 'productaccessories-demo.xml', 'WEBINF_XML', 'productaccessories-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1020, 'productaccessories.delete.xml', 'WEBINF_XML', 'productaccessories.delete.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1021, 'productaccessories.xml', 'WEBINF_XML', 'productaccessories.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1022, 'productandcategorynames.xml', 'WEBINF_XML', 'productandcategorynames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1023, 'productcategorynames-demo.xml', 'WEBINF_XML', 'productcategorynames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1024, 'productcategorynames.delete.xml', 'WEBINF_XML', 'productcategorynames.delete.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1025, 'productcategorynames.xml', 'WEBINF_XML', 'productcategorynames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1026, 'productimages.xml', 'WEBINF_XML', 'productimages.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1027, 'productnames-demo.xml', 'WEBINF_XML', 'productnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1028, 'productnames.xml', 'WEBINF_XML', 'productnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1029, 'productsattributes-demo.xml', 'WEBINF_XML', 'productsattributes-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1030, 'productsattributes.delete.all.xml', 'WEBINF_XML', 'productsattributes.delete.all.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1031, 'productsattributes.delete.xml', 'WEBINF_XML', 'productsattributes.delete.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1032, 'productsattributes.xml', 'WEBINF_XML', 'productsattributes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1033, 'productsku-demo.xml', 'WEBINF_XML', 'productsku-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1034, 'productsku.xml', 'WEBINF_XML', 'productsku.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1035, 'productskuattributes-demo.xml', 'WEBINF_XML', 'productskuattributes-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1036, 'productskuattributes.delete.all.xml', 'WEBINF_XML', 'productskuattributes.delete.all.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1037, 'productskuattributes.delete.xml', 'WEBINF_XML', 'productskuattributes.delete.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1038, 'productskuattributes.xml', 'WEBINF_XML', 'productskuattributes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1039, 'producttypeattrnames-demo.xml', 'WEBINF_XML', 'producttypeattrnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1040, 'producttypeattrnames.xml', 'WEBINF_XML', 'producttypeattrnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1041, 'producttypenames-demo.xml', 'WEBINF_XML', 'producttypenames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1042, 'producttypenames.xml', 'WEBINF_XML', 'producttypenames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1043, 'productypeattributeviewgroupnames-demo.xml', 'WEBINF_XML', 'productypeattributeviewgroupnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1044, 'productypeattributeviewgroupnames.xml', 'WEBINF_XML', 'productypeattributeviewgroupnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1045, 'promotioncouponnames-demo.xml', 'WEBINF_XML', 'promotioncouponnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1046, 'promotioncouponnames.xml', 'WEBINF_XML', 'promotioncouponnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1047, 'promotionnames-demo.xml', 'WEBINF_XML', 'promotionnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1048, 'promotionnames.xml', 'WEBINF_XML', 'promotionnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1049, 'shop.xml', 'WEBINF_XML', 'shop.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1050, 'shopcategory-demo.xml', 'WEBINF_XML', 'shopcategory-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1051, 'shopcategory.xml', 'WEBINF_XML', 'shopcategory.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1052, 'shopcurrencies-demo.xml', 'WEBINF_XML', 'shopcurrencies-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1053, 'shopimages.xml', 'WEBINF_XML', 'shopimages.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1054, 'skuinventory-demo.xml', 'WEBINF_XML', 'skuinventory-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1055, 'skuinventory.xml', 'WEBINF_XML', 'skuinventory.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1056, 'skuprices-demo.xml', 'WEBINF_XML', 'skuprices-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1057, 'skuprices.xml', 'WEBINF_XML', 'skuprices.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1058, 'statenames-demo.xml', 'WEBINF_XML', 'statenames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1059, 'statenames.xml', 'WEBINF_XML', 'statenames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1060, 'taxconfignames-demo.xml', 'WEBINF_XML', 'taxconfignames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1061, 'taxconfignames.xml', 'WEBINF_XML', 'taxconfignames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1062, 'taxnames-demo.xml', 'WEBINF_XML', 'taxnames-demo.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1063, 'taxnames.xml', 'WEBINF_XML', 'taxnames.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (1064, 'warehouse.xml', 'WEBINF_XML', 'warehouse.xml');


INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2000, 'Export Customer Addresses', 'EXPORT', 'customeraddresses.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2001, 'Export Customer Profiles', 'EXPORT', 'customers.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2002, 'Export Customer Orders', 'EXPORT', 'orders.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2003, 'Export Customer Payments', 'EXPORT', 'payments.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2004, 'Export Images: Products and SKU', 'EXPORT', 'productimagesout.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2005, 'Export Images: Category and Content', 'EXPORT', 'categoryimagesout.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2006, 'Export Attribute Definitions', 'EXPORT', 'attributes.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2007, 'Export Brands', 'EXPORT', 'brands.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2008, 'Export Product Types (Definitions)', 'EXPORT', 'producttypes.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2009, 'Export Product Types (Attributes)', 'EXPORT', 'producttypeattributes.xml');
INSERT INTO TDATAGROUP (DATAGROUP_ID, NAME, TYPE, DESCRIPTORS) VALUES (2010, 'Export Tax Configurations', 'EXPORT', 'taxconfigs.xml');


INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2000, 'customeraddresses.xml', 'WEBINF_XML', 'customeraddresses.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2001, 'customers.xml', 'WEBINF_XML', 'customers.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2002, 'orders.xml', 'WEBINF_XML', 'orders.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2003, 'payments.xml', 'WEBINF_XML', 'payments.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2004, 'productimagesout.xml', 'WEBINF_XML', 'productimagesout.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2005, 'categoryimagesout.xml', 'WEBINF_XML', 'categoryimagesout.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2006, 'attributes.xml', 'WEBINF_XML', 'attributes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2007, 'brands.xml', 'WEBINF_XML', 'brands.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2008, 'producttypes.xml', 'WEBINF_XML', 'producttypes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2009, 'producttypeattributes.xml', 'WEBINF_XML', 'producttypeattributes.xml');
INSERT INTO TDATADESCRIPTOR (DATADESCRIPTOR_ID, NAME, TYPE, VALUE) VALUES (2010, 'taxconfigs.xml', 'WEBINF_XML', 'taxconfigs.xml');


INSERT INTO HIBERNATE_UNIQUE_KEYS VALUES (100000);

COMMIT;