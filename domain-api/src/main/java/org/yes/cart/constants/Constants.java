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

    String DEFAULT_DATE_TIME_FORMAT = "yyyy-MMM-dd hh:mm:ss";

    String DEFAULT_DATE_FORMAT = "yyyy-MMM-dd";

    /**
     * Accourding to HTTP/1.1 (RFC2068) spec date time must be in  RFC850 format.
     * <p/>
     * <p/>
     * HTTP приложения исторически допускали три различных формата для
     * представления даты/времени:
     * <p/>
     * Sun, 06 Nov 1994 08:49:37 GMT  ; RFC 822, дополненный в RFC 1123
     * Sunday, 06-Nov-94 08:49:37 GMT ; RFC 850, переписанный как RFC 1036
     * Sun Nov  6 08:49:37 1994       ; формат asctime() ANSI C
     * <p/>
     * Первый формат выбран в качестве стандарта Интернета и представляет
     * подмножество фиксированной длины, как определено в RFC 1123
     * (модифицированном RFC 822)
     */
    String RFC850_DATE_TIME_FORMAT = "EEE, dd MMM yyyy HH:mm:ss z";

    /**
     * Money in minor money units. For example
     * 12 dollars and 34 cent will be 1234 cents.
     */
    String MONEY_FORMAT_TOINDEX = "00000000";
    String MONEY_FORMAT = "######.00";
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
     * Default image attribute name.
     */
    String PRODUCT_DEFAULT_IMAGE_ATTR_NAME = "IMAGE0";

    /**
     * Default image attribute name.
     */
    String PRODUCT_SKU_DEFAULT_IMAGE_ATTR_NAME = "SKUIMAGE0";

    /**
     * Default image attribute name.
     */
    String PRODUCT_IMAGE_ATTR_NAME_PREFIX = "IMAGE";

    /**
     * Default image attribute name.
     */
    String PRODUCT_SKU_IMAGE_ATTR_NAME_PREFIX = "SKUIMAGE";


    /**
     * Image height.
     */
    String HEIGHT = "h";
    /**
     * Image width.
     */
    String WIDTH = "w";


    String CATEGOTY_IMAGE_REPOSITORY_URL_PATTERN = "/imgvault/category/";
    String BRAND_IMAGE_REPOSITORY_URL_PATTERN = "/imgvault/brand/";
    String PRODUCT_IMAGE_REPOSITORY_URL_PATTERN = "/imgvault/";


    String CATEGOTY_IMAGE_FILE_PREFIX = "category";
    String BRAND_IMAGE_FILE_PREFIX = "brand";


    /**
     * Items quantity on page. In case if default value in root category not set
     * the default values will be used.
     */
    List<String> DEFAULT_ITEMS_ON_PAGE = Arrays.asList("10,20,30".split(","));


    long ROOT_CATEGORY_ID = 100L;


}
