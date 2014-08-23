    create table TADDRESS (
        ADDRESS_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CITY varchar(128) not null,
        POSTCODE varchar(16),
        ADDRLINE1 varchar(255) not null,
        ADDRLINE2 varchar(255),
        ADDRESS_TYPE varchar(1) not null,
        COUNTRY_CODE varchar(64) not null,
        STATE_CODE varchar(64),
        PHONES varchar(255),
        FIRSTNAME varchar(128) not null,
        LASTNAME varchar(128) not null,
        MIDDLENAME varchar(128),
        DEFAULT_ADDR bit,
        CUSTOMER_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ADDRESS_ID)
    ) comment='Locations have the same address struct in the same region' ;

    create table TASSOCIATION (
        ASSOCIATION_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null,
        NAME varchar(255) not null,
        DESCRIPTION longtext,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ASSOCIATION_ID)
    ) comment='Cross/up/accesories/who buy also buy sell' ;

    create table TATTRIBUTE (
        ATTRIBUTE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null  unique,
        MANDATORY bit not null comment 'Attribute must have the default value if mandatory flag is true',
        ALLOWDUPLICATE bit default 0 not null comment 'Allow to duplicate attr values. Example color - red and black',
        ALLOWFAILOVER bit default 0 not null comment 'Allow search attribute value in parent object',
        VAL longtext comment 'Default value , shall be force inserted for new attributes, if attribute is mandaroty',
        REXP longtext comment 'Regular expression to validate user input on UI.',
        V_FAILED_MSG longtext comment 'Message if type, mandatory or re validation failed',
        RANK integer default 500 comment 'What order in dynamic form on UI',
        CHOICES longtext comment 'Comma separated list of [key-]value. Or service url.',
        NAME varchar(255) not null,
        DISPLAYNAME longtext,
        DESCRIPTION longtext,
        ETYPE_ID bigint not null,
        ATTRIBUTEGROUP_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRIBUTE_ID)
    ) comment='Particular attribute' ;

    create table TATTRIBUTEGROUP (
        ATTRIBUTEGROUP_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null,
        NAME varchar(64) not null,
        DESCRIPTION longtext,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRIBUTEGROUP_ID)
    ) comment='Group attributes: prod,sku,cat,shop,customer,brand etc.' ;

    create table TBRAND (
        BRAND_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        NAME varchar(255) not null,
        DESCRIPTION longtext,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (BRAND_ID)
    ) comment='Product brand / manafacturer' ;

    create table TBRANDATTRVALUE (
        ATTRVALUE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        BRAND_ID bigint not null,
        VAL longtext,
        DISPLAYVAL longtext,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    ) ;

    create table TCARRIER (
        CARRIER_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        NAME varchar(255) not null,
        DISPLAYNAME longtext,
        DESCRIPTION longtext,
        DISPLAYDESCRIPTION longtext,
        WORLDWIDE bit,
        COUNTRY bit,
        STATE bit,
        LOCAL bit,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CARRIER_ID)
    ) comment='Shipment' ;

    create table TCARRIERSLA (
        CARRIERSLA_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        NAME varchar(255) not null,
        DISPLAYNAME longtext,
        DESCRIPTION longtext,
        DISPLAYDESCRIPTION longtext,
        CURRENCY varchar(3) not null,
        MAX_DAYS integer,
        SLA_TYPE varchar(1) not null,
        PRICE decimal(19,2),
        PER_CENT decimal(19,2),
        SCRIPT longtext,
        PRICE_NOTLESS decimal(19,2),
        PERCENT_NOTLESS decimal(19,2),
        COST_NOTLESS decimal(19,2),
        CARRIER_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CARRIERSLA_ID)
    ) comment='Shipment service layer agreement. Days, distances , etc.' ;

    create table TCATEGORY (
        CATEGORY_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        PARENT_ID bigint,
        RANK integer comment 'What the default order of child category in parent ?',
        PRODUCTTYPE_ID bigint comment 'Default product type in category, can be null if category contains different product types',
        NAME varchar(255) not null,
        DISPLAYNAME longtext,
        DESCRIPTION longtext,
        UITEMPLATE varchar(255) comment 'point to a products in list template. Can be used parent template if it not set. In case of no templates at all, store product in list template will be used',
        AVAILABLEFROM datetime,
        AVAILABLETO datetime,
        URI varchar(255),
        TITLE varchar(255),
        METAKEYWORDS varchar(255),
        METADESCRIPTION varchar(255),
        DISPLAY_TITLE longtext,
        DISPLAY_METAKEYWORDS longtext,
        DISPLAY_METADESCRIPTION longtext,
        NAV_BY_ATTR bit comment 'Filtered navigation by attribute values',
        NAV_BY_BRAND bit comment 'Filtered navigation by brand',
        NAV_BY_PRICE bit comment 'Filtered navigation by price range',
        NAV_BY_PRICE_TIERS longtext comment 'Optional price range configuration. Default shop price tiers configuration will used if empty',
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CATEGORY_ID)
    ) comment='Category , self related table.' ;

    create table TCATEGORYATTRVALUE (
        ATTRVALUE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        VAL longtext,
        DISPLAYVAL longtext,
        CATEGORY_ID bigint not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    ) ;

    create table TCOUNTRY (
        COUNTRY_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        COUNTRY_CODE varchar(2) not null,
        ISO_CODE varchar(3) not null,
        NAME varchar(64) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (COUNTRY_ID)
    ) comment='List of supported countries.' ;

    create table TCUSTOMER (
        CUSTOMER_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        EMAIL varchar(255) not null unique,
        FIRSTNAME varchar(128) not null,
        LASTNAME varchar(128) not null,
        MIDDLENAME varchar(128),
        PASSWORD varchar(255) not null,
        TAG varchar(255) comment 'Set of customer tags',
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMER_ID)
    ) ;

    create table TCUSTOMERATTRVALUE (
        ATTRVALUE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        VAL longtext,
        DISPLAYVAL longtext,
        CUSTOMER_ID bigint not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    ) ;

    create table TCUSTOMERORDER (
        CUSTOMERORDER_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        PG_LABEL varchar(255) comment 'Payment gateway label',
        ORDERNUM varchar(255) comment 'Depends from order num producer.',
        CART_GUID varchar(36) not null,
        CURRENCY varchar(3) not null,
        LOCALE varchar(5) not null,
        PRICE decimal(19,2) not null,
        LIST_PRICE numeric(19,2) not null,
        IS_PROMO_APPLIED bit not null default 0,
        APPLIED_PROMO varchar(255),
        MESSAGE varchar(255) comment 'Free text information per each order. Can be whatever',
        ORDERSTATUS varchar(64) not null,
        CUSTOMER_ID bigint,
        SHOP_ID bigint not null,
        BILLING_ADDRESS varchar(255) comment 'Address composed for billing',
        SHIPPING_ADDRESS varchar(255) comment 'Shipping address',
        MULTIPLE_SHIPMENT bit default 0 comment 'Wait for all skus in case of not all available.',
        ORDER_TIMESTAMP datetime not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMERORDER_ID)
    ) ;

    create table TCUSTOMERORDERDELIVERY (
        CUSTOMERORDERDELIVERY_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        DELIVERYNUM varchar(255) comment 'Order contract in case of splited orders (XXX-1,XXX-2, etc). Delivery contract XXX-2-DDD1,XXX-2-DDD2, XXX-2-DDD3, where XXX order num, DDD delivery num.',
        REF_NO varchar(255) comment 'External ref number',
        PRICE decimal(19,2) not null,
        LIST_PRICE decimal(19,2) not null,
        IS_PROMO_APPLIED bit not null default 0,
        APPLIED_PROMO varchar(255),
        DELIVERYSTATUS varchar(64) not null,
        CARRIERSLA_ID bigint,
        CUSTOMERORDER_ID bigint not null,
        DELIVERY_GROUP varchar(16) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMERORDERDELIVERY_ID)
    ) ;

    create table TCUSTOMERORDERDELIVERYDET (
        CUSTOMERORDERDELIVERYDET_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        QTY decimal(19,2) not null comment 'Quantity of SKU in this particular delivery',
        PRICE decimal(19,2) not null,
        SALE_PRICE decimal(19,2) not null,
        LIST_PRICE decimal(19,2) not null,
        IS_GIFT  bit not null default 0,
        IS_PROMO_APPLIED bit not null default 0,
        APPLIED_PROMO varchar(255),
        CODE varchar(255) not null,
        PRODUCTNAME longtext not null,
        CUSTOMERORDERDELIVERY_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMERORDERDELIVERYDET_ID)
    ) comment='Delivery details' ;

    create table TCUSTOMERORDERDET (
        CUSTOMERORDERDET_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        QTY decimal(19,2) not null,
        PRICE decimal(19,2) not null comment 'Price per one unit',
        SALE_PRICE decimal(19,2) not null,
        LIST_PRICE decimal(19,2) not null,
        IS_GIFT  bit not null default 0,
        IS_PROMO_APPLIED bit not null default 0,
        APPLIED_PROMO varchar(255),
        CODE varchar(255) not null,
        PRODUCTNAME longtext not null,
        CUSTOMERORDER_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMERORDERDET_ID)
    ) comment='Orders detail' ;

    create table TCUSTOMERSHOP (
        CUSTOMERSHOP_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CUSTOMER_ID bigint not null,
        SHOP_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMERSHOP_ID)
    ) ;

    create table TCUSTOMERWISHLIST (
        CUSTOMERWISHLIST_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        SKU_ID bigint not null,
        CUSTOMER_ID bigint not null,
        WL_TYPE varchar(1) default 'W' comment 'W-single, A-Available reminder, P-Price reminder, R-In promo reminder',
        VISIBILITY varchar(1) default 'P' comment 'P-private, S-shared',
        TAG varchar(255),
        QTY decimal(19,2) not null default 1,
        REGULAR_PRICE_WHEN_ADDED decimal(19,2) not null default 0,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (CUSTOMERWISHLIST_ID)
    ) ;

    create table TENSEMBLEOPT (
        ENSEMBLEOPT_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        QTY integer not null,
        PRODUCT_ID bigint not null,
        SKU_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ENSEMBLEOPT_ID)
    ) comment='Ensemble options - how many particular sku in ensemble' ;

    create table TETYPE (
        ETYPE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        JAVATYPE varchar(255) not null comment 'Shows the java type',
        BUSINESSTYPE varchar(255) comment 'Used mosty on UI. How to represent product, category, attributes, etc',
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ETYPE_ID)
    ) comment='Types, that will be used in system.' ;

    create table TMAILTEMPLATE (
        MAILTEMPLATE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null,
        FSPOINTER longtext not null comment 'Absolute path to folder with html template files and images.',
        NAME varchar(255) not null,
        DESCRIPTION varchar(255),
        MAILTEMPLATEGROUP_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (MAILTEMPLATE_ID)
    ) ;

    create table TMAILTEMPLATEGROUP (
        MAILTEMPLATEGROUP_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        NAME varchar(64) not null,
        DESCRIPTION longtext,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (MAILTEMPLATEGROUP_ID)
    ) ;

    create table TMANAGER (
        MANAGER_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        EMAIL varchar(255) not null unique,
        FIRSTNAME varchar(128) not null,
        LASTNAME varchar(128) not null,
        MIDDLENAME varchar(128),
        PASSWORD varchar(255) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (MANAGER_ID)
    ) comment='Hold managers' ;

    create table TMANAGERROLE (
        MANAGERROLE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        EMAIL varchar(255) not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (MANAGERROLE_ID)
    ) comment='Hold roles list' ;

    create table TPRODTYPEATTRVIEWGROUP (
        PRODTYPEATTRIBUTEGROUP_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        PRODUCTTYPE_ID bigint not null,
        ATTRCODELIST longtext,
        RANK integer comment 'What the default order of attribute group for prod type ?',
        NAME varchar(64) not null,
        DISPLAYNAME varchar(255),
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODTYPEATTRIBUTEGROUP_ID)
    ) comment='Attrib view group. Example: optic system, sizes, etc.' ;

    create table TPRODUCT (
        PRODUCT_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null comment 'Product or product sku code' unique,
        AVAILABLEFROM datetime comment 'AVAILABLEFROM AVAILABLETO date range when product will be available, for pre and back orders',
        AVAILABLETO datetime,
        NAME varchar(255) not null,
        DISPLAYNAME longtext,
        DESCRIPTION longtext,
        TAG varchar(255) comment 'Set of product tags',
        BRAND_ID bigint not null,
        PRODUCTTYPE_ID bigint not null,
        AVAILABILITY integer default 1 not null,
        FEATURED bit comment 'Is product featured?',
        URI varchar(255),
        TITLE varchar(255),
        METAKEYWORDS varchar(255),
        METADESCRIPTION varchar(255),
        DISPLAY_TITLE longtext,
        DISPLAY_METAKEYWORDS longtext,
        DISPLAY_METADESCRIPTION longtext,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODUCT_ID)
    ) ;

    create table TPRODUCTASSOCIATION (
        PRODUCTASSOCIATION_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        RANK integer comment 'RANK can have different meaning, depending from association type for example similarity always will be less, that 1000000 (MAX similarity) who buy , counter of who buy the one product also buy other product',
        ASSOCIATION_ID bigint not null,
        PRODUCT_ID bigint not null,
        ASSOCIATEDPRODUCT_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODUCTASSOCIATION_ID)
    ) ;

    create table TPRODUCTATTRVALUE (
        ATTRVALUE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        VAL longtext,
        DISPLAYVAL longtext,
        PRODUCT_ID bigint not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    ) ;

    create table TPRODUCTCATEGORY (
        PRODUCTCATEGORY_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        PRODUCT_ID bigint not null,
        CATEGORY_ID bigint not null,
        RANK integer comment 'What the default order of particular product in partcular category',
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODUCTCATEGORY_ID)
    ) ;

    create table TPRODUCTSKUATTRVALUE (
        ATTRVALUE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        VAL longtext,
        DISPLAYVAL longtext,
        SKU_ID bigint not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    ) ;

    create table TPRODUCTTYPE (
        PRODUCTTYPE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        NAME varchar(255),
        DESCRIPTION varchar(255),
        UITEMPLATE varchar(255) comment 'What UI template will be used for show particular type of product.',
        UISEARCHTEMPLATE varchar(255) comment 'UI template label for search on category, if category has a default product type.',
        SERVICE bit comment 'shall we treat this like service ? It mean no inventory will be check',
        ENSEMBLE bit comment 'is this a bundle of products ?',
        SHIPPABLE bit comment 'is this a physical product ? must have weight and sizes in attributes if yes',
        DIGITAL bit default 0 comment 'is this the digital product ?',
        DOWNLOADABLE bit default 0 comment 'is digital product can be downloaded?',
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODUCTTYPE_ID)
    ) comment='Product type.' ;

    create table TPRODUCTTYPEATTR (
        PRODTYPEATTR_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null,
        PRODUCTTYPE_ID bigint not null,
        RANK integer default 500 comment 'What order for filtered navigation',
        VISIBLE bit comment 'Is attribute visible on storefront',
        SIMILARITY bit comment 'Is attribute will be used for calculate products similarity',
        NAV bit comment 'Will be used in filtered navigation',
        NAV_TYPE varchar(1) default 'S' comment 'S - single, R- range',
        RANGE_NAV longtext comment 'In case of range navigation hold the ranges values',
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PRODTYPEATTR_ID)
    ) ;

    create table TROLE (
        ROLE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null unique,
        DESCRIPTION varchar(255),
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ROLE_ID)
    ) comment='Hold roles list' ;

    create table TSEOIMAGE (
        SEOIMAGE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        IMAGE_NAME varchar(255),
        ALT varchar(255),
        TITLE varchar(255),
        DISPLAY_ALT longtext,
        DISPLAY_TITLE longtext,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SEOIMAGE_ID)
    ) comment='Search optimizations on images.' ;

    create table TSHOP (
        SHOP_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null unique,
        NAME varchar(64) not null,
        DESCRIPTION longtext,
        FSPOINTER longtext not null comment 'Point to jsps',
        IMGVAULT longtext not null comment 'Point to image repository',
        URI varchar(255),
        TITLE varchar(255),
        METAKEYWORDS varchar(255),
        METADESCRIPTION varchar(255),
        DISPLAY_TITLE longtext,
        DISPLAY_METAKEYWORDS longtext,
        DISPLAY_METADESCRIPTION longtext,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOP_ID)
    ) ;

    create table TSHOPADVPLACE (
        SHOPADVPLACE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        NAME varchar(255) not null,
        DESCRIPTION longtext,
        SHOP_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOPADVPLACE_ID)
    ) comment='Adv places in particular shop' ;

    create table TSHOPADVRULES (
        SHOPADVRULES_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        rank integer,
        NAME varchar(255) not null,
        DESCRIPTION longtext,
        AVAILABLEFROM datetime,
        AVAILABLETO datetime,
        RULE longtext,
        SHOPADVPLACE_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOPADVRULES_ID)
    ) comment='Adv rules for show some item in adv place' ;

    create table TSHOPATTRVALUE (
        ATTRVALUE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        VAL longtext,
        DISPLAYVAL longtext,
        SHOP_ID bigint not null,
        CODE varchar(255) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    ) ;

    create table TSHOPCATEGORY (
        SHOPCATEGORY_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        RANK integer comment 'What the default order of assignment ?',
        SHOP_ID bigint not null,
        CATEGORY_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOPCATEGORY_ID)
    ) ;

    create table TSHOPEXCHANGERATE (
        SHOPEXCHANGERATE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        FROMCURRENCY varchar(3) not null,
        TOCURRENCY varchar(3) not null,
        SHOP_ID bigint not null,
        RATE decimal(19,2) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOPEXCHANGERATE_ID)
    ) ;

    create table TSHOPTOPSELLER (
        SHOPTOPSELLER_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        SHOP_ID bigint not null,
        PRODUCT_ID bigint not null,
        COUNTER decimal(19,2),
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOPTOPSELLER_ID)
    ) comment='Hold top seller information' ;

    create table TSHOPURL (
        STOREURL_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        URL longtext not null,
        SHOP_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (STOREURL_ID)
    ) ;

    create table TSHOPWAREHOUSE (
        SHOPWAREHOUSE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        SHOP_ID bigint not null,
        WAREHOUSE_ID bigint not null,
        RANK integer comment 'Warehouse priority usage',
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SHOPWAREHOUSE_ID)
    ) comment='Relation between shop and warehouses' ;

    create table TSKU (
        SKU_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null unique,
        NAME varchar(255) not null,
        DISPLAYNAME longtext,
        DESCRIPTION longtext,
        PRODUCT_ID bigint,
        RANK integer comment 'Order of sku for product. First is default.',
        BARCODE varchar(128),
        URI varchar(255),
        TITLE varchar(255),
        METAKEYWORDS varchar(255),
        METADESCRIPTION varchar(255),
        DISPLAY_TITLE longtext,
        DISPLAY_METAKEYWORDS longtext,
        DISPLAY_METADESCRIPTION longtext,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SKU_ID)
    ) ;

    create table TSKUPRICE (
        SKUPRICE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        SKU_ID bigint not null,
        SHOP_ID bigint not null,
        CURRENCY varchar(3) not null,
        QTY decimal(19,2) not null comment 'Quantity of SKU. Price tier',
        REGULAR_PRICE decimal(19,2) not null comment 'Regular price',
        SALE_PRICE decimal(19,2) comment 'Sale price. Must be less that regular',
        MINIMAL_PRICE decimal(19,2) comment 'Minimal price used in name-your-price pricing strategy. Must be less that regular',
        SALE_FROM datetime,
        SALE_TO datetime,
        TAG varchar(45),
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null,
        primary key (SKUPRICE_ID)
    ) comment='Prices. Some discriminators like condition can be added' ;

    create table TSKUWAREHOUSE (
        SKUWAREHOUSE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        WAREHOUSE_ID bigint not null,
        SKU_ID bigint not null,
        QUANTITY decimal(19,2) not null comment 'Current quantity',
        RESERVED decimal(19,2) default 0 comment 'Reserved by payment transaction quantity.',
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SKUWAREHOUSE_ID)
    ) comment='Real quantity of sku at each warehouse' ;

    create table TSTATE (
        STATE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        COUNTRY_CODE varchar(2) not null,
        STATE_CODE varchar(64) not null,
        NAME varchar(64) not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (STATE_ID)
    ) comment='List of supported states.' ;

    create table TSYSTEM (
        SYSTEM_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null,
        NAME varchar(64) not null,
        DESCRIPTION longtext,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (SYSTEM_ID)
    ) ;

    create table TSYSTEMATTRVALUE (
        ATTRVALUE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        VAL longtext,
        DISPLAYVAL longtext,
        CODE varchar(255) not null,
        SYSTEM_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (ATTRVALUE_ID)
    ) ;

    create table TWAREHOUSE (
        WAREHOUSE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null,
        NAME varchar(64) not null,
        DESCRIPTION longtext,
        COUNTRY_CODE varchar(64),
        STATE_CODE varchar(64),
        CITY varchar(128),
        POSTCODE varchar(16),
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (WAREHOUSE_ID)
    ) ;



    create table TPROMOTION (
        PROMOTION_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null unique,
        RANK integer default 500,
        SHOP_CODE varchar(255) not null,
        CURRENCY varchar(5) not null,
        PROMO_TYPE varchar(1) not null,
        PROMO_ACTION varchar(1) not null,
        ELIGIBILITY_CONDITION longtext not null,
        PROMO_ACTION_CONTEXT varchar(255),
        NAME varchar(255) not null,
        DISPLAYNAME longtext,
        DESCRIPTION varchar(100),
        DISPLAYDESCRIPTION longtext,
        TAG varchar(255),
        COUPON_TRIGGERED bit not null,
        CAN_BE_COMBINED bit not null,
        ENABLED bit not null,
        ENABLED_FROM datetime,
        ENABLED_TO datetime,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (PROMOTION_ID)
    );


    create table TPROMOTIONCOUPON (
        PROMOTIONCOUPON_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CODE varchar(255) not null unique,
        PROMOTION_ID bigint not null,
        USAGE_LIMIT integer default 1,
        USAGE_LIMIT_PER_CUSTOMER integer default 1,
        USAGE_COUNT integer default 0,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        primary key (PROMOTIONCOUPON_ID)
    );


    create table TPROMOTIONCOUPONUSAGE (
        PROMOTIONCOUPONUSAGE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CUSTOMER_EMAIL varchar(255) not null,
        COUPON_ID bigint not null,
        CUSTOMERORDER_ID bigint not null,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        primary key (PROMOTIONCOUPONUSAGE_ID)
    );


    create table TMAIL (
        MAIL_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        SHOP_CODE varchar(255),
        SUBJECT varchar(512) not null,
        EMAIL_FROM varchar(512) not null,
        EMAIL_RECEPIENTS varchar(512) not null,
        EMAIL_CC varchar(512),
        EMAIL_BCC varchar(512),
        TEXT_VERSION MEDIUMTEXT,
        HTML_VERSION MEDIUMTEXT,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (MAIL_ID)
    );


    create table TMAILPART (
        MAILPART_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        MAIL_ID bigint not null default 0,
        RESOURCE_ID varchar(255),
        FILENAME varchar(255),
        PART_DATA blob,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        primary key (MAILPART_ID)
    );


    create table TSHOPPINGCARTSTATE (
        TSHOPPINGCARTSTATE_ID bigint not null auto_increment,
        VERSION bigint not null default 0,
        CREATED_TIMESTAMP datetime,
        UPDATED_TIMESTAMP datetime,
        CREATED_BY varchar(64),
        UPDATED_BY varchar(64),
        GUID varchar(36) not null unique,
        CART_STATE varbinary(16384),
        CUSTOMER_EMAIL varchar(255),
        primary key (TSHOPPINGCARTSTATE_ID)
    );



	create table HIBERNATE_UNIQUE_KEYS (
         value integer 
    );

    alter table TADDRESS 
        add index FKADDRCUSTOMER (CUSTOMER_ID), 
        add constraint FKADDRCUSTOMER 
        foreign key (CUSTOMER_ID) 
        references TCUSTOMER (CUSTOMER_ID) 
        on delete cascade;


    alter table TATTRIBUTE 
        add index FK_ATTRIBUTE_ETYPE (ETYPE_ID), 
        add constraint FK_ATTRIBUTE_ETYPE 
        foreign key (ETYPE_ID) 
        references TETYPE (ETYPE_ID);


    alter table TATTRIBUTE 
        add index FK_ATTRIBUTE_ATTRIBUTEGROUP (ATTRIBUTEGROUP_ID), 
        add constraint FK_ATTRIBUTE_ATTRIBUTEGROUP 
        foreign key (ATTRIBUTEGROUP_ID) 
        references TATTRIBUTEGROUP (ATTRIBUTEGROUP_ID);


    alter table TBRANDATTRVALUE 
        add index FKFA06C3CD5CB3C6AB (CODE), 
        add constraint FKFA06C3CD5CB3C6AB 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);

    alter table TBRANDATTRVALUE 
        add index FKFA06C3CDDF51D3AD (BRAND_ID), 
        add constraint FKFA06C3CDDF51D3AD 
        foreign key (BRAND_ID) 
        references TBRAND (BRAND_ID) 
        on delete cascade;



    alter table TCARRIERSLA 
        add index FK_CSLA_CARR (CARRIER_ID), 
        add constraint FK_CSLA_CARR 
        foreign key (CARRIER_ID) 
        references TCARRIER (CARRIER_ID);


    alter table TCATEGORY 
        add index FK_CAT_PRODTYPE (PRODUCTTYPE_ID), 
        add constraint FK_CAT_PRODTYPE 
        foreign key (PRODUCTTYPE_ID) 
        references TPRODUCTTYPE (PRODUCTTYPE_ID);


    alter table TCATEGORYATTRVALUE 
        add index FK_CAT_ATTRIBUTE (CODE), 
        add constraint FK_CAT_ATTRIBUTE 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);

    alter table TCATEGORYATTRVALUE 
        add index FKBAB98EE5FA5E3ED (CATEGORY_ID), 
        add constraint FKBAB98EE5FA5E3ED 
        foreign key (CATEGORY_ID) 
        references TCATEGORY (CATEGORY_ID) 
        on delete cascade;


    alter table TCUSTOMERATTRVALUE 
        add index FK_C_ATTRIBUTE (CODE), 
        add constraint FK_C_ATTRIBUTE 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);

    alter table TCUSTOMERATTRVALUE 
        add index FKB44A120EAF56CFED (CUSTOMER_ID), 
        add constraint FKB44A120EAF56CFED 
        foreign key (CUSTOMER_ID) 
        references TCUSTOMER (CUSTOMER_ID) 
        on delete cascade;



    alter table TCUSTOMERORDER 
        add index FK_ORDER_SHOP (SHOP_ID), 
        add constraint FK_ORDER_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP (SHOP_ID);


    alter table TCUSTOMERORDER 
        add index FK_ORDER_CUSTOMER (CUSTOMER_ID), 
        add constraint FK_ORDER_CUSTOMER 
        foreign key (CUSTOMER_ID) 
        references TCUSTOMER (CUSTOMER_ID);

    create index CUSTOMERORDER_NUM on TCUSTOMERORDER (ORDERNUM);
    create index CUSTOMERORDER_CART on TCUSTOMERORDER (CART_GUID);


    alter table TCUSTOMERORDERDELIVERY
        add index FK_OD_ORD (CUSTOMERORDER_ID),
        add constraint FK_OD_ORD
        foreign key (CUSTOMERORDER_ID)
        references TCUSTOMERORDER (CUSTOMERORDER_ID) on delete cascade;

    alter table TCUSTOMERORDERDELIVERY
        add index FK_OD_CSLA (CARRIERSLA_ID),
        add constraint FK_OD_CSLA
        foreign key (CARRIERSLA_ID)
        references TCARRIERSLA (CARRIERSLA_ID);

    alter table TCUSTOMERORDERDELIVERYDET
        add index FK_CODD_CDELIVERY (CUSTOMERORDERDELIVERY_ID),
        add constraint FK_CODD_CDELIVERY
        foreign key (CUSTOMERORDERDELIVERY_ID)
        references TCUSTOMERORDERDELIVERY (CUSTOMERORDERDELIVERY_ID);


    alter table TCUSTOMERORDERDET 
        add index FKCB358C37A7F39C2D (CUSTOMERORDER_ID), 
        add constraint FKCB358C37A7F39C2D 
        foreign key (CUSTOMERORDER_ID) 
        references TCUSTOMERORDER (CUSTOMERORDER_ID);


    alter table TCUSTOMERSHOP 
        add index FK_CS_SHOP (SHOP_ID), 
        add constraint FK_CS_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP (SHOP_ID);

    alter table TCUSTOMERSHOP 
        add index FK_CS_CUSTOMER (CUSTOMER_ID), 
        add constraint FK_CS_CUSTOMER 
        foreign key (CUSTOMER_ID) 
        references TCUSTOMER (CUSTOMER_ID)         on delete cascade;


    alter table TCUSTOMERWISHLIST 
        add index FK_WL_SKU (SKU_ID), 
        add constraint FK_WL_SKU 
        foreign key (SKU_ID) 
        references TSKU (SKU_ID);


    alter table TCUSTOMERWISHLIST 
        add index FK_WL_CUSTOMER (CUSTOMER_ID), 
        add constraint FK_WL_CUSTOMER 
        foreign key (CUSTOMER_ID) 
        references TCUSTOMER (CUSTOMER_ID);


    alter table TENSEMBLEOPT 
        add index FK_END_SKU (SKU_ID), 
        add constraint FK_END_SKU 
        foreign key (SKU_ID) 
        references TSKU (SKU_ID);


    alter table TENSEMBLEOPT 
        add index FK_ENS_PROD (PRODUCT_ID), 
        add constraint FK_ENS_PROD 
        foreign key (PRODUCT_ID) 
        references TPRODUCT (PRODUCT_ID);


    alter table TMAILTEMPLATE 
        add index FK_M_TEMPLATEGROUP (MAILTEMPLATEGROUP_ID), 
        add constraint FK_M_TEMPLATEGROUP 
        foreign key (MAILTEMPLATEGROUP_ID) 
        references TMAILTEMPLATEGROUP (MAILTEMPLATEGROUP_ID);


    create index MANAGER_EMAIL on TMANAGER (EMAIL);

    alter table TPRODTYPEATTRVIEWGROUP 
        add index FK4589D8C42AD8F70D (PRODUCTTYPE_ID), 
        add constraint FK4589D8C42AD8F70D 
        foreign key (PRODUCTTYPE_ID) 
        references TPRODUCTTYPE (PRODUCTTYPE_ID);


    alter table TPRODUCT 
        add index FK_PROD_PRODTYPE (PRODUCTTYPE_ID), 
        add constraint FK_PROD_PRODTYPE 
        foreign key (PRODUCTTYPE_ID) 
        references TPRODUCTTYPE (PRODUCTTYPE_ID);

    alter table TPRODUCT 
        add index FK_PROD_BRAND (BRAND_ID), 
        add constraint FK_PROD_BRAND 
        foreign key (BRAND_ID) 
        references TBRAND (BRAND_ID);

                                                           
    alter table TPRODUCTASSOCIATION 
        add index FK_PA_ASSOC (ASSOCIATION_ID), 
        add constraint FK_PA_ASSOC 
        foreign key (ASSOCIATION_ID) 
        references TASSOCIATION (ASSOCIATION_ID);

    alter table TPRODUCTASSOCIATION 
        add index FK_PA_ASSOCPROD (ASSOCIATEDPRODUCT_ID), 
        add constraint FK_PA_ASSOCPROD 
        foreign key (ASSOCIATEDPRODUCT_ID) 
        references TPRODUCT (PRODUCT_ID);


    alter table TPRODUCTASSOCIATION 
        add index FK_PA_PRODUCT (PRODUCT_ID), 
        add constraint FK_PA_PRODUCT 
        foreign key (PRODUCT_ID) 
        references TPRODUCT (PRODUCT_ID);


    alter table TPRODUCTATTRVALUE 
        add index FK_P_ATTRIBUTE (CODE), 
        add constraint FK_P_ATTRIBUTE 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);

    alter table TPRODUCTATTRVALUE 
        add index FK215F4E65FFF5E8AD (PRODUCT_ID), 
        add constraint FK215F4E65FFF5E8AD 
        foreign key (PRODUCT_ID) 
        references TPRODUCT (PRODUCT_ID) 
        on delete cascade;




    alter table TPRODUCTCATEGORY 
        add index FK_PC_CAT (CATEGORY_ID), 
        add constraint FK_PC_CAT 
        foreign key (CATEGORY_ID) 
        references TCATEGORY (CATEGORY_ID);


    alter table TPRODUCTCATEGORY 
        add index FK_PC_PRODUCT (PRODUCT_ID), 
        add constraint FK_PC_PRODUCT 
        foreign key (PRODUCT_ID) 
        references TPRODUCT (PRODUCT_ID);



    alter table TPRODUCTSKUATTRVALUE 
        add index FK23B3D31E4EC4B749 (SKU_ID), 
        add constraint FK23B3D31E4EC4B749 
        foreign key (SKU_ID) 
        references TSKU (SKU_ID);

    alter table TPRODUCTSKUATTRVALUE 
        add index FK_S_ATTRIBUTE (CODE), 
        add constraint FK_S_ATTRIBUTE 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);


    alter table TPRODUCTTYPEATTR 
        add index FK_PTA_PRODTYPE (PRODUCTTYPE_ID), 
        add constraint FK_PTA_PRODTYPE 
        foreign key (PRODUCTTYPE_ID) 
        references TPRODUCTTYPE (PRODUCTTYPE_ID)         on delete cascade;

    alter table TPRODUCTTYPEATTR 
        add index FK_PTA_ATTR (CODE), 
        add constraint FK_PTA_ATTR 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);

    create index ROLE_CODE on TROLE (CODE);


    alter table TSHOPADVPLACE 
        add index FK_ADVP_SHOP (SHOP_ID), 
        add constraint FK_ADVP_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP (SHOP_ID);



    alter table TSHOPADVRULES 
        add index FK_ADVR_ADVPLACE (SHOPADVPLACE_ID), 
        add constraint FK_ADVR_ADVPLACE 
        foreign key (SHOPADVPLACE_ID) 
        references TSHOPADVPLACE (SHOPADVPLACE_ID);


    alter table TSHOPATTRVALUE 
        add index FK_ATTR_SHOP (SHOP_ID), 
        add constraint FK_ATTR_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP (SHOP_ID);


    alter table TSHOPATTRVALUE 
        add index FK_SHOP_ATTRIBUTE (CODE), 
        add constraint FK_SHOP_ATTRIBUTE 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);



    alter table TSHOPCATEGORY 
        add index FK_SC_SHOP (SHOP_ID), 
        add constraint FK_SC_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP (SHOP_ID);

    alter table TSHOPCATEGORY 
        add index FK_SC_CAT (CATEGORY_ID), 
        add constraint FK_SC_CAT 
        foreign key (CATEGORY_ID) 
        references TCATEGORY (CATEGORY_ID);



    alter table TSHOPEXCHANGERATE 
        add index FK_ER_SHOP (SHOP_ID), 
        add constraint FK_ER_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP (SHOP_ID);



    alter table TSHOPTOPSELLER 
        add index FKB33456EAE13125FC (PRODUCT_ID), 
        add constraint FKB33456EAE13125FC 
        foreign key (PRODUCT_ID) 
        references TPRODUCT (PRODUCT_ID);


    alter table TSHOPURL 
        add index FK_SHOPURL_SHOP (SHOP_ID), 
        add constraint FK_SHOPURL_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP (SHOP_ID);


    alter table TSHOPWAREHOUSE 
        add index FK13C59499F65CA98 (SHOP_ID), 
        add constraint FK13C59499F65CA98 
        foreign key (SHOP_ID) 
        references TSHOP (SHOP_ID);

    alter table TSHOPWAREHOUSE 
        add index FK13C594991C1544FC (WAREHOUSE_ID), 
        add constraint FK13C594991C1544FC 
        foreign key (WAREHOUSE_ID) 
        references TWAREHOUSE (WAREHOUSE_ID);



    alter table TSKU 
        add index FK_SKU_PROD (PRODUCT_ID), 
        add constraint FK_SKU_PROD 
        foreign key (PRODUCT_ID) 
        references TPRODUCT (PRODUCT_ID);



    alter table TSKUPRICE 
        add index FK_SP_SKU (SKU_ID), 
        add constraint FK_SP_SKU 
        foreign key (SKU_ID) 
        references TSKU (SKU_ID);

    alter table TSKUPRICE 
        add index FK_SP_SHOP (SHOP_ID), 
        add constraint FK_SP_SHOP 
        foreign key (SHOP_ID) 
        references TSHOP (SHOP_ID);



    alter table TSKUWAREHOUSE 
        add index FKAC00F89A4EC4B749 (SKU_ID), 
        add constraint FKAC00F89A4EC4B749 
        foreign key (SKU_ID) 
        references TSKU (SKU_ID);

    alter table TSKUWAREHOUSE 
        add index FKAC00F89A1C1544FC (WAREHOUSE_ID), 
        add constraint FKAC00F89A1C1544FC 
        foreign key (WAREHOUSE_ID) 
        references TWAREHOUSE (WAREHOUSE_ID);

    alter table TSKUWAREHOUSE
        add constraint U_SKUINVENTORY unique (WAREHOUSE_ID, SKU_ID);

    alter table TSYSTEMATTRVALUE 
        add index FK_SYS_ATTRIBUTE (CODE), 
        add constraint FK_SYS_ATTRIBUTE 
        foreign key (CODE) 
        references TATTRIBUTE (CODE);

    alter table TSYSTEMATTRVALUE 
        add index FK_ATTR_SYS (SYSTEM_ID), 
        add constraint FK_ATTR_SYS 
        foreign key (SYSTEM_ID) 
        references TSYSTEM (SYSTEM_ID);


    create index IMAGE_NAME_IDX on TSEOIMAGE (IMAGE_NAME);

    create index PROMO_SHOP_CODE on TPROMOTION (SHOP_CODE);
    create index PROMO_CURRENCY on TPROMOTION (CURRENCY);
    create index PROMO_PTYPE on TPROMOTION (PROMO_TYPE);
    create index PROMO_PACTION on TPROMOTION (PROMO_ACTION);
    create index PROMO_ENABLED on TPROMOTION (ENABLED);
    create index PROMO_ENABLED_FROM on TPROMOTION (ENABLED_FROM);
    create index PROMO_ENABLED_TO on TPROMOTION (ENABLED_TO);


    alter table TPROMOTIONCOUPON
        add index FK_PROMO_COUPON (PROMOTION_ID),
        add constraint FK_PROMO_COUPON
        foreign key (PROMOTION_ID)
        references TPROMOTION (PROMOTION_ID);

    alter table TPROMOTIONCOUPONUSAGE
        add index FK_COUPON_USAGE (COUPON_ID),
        add constraint FK_COUPON_USAGE
        foreign key (COUPON_ID)
        references TPROMOTIONCOUPON (PROMOTIONCOUPON_ID);

    alter table TPROMOTIONCOUPONUSAGE
        add index FK_ORD_COUPON_USAGE (CUSTOMERORDER_ID),
        add constraint FK_ORD_COUPON_USAGE
        foreign key (CUSTOMERORDER_ID)
        references TCUSTOMERORDER (CUSTOMERORDER_ID) on delete cascade;


    create index PROMOTIONCOUPONUSAGE_EMAIL on TPROMOTIONCOUPONUSAGE (CUSTOMER_EMAIL);

    create index SHOPPINGCARTSTATE_EMAIL on TSHOPPINGCARTSTATE (CUSTOMER_EMAIL);


    alter table TMAILPART
        add index FK_MAIL(MAIL_ID)  ,
        add constraint FKMAILMAILPART
        foreign key (MAIL_ID)
        references TMAIL(MAIL_ID);

