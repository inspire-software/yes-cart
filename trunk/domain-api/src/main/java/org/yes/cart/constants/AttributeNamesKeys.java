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
        String SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABELS = "SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABELS";
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
    }


    interface Category {

        String CATEGORY_ITEMS_PER_PAGE = "CATEGORY_ITEMS_PER_PAGE";

        String CATEGORY_SUBCATEGORIES_COLUMNS = "CATEGORY_SUBCATEGORIES_COLUMNS";

        String CATEGORY_ITEMS_FEATURED = "CATEGORY_ITEMS_FEATURED";

        String CATEGORY_ITEMS_NEW_ARRIVAL = "CATEGORY_ITEMS_NEW_ARRIVAL";

        /**
         * Category image attribute name
         */
        String CATEGORY_IMAGE = "CATEGORY_IMAGE";


        /**
         * Label of category image retreive strategy. Allowed values:
         * ATTRIBUTE
         * RANDOM_PRODUCT
         */
        String CATEGORY_IMAGE_RETRIEVE_STRATEGY = "CATEGORY_IMAGE_RETRIEVE_STRATEGY";

        /**
         * Default strategy
         */
        String CATEGORY_IMAGE_DEFAULT_RETREIVE_STRATEGY = "RANDOM_PRODUCT";

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
         * full attribute name. E.g. CATEGORY_DESCRITION_en.
         */
        String CATEGORY_DESCRIPTION_PREFIX = "CATEGORY_DESCRITION_";


    }

    interface Product {

        /**
         * Product description attribute prefix. Requires current language to make
         * full attribute name. E.g. PRODUCT_DESCRITION_en.
         */
        String PRODUCT_DESCRIPTION_PREFIX = "PRODUCT_DESCRITION_";

        /**
         * Product SKU description attribute prefix. Requires current language to make
         * full attribute name. E.g. SKUPRODUCT_DESCRITION_en.
         */
        String SKUPRODUCT_DESCRIPTION_PREFIX = "SKUPRODUCT_DESCRITION_";


    }

    interface Shop {

        /**
         * Admin email in particular shop.
         */
        String SHOP_ADMIN_EMAIL = "SHOP_ADMIN_EMAIL";

    }


    /**
     * Default (failover) directory for resources.
     */
    String SEARCH_ITEMS_PER_PAGE = "SEARCH_ITEMS_PER_PAGE";

    /**
     * Supported by shop currencies.
     */
    String SUPPORTED_CURRENCIES = "CURRENCY";


    String BRAND_IMAGE = "BRAND_IMAGE";


    /**
     * Default (failover) directory for resources.
     */
    String SYSTEM_MAILTEMPLATES_FSPOINTER = "SYSTEM_MAILTEMPLATES_FSPOINTER";


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

    /**
     * The absolute path to import folder.
     */
    String SHOP_B2B = "SHOP_B2B";


}
