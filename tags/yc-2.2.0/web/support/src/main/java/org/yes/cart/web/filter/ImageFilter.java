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

package org.yes.cart.web.filter;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.yes.cart.constants.Constants;
import org.yes.cart.service.domain.ImageService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.image.ImageNameStrategy;
import org.yes.cart.util.ShopCodeContext;
import org.yes.cart.web.application.ApplicationDirector;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.Calendar;
import java.util.Date;

/**
 * ImageServlet responsible for get product or brand images
 * in requested size. All url like
 * imgrevault/*.[jpg | gif | etc]?w=width&h=height
 * will be served by this class image will be resized if
 * necessary in stored in file cache. File name must be in following
 * format seo_name_code_x where x is a-z suffix.
 * Folder store must be in following format
 * repository_folder/c/code/seo_name_code_[a-z]
 * resized
 * repository_folder/widthxheight/c/code/seo_name_code_[a-z]
 * <p/>
 * No need to use nio, becuse images will be cached
 * on apache or ngnix
 * <p/>
 * User: Igor Azarny iazarny@yahoo.com
 * Date: 2011-May-17
 * Time: 8:07:27 AM
 */
public class ImageFilter extends AbstractFilter implements Filter {

    private static final String ETAG = "ETag";

    private static final String IF_NONE_MATCH = "If-None-Match";

    private static final String IF_MODIFIED_SINCE = "If-Modified-Since";

    private static final String LAST_MODIFIED = "Last-Modified";

    private final MimetypesFileTypeMap fileTypeMap;

    private final ImageService imageService;

    private final SystemService systemService;

    private Integer etagExpiration = null;

    public ImageFilter(
            final ApplicationDirector applicationDirector,
            final ImageService imageService,
            final SystemService systemService) {
        super(applicationDirector);
        this.imageService = imageService;
        this.systemService = systemService;
        fileTypeMap = new MimetypesFileTypeMap();
        fileTypeMap.addMimeTypes("image/bmp bmp");
        fileTypeMap.addMimeTypes("application/x-shockwave-flash swf");
    }

    String getContentType(final String fileName) {
        return fileTypeMap.getContentType(fileName.toLowerCase());
    }

    /**
     * Get the system service.
     *
     * @return {@link SystemService}
     */
    private SystemService getSystemService() {
        return systemService;
    }

    /**
     * Get etag expiration timeout.
     *
     * @return etag expiration timeout.
     */
    private Integer getEtagExpiration() {
        if (etagExpiration == null) {
            etagExpiration = getSystemService().getEtagExpirationForImages();
        }
        return etagExpiration;
    }


    public void handleRequestInternal(final HttpServletRequest httpServletRequest,
                                      final HttpServletResponse httpServletResponse) throws ServletException, IOException {


        final String servletPath = URLDecoder.decode(httpServletRequest.getServletPath(), "UTF-8"); //this for filter
        //httpServletRequest.getPathInfo(); //this for servlet

        final String previousToken = httpServletRequest.getHeader(IF_NONE_MATCH);
        final String currentToken = getETagValue(httpServletRequest);

        httpServletResponse.setHeader(ETAG, currentToken);

        final Date modifiedDate = new Date(httpServletRequest.getDateHeader(IF_MODIFIED_SINCE));
        final Calendar calendar = Calendar.getInstance();
        final Date now = calendar.getTime();

        calendar.setTime(modifiedDate);
        calendar.add(Calendar.MINUTE, getEtagExpiration());
        if (currentToken.equals(previousToken) && (now.getTime() < calendar.getTime().getTime())) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_MODIFIED);
            // use the same date we sent when we created the ETag the first time through
            httpServletResponse.setHeader(LAST_MODIFIED, httpServletRequest.getHeader(IF_MODIFIED_SINCE));
            final Logger log = ShopCodeContext.getLog(this);
            if (log.isDebugEnabled()) {
                log.debug("ETag the same, will return 304");
            }
        } else {

            httpServletResponse.setDateHeader(LAST_MODIFIED, (new Date()).getTime());

            final String width = httpServletRequest.getParameter(Constants.WIDTH);
            final String height = httpServletRequest.getParameter(Constants.HEIGHT);


            final ImageNameStrategy imageNameStrategy = imageService.getImageNameStrategy(servletPath);

            String code = imageNameStrategy.resolveObjectCode(servletPath);  //optional product or sku code
            String locale = imageNameStrategy.resolveLocale(servletPath);  //optional locale
            String originalFileName = imageNameStrategy.resolveFileName(servletPath);  //here file name with prefix

            final String imageRealPathPrefix = getImageRepositoryRoot();

            String absolutePathToOriginal =
                            imageRealPathPrefix +
                            imageNameStrategy.resolveRelativeInternalFileNamePath(originalFileName, code, locale); //path to not resized image


            final boolean origFileExists = imageService.isImageInRepository(originalFileName, code, imageNameStrategy.getUrlPath(), imageRealPathPrefix);

            if (!origFileExists) {
                code = Constants.NO_IMAGE;
                originalFileName = imageNameStrategy.resolveFileName(code);  //here file name with prefix
                absolutePathToOriginal =
                        imageRealPathPrefix +
                                imageNameStrategy.resolveRelativeInternalFileNamePath(originalFileName, code, locale); //path to not resized image
            }


            String absolutePathToResized = null;
            if (width != null && height != null && imageService.isSizeAllowed(width, height)) {
                absolutePathToResized =
                        imageRealPathPrefix +
                                imageNameStrategy.resolveRelativeInternalFileNamePath(originalFileName, code, locale, width, height);
            }

            final byte[] imageFile = getImageFile(absolutePathToOriginal, absolutePathToResized, width, height);
            IOUtils.write(imageFile, httpServletResponse.getOutputStream());

        }
    }

    private String getImageRepositoryRoot() {

        return systemService.getImageRepositoryDirectory();

    }

    /**
     * Get the etag value for requested image. Atm implemented as hash code.
     *
     * @param httpServletRequest request
     * @return etag value.
     */
    private String getETagValue(final HttpServletRequest httpServletRequest) {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append('"');
        stringBuilder.append(httpServletRequest.getParameter(Constants.WIDTH));
        stringBuilder.append('x');
        stringBuilder.append(httpServletRequest.getParameter(Constants.HEIGHT));
        stringBuilder.append(httpServletRequest.getServletPath().hashCode());
        stringBuilder.append('"');
        return stringBuilder.toString();
    }

    /**
     * {@inheritDoc}
     */
    public ServletRequest doBefore(final ServletRequest servletRequest,
                                   final ServletResponse servletResponse) throws IOException, ServletException {
        handleRequestInternal(
                (HttpServletRequest) servletRequest,
                (HttpServletResponse) servletResponse);
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public void doAfter(final ServletRequest servletRequest,
                        final ServletResponse servletResponse) throws IOException, ServletException {
        //do nothing
    }

    byte[] getImageFile(final String absolutePathToOriginal,
                        final String absolutePathToResized,
                        final String width,
                        final String height) throws IOException {

        return imageService.resizeImage(absolutePathToOriginal, absolutePathToResized, width, height);

    }

}
