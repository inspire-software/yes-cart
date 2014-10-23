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

package org.yes.cart.service.image;

/**
 * Strategy to getByKey the file names for store/retrieve images from image repository.
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 07-May-2011
 * Time: 11:13:01
 */
public interface ImageNameStrategy {

    /**
     * URL path that identifies this type of strategy for filename resolution.
     *
     * @return url path
     */
    String getUrlPath();

    /**
     * Get the internal image relative path root directory ended with {@see File#separator}. E.g. "category/"
     *
     * @return directory name
     */
    String getRelativeInternalRootDirectory();

    /**
     * Get the file name from url with particular prefix.
     * Prefix - part of path to image repository.
     *
     * @param url given url
     * @return image file name.
     */
    String resolveFileName(final String url);

    /**
     * Get the object code for given url.
     *
     * For sku or product this will be SKU code
     * For categories will be name
     * For brand will be name
     *
     * @param url filename
     * @return code for given image name or null if particular strategy does not support code.
     */
    String resolveObjectCode(String url);

    /**
     * Get locale for given url
     *
     * Locale must correspond to available locales provided by LanguageService
     *
     * @param url filename
     * @return locale for given image name or null if no locale information can be extracted
     */
    String resolveLocale(String url);

    /**
     * Get suffix (a-z) for given url
     *
     * @param url filename
     * @return "0"-"26" suffix
     */
    String resolveSuffix(String url);

    /**
     * Get the file name in image repository.
     *
     *
     * @param fileName file name without the full path
     * @param code     product or sku  code
     * @param locale   locale information or null
     * @return full name with path to file.
     */
    String resolveRelativeInternalFileNamePath(String fileName, String code, String locale);

    /**
     * Get the file name of re-sized in image repository.
     *
     *
     * @param fileName file name without the full path
     * @param code     product code
     * @param locale   locale information or null
     * @param width    image width
     * @param height   image height   @return full name with path to file.
     */
    String resolveRelativeInternalFileNamePath(String fileName, String code, final String locale, String width, String height);

    /**
     * Create rolling name for given filename with given code and locale.
     *
     *
     * @param fileName full filename
     * @param code     object code
     * @param suffix   suffix
     * @param locale   locale
     *  @return rolling file name (e.g. name-1_code_a.jpg -> name-2_code_a.jpg)
     */
    String createRollingFileName(String fileName, String code, final String suffix, final String locale);

}
