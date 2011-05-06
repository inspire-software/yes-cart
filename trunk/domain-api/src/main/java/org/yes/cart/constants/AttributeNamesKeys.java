package org.yes.cart.constants;

/**
 * Build in attribute names.
 *
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
public interface AttributeNamesKeys {

    /**
     * Comma separated list of paymenе modules urls.
     */
    final static String SYSTEM_PAYMENT_MODULES_URLS = "SYSTEM_PAYMENT_MODULES_URLS";

    /**
     * Comma separated list of active payment gateways.
     */
    final static String SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABELS = "SYSTEM_ACTIVE_PAYMENT_GATEWAYS_LABELS";

    /**
     * Etag expiration timeout for images.
     */
    final static String SYSTEM_ETAG_CACHE_IMAGES_TIME = "SYSTEM_ETAG_CACHE_IMAGES_TIME";

    /**
     * Etag expiration timeout for pages.
     */
    final static String SYSTEM_ETAG_CACHE_PAGES_TIME = "SYSTEM_ETAG_CACHE_PAGES_TIME";


    /**
     * Default shop to redirect.
     */
    final static String SYSTEM_DEFAULT_SHOP = "SYSTEM_DEFAULT_SHOP";

    /**
     * Default (failover) directory for resources.
     */
    final static String SYSTEM_DEFAULT_FSPOINTER = "SYSTEM_DEFAULT_FSPOINTER";

    /**
     * Image repository.
     */
    final static String SYSTEM_IMAGE_VAULT = "SYSTEM_IMAGE_VAULT";

    /**
     * The absolute path to import descriptors.
     */
    final static String SYSTEM_IMPORT_DESCRIPTORS = "SYSTEM_IMPORT_DESCRIPTORS";

    /**
     * The absolute path to archive folder where import files will be moved after import.
     */
    final static String SYSTEM_IMPORT_ARCHIVE = "SYSTEM_IMPORT_ARCHIVE";

    /**
     * The absolute path to import folder.
     */
    final static String SYSTEM_IMPORT = "SYSTEM_IMPORT";


    /**
     * Default (failover) directory for resources.
     */
    final static String SEARCH_ITEMS_PER_PAGE = "SEARCH_ITEMS_PER_PAGE";

    /**
     * Supported by shop currensies.
     */
    final static String SUPPORTED_CURRENSIES = "CURRENCY";


    public final static String BRAND_IMAGE = "BRAND_IMAGE";


    public final static String CATEGORY_ITEMS_PER_PAGE = "CATEGORY_ITEMS_PER_PAGE";

    public final static String CATEGORY_SUBCATEGORIES_COLUMNS = "CATEGORY_SUBCATEGORIES_COLUMNS";

    /**
     * Label of category image retreive strategy. Allowed values:
     * ATTRIBUTE
     * RANDOM_PRODUCT
     */
    public final static String CATEGORY_IMAGE_RETREIVE_STRATEGY = "CATEGORY_IMAGE_RETREIVE_STRATEGY";


    /**
     * Default (failover) directory for resources.
     */
    final static String SYSTEM_MAILTEMPLATES_FSPOINTER = "SYSTEM_MAILTEMPLATES_FSPOINTER";


    /**
     * Customer phone.
     */
    final static String CUSTOMER_PHONE = "CUSTOMER_PHONE";

    /**
     * Shop mail from.
     */
    final static String SHOP_MAIL_FROM = "SHOP_MAIL_FROM";

    /**
     * The absolute path to import folder.
     */
    final static String SHOP_B2B = "SHOP_B2B";


}
