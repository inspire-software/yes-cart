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

        /**
         * Category image attribute name
         */
        String CATEGORY_IMAGE = "CATEGORY_IMAGE";


        /**
         * Label of category image retreive strategy. Allowed values:
         * ATTRIBUTE
         * RANDOM_PRODUCT
         */
        String CATEGORY_IMAGE_RETREIVE_STRATEGY = "CATEGORY_IMAGE_RETREIVE_STRATEGY";

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


    }

    interface Product {

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
     * Customer phone.
     */
    String MARKETING_MAIL_ALLOWED = "MARKETING_MAIL_ALLOWED";

    /**
     * Shop mail from.
     */
    String SHOP_MAIL_FROM = "SHOP_MAIL_FROM";

    /**
     * The absolute path to import folder.
     */
    String SHOP_B2B = "SHOP_B2B";


}
