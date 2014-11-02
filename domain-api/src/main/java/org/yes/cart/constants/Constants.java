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
     * This is the import date format for csv import.
     * Example: 2014-01-24 16:54:00
     * We used short form format to prevent localisation issues in csv. All parts of date time imports are numbers.
     */
    String DEFAULT_IMPORT_DATE_TIME_FORMAT = "yyyy-MM-dd hh:mm:ss";

    String DEFAULT_DATE_FORMAT = "dd MMMMM yyyy";

    /**
     * According to HTTP/1.1 (RFC2068) spec date time must be in  RFC850 format.
     * <p/>
     * <p/>
     * HTTP application historically accepted three formats to represent date/time:
     * <p/>
     * Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, enhanced in RFC 1123
     * Sunday, 06-Nov-94 08:49:37 GMT ; RFC 850, rewritten as RFC 1036
     * Sun Nov  6 08:49:37 1994       ; format asctime() ANSI C
     * <p/>
     * The first format is chosen as Internet standard and represents fixed
     * length string as stated in RFC 1123 (modified RFC 822)
     */
    String RFC850_DATE_TIME_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    /**
     * Money in minor money units. For example
     * 12 dollars and 34 cent will be 1234 cents.
     */
    String MONEY_FORMAT_TOINDEX = "00000000";
    String MONEY_FORMAT = "#####0.00";
    int MONEY_SCALE = 2;

    /**
     * Inventory in minor units. For example
     * 12.345 meter of fabric would be 12345.
     * 1 item would be 1000
     */
    String INVENTORY_FORMAT_TOINDEX = "00000000";
    String INVENTORY_FORMAT = "#####0.000";
    int INVENTORY_SCALE = 3;

    /**
     * Default decimal scale
     */
    int DEFAULT_SCALE = 2;


    /**
     * For price filtered navigation expected only whole digits.
     */
    String MONEY_FORMAT_PRICE_NAVIGATION = "########";

    /**
     * No image file name.
     */
    String NO_IMAGE = "noimage.jpeg";


    /**
     * Image height.
     */
    String HEIGHT = "h";
    /**
     * Image width.
     */
    String WIDTH = "w";


    String CATEGORY_IMAGE_REPOSITORY_URL_PATTERN    = "/imgvault/category/";
    String BRAND_IMAGE_REPOSITORY_URL_PATTERN       = "/imgvault/brand/";
    String PRODUCT_IMAGE_REPOSITORY_URL_PATTERN     = "/imgvault/product/";
    String SHOP_IMAGE_REPOSITORY_URL_PATTERN        = "/imgvault/shop/";

    /**
     * Items quantity on page. In case if default value in root category not set
     * the default values will be used.
     */
    List<String> DEFAULT_ITEMS_ON_PAGE = Arrays.asList("10", "20", "30");


    long ROOT_CATEGORY_ID = 100L;

}
