/*
 * Copyright 2009 Denys Pavlov, Igor Azarnyi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.yes.cart.constants;

/**
 * Build in attribute names.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
@SuppressWarnings("PMD.AvoidConstantsInterface")
public interface AttributeNamesKeys {


    interface System {

        /**
         * Comma separated list of active payment gateways.
         */
        String SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL = "SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABEL";
        /**
         * Etag expiration timeout for images.
         */
        String SYSTEM_ETAG_CACHE_IMAGES_TIME = "SYSTEM_ETAG_CACHE_IMAGES_TIME";
        /**
         * Etag expiration timeout for pages.
         */
        String SYSTEM_ETAG_CACHE_PAGES_TIME = "SYSTEM_ETAG_CACHE_PAGES_TIME";
        /**
         * Default shop to redirect.
         */
        String SYSTEM_DEFAULT_SHOP = "SYSTEM_DEFAULT_SHOP";
        /**
         * CMS preview URL template.
         */
        String SYSTEM_PREVIEW_URL_TEMPLATE = "SYSTEM_PREVIEW_URL_TEMPLATE";
        /**
         * CMS preview URL template.
         */
        String SYSTEM_PREVIEW_URI_CSS = "SYSTEM_PREVIEW_URI_CSS";
        /**
         * Size of the log tail in characters to show in Admin.
         */
        String IMPORT_JOB_LOG_SIZE = "IMPORT_JOB_LOG_SIZE";
        /**
         * Import job timeout (if ping takes more than this value then the job is considered halted).
         */
        String IMPORT_JOB_TIMEOUT_MS = "IMPORT_JOB_TIMEOUT_MS";
        /**
         * Batch size for indexing.
         */
        String JOB_REINDEX_PRODUCT_BATCH_SIZE = "JOB_REINDEX_PRODUCT_BATCH_SIZE";
        /**
         * Delta check size for inventory changed processor.
         */
        String JOB_PRODUCT_INVENTORY_UPDATE_DELTA = "JOB_PRODINVUP_DELTA";
        /**
         * Delta check delay for inventory changed processor.
         */
        String JOB_PRODUCT_INVENTORY_UPDATE_DELTA_DELAY_SECONDS = "JOB_PRODINVUP_DELTA_S";
        /**
         * Full reindex size for inventory changed processor.
         */
        String JOB_PRODUCT_INVENTORY_FULL_THRESHOLD = "JOB_PRODINVUP_FULL";
        /**
         * Timeout for backdoor WS call.
         */
        String SYSTEM_BACKDOOR_TIMEOUT_MS = "SYSTEM_BACKDOOR_TIMEOUT_MS";
        /**
         * Timeout for backdoor WS call.
         */
        String SYSTEM_BACKDOOR_PRODUCT_BULK_INDEX_TIMEOUT_MS = "SYSTEM_BACKDOOR_PRODB_IDX_TIMEOUT_MS";
        /**
         * Timeout for backdoor WS call.
         */
        String SYSTEM_BACKDOOR_PRODUCT_SINGLE_INDEX_TIMEOUT_MS = "SYSTEM_BACKDOOR_PRODS_IDX_TIMEOUT_MS";
        /**
         * Timeout for backdoor WS call.
         */
        String SYSTEM_BACKDOOR_SQL_TIMEOUT_MS = "SYSTEM_BACKDOOR_SQL_TIMEOUT_MS";
        /**
         * Timeout for backdoor WS call.
         */
        String SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS = "SYSTEM_BACKDOOR_CACHE_TIMEOUT_MS";
        /**
         * Timeout for backdoor WS call.
         */
        String SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS = "SYSTEM_BACKDOOR_IMAGE_TIMEOUT_MS";
        /**
         * Default (failover) directory for resources.
         */
        String SYSTEM_DEFAULT_FSPOINTER = "SYSTEM_DEFAULT_FSPOINTER";
        /**
         * Image repository.
         */
        String SYSTEM_IMAGE_VAULT = "SYSTEM_IMAGE_VAULT";
        /**
         * File repository.
         */
        String SYSTEM_FILE_VAULT = "SYSTEM_FILE_VAULT";
        /**
         * System file repository.
         */
        String SYSTEM_SYSFILE_VAULT = "SYSTEM_SYSFILE_VAULT";
        /**
         * Default (failover) directory for resources.
         */
        String SYSTEM_MAILTEMPLATES_FSPOINTER = "SYSTEM_MAILTEMPLATES_FSPOINTER";

        /**
         * Time in seconds for abandoned carts
         */
        String CART_ABANDONED_TIMEOUT_SECONDS = "CART_ABANDONED_TIMEOUT_SECONDS";

        /**
         * Time in seconds for expired guest accounts
         */
        String GUESTS_EXPIRY_TIMEOUT_SECONDS = "GUESTS_EXPIRY_TIMEOUT_SECONDS";

        /**
         * Time in seconds for abandoned carts
         */
        String CART_EMPTY_ANONYMOUS_TIMEOUT_SECONDS = "CART_EMPTY_ANONYMOUS_TIMEOUT_SECONDS";

        /**
         * Allowed image sizes
         */
        String SYSTEM_ALLOWED_IMAGE_SIZES = "SYSTEM_ALLOWED_IMAGE_SIZES";

    }


    interface Category {

        /**
         * Pagination option for category
         */
        String CATEGORY_ITEMS_PER_PAGE = "CATEGORY_ITEMS_PER_PAGE";

        /**
         * Comma separated list of product sorting options
         */
        String CATEGORY_SORT_OPTIONS = "CATEGORY_SORT_OPTIONS";

        /**
         * Number of categories to be displayed in one row for grid view
         */
        String CATEGORY_SUBCATEGORIES_COLUMNS = "CATEGORY_SUBCATEGORIES_COLUMNS";

        /**
         * Max count of featured products to be shown for category
         */
        String CATEGORY_ITEMS_FEATURED = "CATEGORY_ITEMS_FEATURED";

        /**
         * Max count of new arrival products for category
         */
        String CATEGORY_ITEMS_NEW_ARRIVAL = "CATEGORY_ITEMS_NEW_ARRIVAL";

        /**
         * Category image attribute name prefix
         */
        String CATEGORY_IMAGE_PREFIX = "CATEGORY_IMAGE";

        /**
         * Category image attribute name prefix
         */
        String CATEGORY_FILE_PREFIX = "CATEGORY_FILE";

        /**
         * Category image attribute name
         */
        String CATEGORY_IMAGE = CATEGORY_IMAGE_PREFIX + "0";

        /**
         * Label of category image retrieve strategy. Allowed values:
         * ATTRIBUTE
         * RANDOM_PRODUCT
         */
        String CATEGORY_IMAGE_RETRIEVE_STRATEGY = "CATEGORY_IMAGE_RETRIEVE_STRATEGY";

        /** Category images width.  */
        String CATEGORY_IMAGE_WIDTH = "CATEGORY_IMAGE_WIDTH";
        /** Category images height.  */
        String CATEGORY_IMAGE_HEIGHT = "CATEGORY_IMAGE_HEIGHT";

        /** Product image width in category.  */
        String PRODUCT_IMAGE_WIDTH = "PRODUCT_IMAGE_WIDTH";
        /** Product image height in category.  */
        String PRODUCT_IMAGE_HEIGHT = "PRODUCT_IMAGE_HEIGHT";

        /** Product image width in category.  */
        String PRODUCT_IMAGE_THUMB_WIDTH = "PRODUCT_IMAGE_THUMB_WIDTH";
        /** Product image height in category.  */
        String PRODUCT_IMAGE_THUMB_HEIGHT = "PRODUCT_IMAGE_THUMB_HEIGHT";

        /** Product columns in grid */
        String CATEGORY_PRODUCTS_COLUMNS = "CATEGORY_PRODUCTS_COLUMNS";

        /**
         * Category description attribute prefix. Requires current language to make
         * full attribute name. E.g. CATEGORY_DESCRIPTION_en.
         */
        String CATEGORY_DESCRIPTION_PREFIX = "CATEGORY_DESCRIPTION_";

        /**
         * Flag whether to include subcategories in search
         */
        String CATEGORY_INCLUDE_SUBCATEGORIES_IN_SEARCH = "INCLUDE_SUBCATEGORIES_IN_SEARCH_CAT";

        /**
         * Number of days for newarrival tag
         */
        String CATEGORY_NEW_ARRIVAL_DAYS_OFFSET = "CATEGORY_NEW_ARRIVAL_DAYS_OFFSET";

        /**
         * Limit of how many filter nav values to display in a block
         */
        String CATEGORY_FILTERNAV_LIMIT = "CATEGORY_FILTERNAV_LIMIT";

    }

    interface Product {

        /**
         * Product description attribute prefix. Requires current language to make
         * full attribute name. E.g. PRODUCT_DESCRIPTION_en.
         */
        String PRODUCT_DESCRIPTION_PREFIX = "PRODUCT_DESCRIPTION_";

        /**
         * Default image attribute name.
         */
        String PRODUCT_IMAGE_ATTR_NAME_PREFIX = "IMAGE";

        /**
         * Default image attribute name.
         */
        String PRODUCT_DEFAULT_IMAGE_ATTR_NAME = PRODUCT_IMAGE_ATTR_NAME_PREFIX + "0";

        /**
         * Default image attribute name.
         */
        String PRODUCT_FILE_ATTR_NAME_PREFIX = "FILE";

        /**
         * Product weight in KG (This attribute is used by Logistics operations)
         */
        String PRODUCT_WEIGHT_KG = "PRODUCT_WEIGHT_KG";

        /**
         * Product volume in m3 (This attribute is used by Logistics operations)
         */
        String PRODUCT_VOLUME_M3 = "PRODUCT_VOLUME_M3";
    }

    interface Shop {

        /**
         * Admin email in particular shop.
         */
        String SHOP_ADMIN_EMAIL = "SHOP_ADMIN_EMAIL";

        /**
         * B2B profile.
         */
        String SHOP_B2B = "SHOP_B2B";

        /**
         * Enable B2B address book mode.
         */
        String SHOP_B2B_ADDRESSBOOK = "SHOP_B2B_ADDRESSBOOK";

        /**
         * Enable B2B stict price mode (only prices in sub shop are considered).
         */
        String SHOP_B2B_STRICT_PRICE = "SHOP_B2B_STRICT_PRICE";

        /**
         * Storefront requires authentication.
         */
        String SHOP_SF_REQUIRE_LOGIN = "SHOP_SF_REQUIRE_LOGIN";

        /**
         * Storefront requires approval for new customer registrations of given types.
         */
        String SHOP_SF_REQUIRE_REG_APPROVE = "SHOP_SF_REQUIRE_REG_APPROVE_TYPES";

        /**
         * Registration for given types required notification to admin.
         */
        String SHOP_SF_REQUIRE_REG_NOTIFICATION = "SHOP_SF_REQUIRE_REG_NOTIFY_TYPES";

        /**
         * Customer types whose orders require approval.
         */
        String SHOP_SF_REQUIRE_ORDER_APPROVE = "SHOP_SF_REQUIRE_ORDER_APPROVE_TYPES";

        /**
         * Customer types who are not allowed to place orders.
         */
        String SHOP_SF_CANNOT_PLACE_ORDER = "SHOP_SF_CANNOT_PLACE_ORDER_TYPES";

        /**
         * Customer types who can do repeat orders.
         */
        String SHOP_SF_REPEAT_ORDER_TYPES = "SHOP_SF_REPEAT_ORDER_TYPES";

        /**
         * Customer types who can create shopping lists.
         */
        String SHOP_SF_SHOPPING_LIST_TYPES = "SHOP_SF_SHOPPING_LIST_TYPES";

        /**
         * Customer types who can leave per line remarks.
         */
        String SHOP_SF_B2B_LINE_REMARKS_TYPES = "SHOP_SF_B2B_LINE_REMARKS_TYPES";

        /**
         * Customer types who can add B2B related information.
         */
        String SHOP_SF_B2B_ORDER_FORM_TYPES = "SHOP_SF_B2B_ORDER_FORM_TYPES";

        /**
         * Supported by shop currencies.
         */
        String SUPPORTED_CURRENCIES = "CURRENCY";

        /**
         * Supported by shop countries for delivery.
         */
        String SUPPORTED_COUNTRY_SHIP = "COUNTRY_SHIP";

        /**
         * Supported by shop countries for billing.
         */
        String SUPPORTED_COUNTRY_BILL = "COUNTRY_BILL";

        /**
         * Supported by shop address formatter.
         */
        String ADDRESS_FORMATTER_PREFIX = "SHOP_ADDRESS_FORMATTER";

        /**
         * Supported by shop customer name formatter.
         */
        String CUSTOMER_NAME_FORMATTER = "SHOP_CUSTOMER_FORMATTER";

        /**
         * Supported by shop customer types.
         */
        String SHOP_CUSTOMER_TYPES = "SHOP_CUSTOMER_TYPES";

        /**
         * Flag to determine is guest checkout is enabled.
         */
        String SHOP_CHECKOUT_ENABLE_GUEST = "SHOP_CHECKOUT_ENABLE_GUEST";

        /**
         * Supported by shop customer types for request for quote.
         */
        String SHOP_RFQ_CUSTOMER_TYPES = "SHOP_RFQ_CUSTOMER_TYPES";

        /**
         * Customer types for whom address book management is disabled (addresses only managed via call centre).
         */
        String SHOP_ADDRESSBOOK_DISABLED_CUSTOMER_TYPES = "SHOP_ADDRESSBOOK_DISABLED_CUSTOMER_TYPES";

        /**
         * Supported by shop storefront languages.
         */
        String SUPPORTED_LANGUAGES = "SUPPORTED_LANGUAGES";

        /**
         * Comma separated list of active payment gateways.
         */
        String SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL = "SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL";

        /**
         * Regular expression for allowed IPs.
         */
        String SHOP_PAYMENT_GATEWAYS_ALLOWED_IPS_REGEX = "SHOP_PAYMENT_GATEWAYS_ALLOWED_IPS_REGEX";


        /**
         * Shop image attribute name prefix.
         */
        String SHOP_IMAGE_PREFIX = "SHOP_IMAGE";

        /**
         * Shop file attribute name prefix.
         */
        String SHOP_FILE_PREFIX = "SHOP_FILE";

        /**
         * Default Shop image.
         */
        String SHOP_IMAGE = SHOP_IMAGE_PREFIX + "0";

        /**
         * Setting for whether to show quantity picker or not
         */
        String CART_ADD_ENABLE_QTY_PICKER = "CART_ADD_ENABLE_QTY_PICKER";

        /**
         * Setting for whether to show prices with tax information
         */
        String SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO = "SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO";

        /**
         * Setting for whether to allow customer of specified types to manipulate tax options
         */
        String SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE_TYPES = "SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_CHANGE";

        /**
         * Setting for whether to show prices with tax information using NET price or GROSS price
         */
        String SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET = "SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_NET";

        /**
         * Setting for whether to show prices with tax information using tax amount or tax percent
         */
        String SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT = "SHOP_PRODUCT_ENABLE_PRICE_TAX_INFO_SHOW_AMOUNT";

        /**
         * Setting for whether to allow single address (i.e. use shipping for billing)
         */
        String SHOP_DELIVERY_ONE_ADDRESS_DISABLE = "SHOP_DELIVERY_ONE_ADDRESS_DISABLE";

        /**
         * Setting for whether to show prices
         */
        String SHOP_PRODUCT_HIDE_PRICES = "SHOP_PRODUCT_HIDE_PRICES";

        /**
         * Flag whether to include subcategories in search
         */
        String SHOP_INCLUDE_SUBCATEGORIES_IN_SEARCH = "INCLUDE_SUBCATEGORIES_IN_SEARCH_SHOP";

        /**
         * Product attributes to copy over to order line
         */
        String SHOP_PRODUCT_STORED_ATTRIBUTES = "SHOP_PRODUCT_STORED_ATTRIBUTES";

        /**
         * Flag whether to use manufacturer code or seller code as primary UI property
         */
        String SHOP_PRODUCT_DISPLAY_MANUFACTURER_CODE = "PRODUCT_DISPLAY_MANUFACTURER_CODE_SHOP";

        /**
         * Number of days for newarrival tag
         */
        String SHOP_NEW_ARRIVAL_DAYS_OFFSET = "SHOP_NEW_ARRIVAL_DAYS_OFFSET";

        /**
         * Comma separated list of page size options
         */
        String SHOP_CATEGORY_ITEMS_PER_PAGE = "SHOP_CATEGORY_ITEMS_PER_PAGE";

        /**
         * Enable compound searching for this shop
         */
        String SHOP_SEARCH_ENABLE_COMPOUND = "SHOP_SEARCH_ENABLE_COMPOUND";

        /**
         * Enable search suggest for this shop
         */
        String SHOP_SEARCH_ENABLE_SUGGEST = "SHOP_SEARCH_ENABLE_SUGGEST";

        /**
         * Max number of items to return for this shop
         */
        String SHOP_SEARCH_SUGGEST_MAX_ITEMS = "SHOP_SEARCH_SUGGEST_MAX_ITEMS";

        /**
         * Min number of char for search suggest for this shop
         */
        String SHOP_SEARCH_SUGGEST_MIN_CHARS = "SHOP_SEARCH_SUGGEST_MIN_CHARS";

        /**
         * Milliseconds after which result pop up should fade out
         */
        String SHOP_SEARCH_SUGGEST_FADE_OUT = "SHOP_SEARCH_SUGGEST_FADE_OUT";

        /**
         * Comma separated list of product sorting options
         */
        String SHOP_CATEGORY_SORT_OPTIONS = "SHOP_CATEGORY_SORT_OPTIONS";

        /** Category images width.  */
        String SHOP_CATEGORY_IMAGE_WIDTH = "SHOP_CATEGORY_IMAGE_WIDTH";
        /** Category images height.  */
        String SHOP_CATEGORY_IMAGE_HEIGHT = "SHOP_CATEGORY_IMAGE_HEIGHT";

        /** Product image width in category.  */
        String SHOP_PRODUCT_IMAGE_WIDTH = "SHOP_PRODUCT_IMAGE_WIDTH";
        /** Product image height in category.  */
        String SHOP_PRODUCT_IMAGE_HEIGHT = "SHOP_PRODUCT_IMAGE_HEIGHT";

        /** Product image width in category.  */
        String SHOP_PRODUCT_IMAGE_THUMB_WIDTH = "SHOP_PRODUCT_IMAGE_THUMB_WIDTH";
        /** Product image height in category.  */
        String SHOP_PRODUCT_IMAGE_THUMB_HEIGHT = "SHOP_PRODUCT_IMAGE_THUMB_HEIGHT";

        /** Product columns in grid */
        String SHOP_CATEGORY_PRODUCTS_COLUMNS = "SHOP_CATEGORY_PRODUCTS_COLUMNS";

        /**
         * Number of categories to be displayed in one row for grid view
         */
        String SHOP_CATEGORY_SUBCATEGORIES_COLUMNS = "SHOP_CATEGORY_SUBCATEGORIES_COLUMNS";

        /**
         * Max count of featured products to be shown for category
         */
        String SHOP_CATEGORY_ITEMS_FEATURED = "SHOP_CATEGORY_ITEMS_FEATURED";

        /**
         * Max count of new arrival products for category
         */
        String SHOP_CATEGORY_ITEMS_NEW_ARRIVAL = "SHOP_CATEGORY_ITEMS_NEW_ARRIVAL";

        /**
         * Limit of how many filter nav values to display in a block
         */
        String SHOP_CATEGORY_FILTERNAV_LIMIT = "SHOP_CATEGORY_FILTERNAV_LIMIT";

        /**
         * Setting for whether to show coupons or not
         */
        String CART_UPDATE_ENABLE_COUPONS = "SHOP_CHECKOUT_ENABLE_COUPONS";

        /**
         * Setting for whether to show order message or not
         */
        String CART_UPDATE_ENABLE_ORDER_MSG = "SHOP_CHECKOUT_ENABLE_ORDER_MSG";

        /**
         * Attributes to be used for the registration form
         */
        String CUSTOMER_REGISTRATION_ATTRIBUTES_PREFIX = "SHOP_CREGATTRS";

        /**
         * Attributes to be used for the profile editing (by customer) form
         */
        String CUSTOMER_PROFILE_ATTRIBUTES_VISIBLE_PREFIX = "SHOP_CPROFATTRS_VISIBLE";

        /**
         * Attributes on the profile editing (by customer) form which must not editable
         */
        String CUSTOMER_PROFILE_ATTRIBUTES_READONLY_PREFIX = "SHOP_CPROFATTRS_READONLY";

        /**
         * Time in seconds since last cart modification before session expires
         */
        String CART_SESSION_EXPIRY_SECONDS = "CART_SESSION_EXPIRY_SECONDS";

        /**
         * Enable cookie policy (aka The EU cookie law (e-Privacy Directive))
         */
        String SHOP_COOKIE_POLICY_ENABLE = "SHOP_COOKIE_POLICY_ENABLE";

        /**
         * Special token for customer password reset from Admin
         */
        String SHOP_CUSTOMER_PASSWORD_RESET_CC = "SHOP_CUSTOMER_PASSWORD_RESET_CC";

        /**
         * Special token for customer password reset from Admin
         */
        String SHOP_CUSTOMER_TOKEN_EXPIRY_SECONDS = "SHOP_CUSTOMER_TOKEN_EXPIRY_SECONDS";

        /**
         * Flag to indicate that this shop is using custom mail sender configurations.
         */
        String SHOP_MAIL_SERVER_CUSTOM_ENABLE = "SHOP_MAIL_SERVER_CUSTOM_ENABLE";

        /**
         * Custom mail sender configurations - host.
         */
        String SHOP_MAIL_SERVER_HOST = "SHOP_MAIL_SERVER_HOST";

        /**
         * Custom mail sender configurations - port.
         */
        String SHOP_MAIL_SERVER_PORT = "SHOP_MAIL_SERVER_PORT";

        /**
         * Custom mail sender configurations - username.
         */
        String SHOP_MAIL_SERVER_USERNAME = "SHOP_MAIL_SERVER_USERNAME";

        /**
         * Custom mail sender configurations - password.
         */
        String SHOP_MAIL_SERVER_PASSWORD = "SHOP_MAIL_SERVER_PASSWORD";

        /**
         * Custom mail sender configurations - Use SMTP-AUTH.
         */
        String SHOP_MAIL_SERVER_SMTPAUTH_ENABLE = "SHOP_MAIL_SERVER_SMTPAUTH_ENABLE";

        /**
         * Custom mail sender configurations - Use TLS to encrypt.
         */
        String SHOP_MAIL_SERVER_STARTTLS_ENABLE = "SHOP_MAIL_SERVER_STARTTLS_ENABLE";

        /**
         * Prefix for regional pricing. The format is SHOP_REGIONAL_PRICING_[COUNTRY_CODE]_[STATE_CODE]
         */
        String SHOP_REGIONAL_PRICING_PREFIX = "SHOP_REGIONAL_PRICING_";

        /**
         * Tracing of page rendering on storefront
         */
        String SHOP_SF_PAGE_TRACE = "SHOP_SF_PAGE_TRACE";

        /**
         * Supported suppliers map: code => email
         */
        String ORDER_EXPORTER_MAIL_SUPPORTED_SUPPLIERS = "ORDER_EXPORTER_MAIL_SUPPORTED_SUPPLIERS";

    }

    interface Brand {

        /**
         * Brand image attribute name prefix.
         */
        String BRAND_IMAGE_PREFIX = "BRAND_IMAGE";

        /**
         * Brand file attribute name prefix.
         */
        String BRAND_FILE_PREFIX = "BRAND_FILE";

        /**
         * Default Brand image.
         */
        String BRAND_IMAGE = BRAND_IMAGE_PREFIX + "0";

    }

    interface Customer {

        /**
         * Employee ID.
         */
        String B2B_REF = "CUSTOMER_B2B_REF";

        /**
         * Employee ID.
         */
        String B2B_EMPLOYEE_ID = "CUSTOMER_B2B_EMPLOYEE_ID";

        /**
         * Charge authority for placed orders
         */
        String B2B_CHARGE_ID = "CUSTOMER_B2B_CHARGE_ID";

        /**
         * Customer specific orders require approval flag
         */
        String B2B_REQUIRE_APPROVE = "CUSTOMER_B2B_REQUIRE_APPROVE";

        /**
         * Customer specific orders require approval flag
         */
        String B2B_REQUIRE_APPROVE_X = "CUSTOMER_B2B_REQUIRE_APPROVE_X";

        /**
         * Block checkout for this customer
         */
        String BLOCK_CHECKOUT = "CUSTOMER_BLOCK_CHECKOUT";

        /**
         * Block checkout for this customer
         */
        String BLOCK_CHECKOUT_X = "CUSTOMER_BLOCK_CHECKOUT_X";

        /**
         * Customer phone.
         */
        String CUSTOMER_PHONE = "CUSTOMER_PHONE";

        /**
         * Customer has opted in for marketing contact.
         */
        String MARKETING_OPT_IN = "MARKETING_OPT_IN";

    }

    interface Cart {

        String CUSTOMER_TYPE_GUEST = "B2G";

        String CUSTOMER_TYPE_REGULAR = "B2C";

        String CUSTOMER_TYPE_B2BSUB = "B2E";

        String ORDER_INFO_CUSTOMER_TYPE = "customerType";
        String ORDER_INFO_CUSTOMER_SHOPPING_LIST_ON = "shoppingListsEnabled";
        String ORDER_INFO_CUSTOMER_REPEAT_ORDER_ON = "repeatOrderEnabled";
        String ORDER_INFO_CUSTOMER_SHOPPING_RFQ_ON = "rfqEnabled";
        String ORDER_INFO_CUSTOMER_SHOPPING_B2B_FORM_ON = "orderB2BFormEnabled";
        String ORDER_INFO_CUSTOMER_SHOPPING_B2B_LINEREMARKS_ON = "orderB2BLineRemarksEnabled";
        String ORDER_INFO_CUSTOMER_MESSAGE_ON = "orderMessageEnabled";
        String ORDER_INFO_COUPONS_ON = "couponsEnabled";
        String ORDER_INFO_ADDRESSBOOK_ON = "addressBookEnabled";

        String ORDER_INFO_BLOCK_CHECKOUT = "blockCheckout";
        String ORDER_INFO_BLOCK_CHECKOUT_TYPE = "blockCheckoutType";
        String ORDER_INFO_BLOCK_CHECKOUT_CUSTOMER = "blockCheckoutCustomer";
        String ORDER_INFO_BLOCK_CHECKOUT_CUSTOMER_X = "blockCheckoutCustomerX";

        String ORDER_INFO_APPROVE_ORDER = "b2bRequireApprove";
        String ORDER_INFO_APPROVE_ORDER_TYPE = "b2bRequireApproveType";
        String ORDER_INFO_APPROVE_ORDER_CUSTOMER = "b2bRequireApproveCustomer";
        String ORDER_INFO_APPROVE_ORDER_CUSTOMER_X = "b2bRequireApproveCustomerX";

        String ORDER_INFO_B2B_REF = "b2bRef";
        String ORDER_INFO_B2B_EMPLOYEE_ID = "b2bEmployeeId";
        String ORDER_INFO_B2B_CHARGE_ID = "b2bChargeId";
        String ORDER_INFO_B2B_ORDER_REMARKS_ID = "b2bRemarks";
        String ORDER_INFO_B2B_ORDER_LINE_REMARKS_ID = "b2bRemarksLine";

        String ORDER_INFO_ORDER_LINE_PRICE_REF_ID = "priceRefLine";

        String ORDER_INFO_REQUESTED_DELIVERY_DATE_ID = "deliveryDate";

        String ORDER_INFO_B2B_APPROVED_BY = "b2bApprovedBy";
        String ORDER_INFO_B2B_APPROVED_DATE = "b2bApprovedDate";

    }

}
