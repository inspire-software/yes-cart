/*
 * Copyright 2009 Igor Azarnyi, Denys Pavlov
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
         * Comma separated list of paymenе modules urls.
         */
        String SYSTEM_PAYMENT_MODULES_URLS = "SYSTEM_PAYMENT_MODULES_URLS";
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
         * Default backdoor uri prefix.
         * Real example of attribute would be: SYSTEM_BACKDOOR_URI_YES0
         */
        String SYSTEM_BACKDOOR_URI_PREFIX = "SYSTEM_BACKDOOR_URI";
        /**
         * Default cache director uri.
         * Real example of attribute would be: SYSTEM_CACHEDIRECTOR_URI_YES0
         */
        String SYSTEM_CACHEDIRECTOR_URI_PREFIX = "SYSTEM_CACHEDIRECTOR_URI";
        /**
         * Default lucene index disabled setting.
         * Real example of attribute would be: SYSTEM_LUCENE_DISABLED_PREFIX_YES0
         */
        String SYSTEM_LUCENE_INDEX_DISABLED_PREFIX = "SYSTEM_LUCENE_DISABLED_PREFIX";
        /**
         * Size of the log tail in characters to show in YUM.
         */
        String IMPORT_JOB_LOG_SIZE = "IMPORT_JOB_LOG_SIZE";
        /**
         * Import job timeout (if ping takes more than this value then the job is considered haulted).
         */
        String IMPORT_JOB_TIMEOUT_MS = "IMPORT_JOB_TIMEOUT_MS";
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
         * The absolute path to import descriptors.
         */
        String SYSTEM_IMPORT_DESCRIPTORS = "SYSTEM_IMPORT_DESCRIPTORS";
        /**
         * The absolute path to archive folder where import files will be moved after import.
         */
        String SYSTEM_IMPORT_ARCHIVE = "SYSTEM_IMPORT_ARCHIVE";
        /**
         * The absolute path to import folder.
         */
        String SYSTEM_IMPORT = "SYSTEM_IMPORT";
        /**
         * Default (failover) directory for resources.
         */
        String SYSTEM_MAILTEMPLATES_FSPOINTER = "SYSTEM_MAILTEMPLATES_FSPOINTER";
    }


    interface Category {

        String CATEGORY_ITEMS_PER_PAGE = "CATEGORY_ITEMS_PER_PAGE";

        String CATEGORY_SUBCATEGORIES_COLUMNS = "CATEGORY_SUBCATEGORIES_COLUMNS";

        String CATEGORY_ITEMS_FEATURED = "CATEGORY_ITEMS_FEATURED";

        String CATEGORY_ITEMS_NEW_ARRIVAL = "CATEGORY_ITEMS_NEW_ARRIVAL";

        /**
         * Category image attribute name prefix
         */
        String CATEGORY_IMAGE_PREFIX = "CATEGORY_IMAGE";

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

        /** Category images width and height.  */
        String CATEGORY_IMAGE_WIDTH = "CATEGORY_IMAGE_WIDTH";
        String CATEGORY_IMAGE_HEIGHT = "CATEGORY_IMAGE_HEIGHT";

        /** Product image width and height in category.  */
        String PRODUCT_IMAGE_WIDTH = "PRODUCT_IMAGE_WIDTH";
        String PRODUCT_IMAGE_HEIGHT = "PRODUCT_IMAGE_HEIGHT";

        /** Product image width and height in category.  */
        String PRODUCT_IMAGE_TUMB_WIDTH = "PRODUCT_IMAGE_TUMB_WIDTH";
        String PRODUCT_IMAGE_TUMB_HEIGHT = "PRODUCT_IMAGE_TUMB_HEIGHT";

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
    }

    interface Shop {

        /**
         * Admin email in particular shop.
         */
        String SHOP_ADMIN_EMAIL = "SHOP_ADMIN_EMAIL";

        /**
         * The absolute path to import folder.
         */
        String SHOP_B2B = "SHOP_B2B";

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
         * Supported by shop storefront languages.
         */
        String SUPPORTED_LANGUAGES = "SUPPORTED_LANGUAGES";

        /**
         * Comma separated list of active payment gateways.
         */
        String SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL = "SHOP_ACTIVE_PAYMENT_GATEWAYS_LABEL";


        /**
         * Shop image attribute name prefix.
         */
        String SHOP_IMAGE_PREFIX = "SHOP_IMAGE";

        /**
         * Default Shop image.
         */
        String SHOP_IMAGE = SHOP_IMAGE_PREFIX + "0";

        /**
         * Setting for whether to show quantity picker or not
         */
        String CART_ADD_ENABLE_QTY_PICKER = "CART_ADD_ENABLE_QTY_PICKER";

        /**
         * Flag whether to include subcategories in search
         */
        String SHOP_INCLUDE_SUBCATEGORIES_IN_SEARCH = "INCLUDE_SUBCATEGORIES_IN_SEARCH_SHOP";

        /**
         * Number of days for newarrival tag
         */
        String SHOP_NEW_ARRIVAL_DAYS_OFFSET = "SHOP_NEW_ARRIVAL_DAYS_OFFSET";

        /**
         * Setting for whether to show coupons or not
         */
        String CART_UPDATE_ENABLE_COUPONS = "SHOP_CHECKOUT_ENABLE_COUPONS";

        /**
         * Setting for whether to show order message or not
         */
        String CART_UPDATE_ENABLE_ORDER_MSG = "SHOP_CHECKOUT_ENABLE_ORDER_MSG";

    }

    interface Brand {

        /**
         * Brand image attribute name prefix.
         */
        String BRAND_IMAGE_PREFIX = "BRAND_IMAGE";

        /**
         * Default Brand image.
         */
        String BRAND_IMAGE = BRAND_IMAGE_PREFIX + "0";

    }


    /**
     * Default (failover) directory for resources.
     */
    String SEARCH_ITEMS_PER_PAGE = "SEARCH_ITEMS_PER_PAGE";

    /**
     * Customer phone.
     */
    String CUSTOMER_PHONE = "CUSTOMER_PHONE";

    /**
     * Customer has opted in for marketing contact.
     */
    String MARKETING_OPT_IN = "MARKETING_OPT_IN";

    /**
     * Shop mail from.
     */
    String SHOP_MAIL_FROM = "SHOP_MAIL_FROM";


}
