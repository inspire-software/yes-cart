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

import java.util.Arrays;
import java.util.List;

/**
 * Different system constants.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:12:54
 */
@SuppressWarnings("PMD.AvoidConstantsInterface")
public interface Constants {

    /**
     * Default money scale
     */
    int MONEY_SCALE = 2;

    /**
     * Default inventory scale
     */
    int INVENTORY_SCALE = 3;

    /**
     * Default decimal scale
     */
    int DEFAULT_SCALE = 2;


    /**
     * No image file name.
     */
    String NO_IMAGE = "noimage.jpeg";

    /**
     * No file file name.
     */
    String NO_FILE = "nofile.txt";


    /**
     * Image height.
     */
    String HEIGHT = "h";
    /**
     * Image width.
     */
    String WIDTH = "w";

    /**
     * Default thumb size
     */
    String[] DEFAULT_THUMB_SIZE = new String[] { "80", "80" };

    /**
     * Default product image size
     */
    String[] DEFAULT_PRODUCTLIST_IMAGE_SIZE = new String[] { "280", "280" };

    /**
     * Default product image size
     */
    String[] DEFAULT_CATEGORYLIST_IMAGE_SIZE = new String[] { "280", "280" };

    /**
     * Default limit of featured panel
     */
    int FEATURED_LIST_SIZE = 15;

    /**
     * Default limit of recommendation panels (newarrival, recently viewed)
     */
    int RECOMMENDATION_SIZE = 5;

    /**
     * Default product count in one row on category and search pages
     */
    int PRODUCT_COLUMNS_SIZE = 2;

    /**
     * Default sub categories count in one row on category and search pages
     */
    int SUBCATEGORIES_COLUMNS_SIZE = 2;

    String CATEGORY_IMAGE_REPOSITORY_URL_PATTERN    = "/imgvault/category/";
    String BRAND_IMAGE_REPOSITORY_URL_PATTERN       = "/imgvault/brand/";
    String CUSTOMER_IMAGE_REPOSITORY_URL_PATTERN    = "/imgvault/customer/";
    String PRODUCT_IMAGE_REPOSITORY_URL_PATTERN     = "/imgvault/product/";
    String SHOP_IMAGE_REPOSITORY_URL_PATTERN        = "/imgvault/shop/";
    String SYSTEM_IMAGE_REPOSITORY_URL_PATTERN      = "/imgvault/system/";

    String CATEGORY_FILE_REPOSITORY_URL_PATTERN    = "/filevault/category/";
    String BRAND_FILE_REPOSITORY_URL_PATTERN       = "/filevault/brand/";
    String CUSTOMER_FILE_REPOSITORY_URL_PATTERN    = "/filevault/customer/";
    String PRODUCT_FILE_REPOSITORY_URL_PATTERN     = "/filevault/product/";
    String SHOP_FILE_REPOSITORY_URL_PATTERN        = "/filevault/shop/";
    String SYSTEM_FILE_REPOSITORY_URL_PATTERN      = "/filevault/system/";

    String CATEGORY_SYSFILE_REPOSITORY_URL_PATTERN    = "/sysfilevault/category/";
    String BRAND_SYSFILE_REPOSITORY_URL_PATTERN       = "/sysfilevault/brand/";
    String CUSTOMER_SYSFILE_REPOSITORY_URL_PATTERN    = "/sysfilevault/customer/";
    String PRODUCT_SYSFILE_REPOSITORY_URL_PATTERN     = "/sysfilevault/product/";
    String SHOP_SYSFILE_REPOSITORY_URL_PATTERN        = "/sysfilevault/shop/";
    String SYSTEM_SYSFILE_REPOSITORY_URL_PATTERN      = "/sysfilevault/system/";

    /**
     * Items quantity on page. In case if default value in root category not set
     * the default values will be used.
     */
    List<String> DEFAULT_ITEMS_ON_PAGE = Arrays.asList("10", "20", "30");

    /**
     * Sort options on page. In case if default value in root category not set
     * the default values will be used.
     */
    List<String> DEFAULT_PAGE_SORT = Arrays.asList("displayName", "sku", "basePrice");

    /**
     * Default customer password reset token expiry.
     */
    int DEFAULT_CUSTOMER_TOKEN_EXPIRY_SECONDS = 86400;

    /**
     * Constant to determine unsuccessful reset password command
     */
    String PASSWORD_RESET_AUTH_TOKEN_INVALID = "PASSWORD_RESET_AUTH_TOKEN_INVALID";

    /**
     * Delimiter for range navigation value. Need to select this value carefully so that
     * parameter parser can distinguish between range and single values.
     */
    String RANGE_NAVIGATION_DELIMITER = "-_-";

    /**
     * Delimiter for facet navigation display value.
     */
    String FACET_NAVIGATION_DELIMITER = "|||";

    /**
     * Number precision to use. All decimals need to be converted to long to allow for long point
     * range queries. Therefore with this precision the decimal point is moved to right and then
     * the value is rounded to get the long.
     *
     * E.g. 1 => 1000, 2.33 => 2330, 3.3333 => 3333 and 3.3336 => 3334
     */
    int NUMERIC_NAVIGATION_PRECISION = 3;

    /** Root category PK. */
    long ROOT_CATEGORY_ID = 100L;

    /**
     * Default category filter nav limit
     */
    int CATEGORY_FILTERNAV_LIMIT = 25;

}
