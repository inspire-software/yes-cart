package org.yes.cart.service.image.impl;

import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.image.ImageNameStrategy;

import java.io.File;

/**
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 09-May-2011
 * Time: 14:12:54
 */
public abstract class AbstractImageNameStrategyImpl implements ImageNameStrategy {


    /**
     * Get the image path prefix ended with {@see File#separator}
     *
     */
    protected abstract String getPathPrefix();


    /**
     * Get the file name from url with particular prefix.
     * Prefix - part of path to image repository and depends from
     * partucular strategy. i.e.
     * category will return /category/
     * brand woll return /brand/
     *
     * @param url fiven url
     * @return image file name.
     */
    public String getFileName(final String url) {

        final File file = new File(url);
        String fileName = file.getName();
        int idx = fileName.indexOf('?');
        if (idx > -1) {
            fileName = fileName.substring(0, idx);
        }
        return fileName;
    }

    /**
     * Get the file name from url.
     *
     * @param url fiven url
     * @return image file name.
     */

    protected String getFileNameWithoutPrefix(final String url) {

        final File file = new File(url);
        String fileName = file.getName();
        int idx = fileName.indexOf('?');
        if (idx > -1) {
            fileName = fileName.substring(0, idx);
        }
        return fileName;

    }


    /**
     * Get the file name in image repository.
     *
     * @param fileName file name without the full path
     * @param code     product code
     * @return full name with path to file.
     */
    public String getFullFileNamePath(final String fileName, final String code) {
        return getFullFileNamePath(fileName, code, null, null);
    }


    /**
     * Get the file name of resized in image repository.
     *
     * @param fileName file name without the full path
     * @param code     product code
     * @param width    image width
     * @param height   image height
     * @return full name with path to file.
     */
    public String getFullFileNamePath(final String fileName, final String code, final String width, final String height) {
        final StringBuilder stringBuilder = new StringBuilder();
        if (width != null) {
            stringBuilder.append(width);
            stringBuilder.append('x');
            stringBuilder.append(height);
            stringBuilder.append(File.separator);
        }
        if (code != null) {
            stringBuilder.append(code.charAt(0));
            stringBuilder.append(File.separator);
            stringBuilder.append(code);
            stringBuilder.append(File.separator);
        }

        stringBuilder.append(fileName);
        return stringBuilder.toString();
    }

}
