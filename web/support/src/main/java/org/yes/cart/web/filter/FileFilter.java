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

package org.yes.cart.web.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.domain.FileService;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.media.MediaFileNameStrategy;
import org.yes.cart.web.support.util.HttpUtil;

import javax.activation.MimetypesFileTypeMap;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * FileServlet responsible for get product or brand files.
 * <p/>
 * User: Denis Pavlov
 */
public class FileFilter extends AbstractFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(FileFilter.class);

    private final MimetypesFileTypeMap fileTypeMap;

    private final FileService fileService;

    private final SystemService systemService;

    public FileFilter(final FileService fileService,
                      final SystemService systemService) {
        this.fileService = fileService;
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


    public void handleRequestInternal(final HttpServletRequest httpServletRequest,
                                      final HttpServletResponse httpServletResponse) throws ServletException, IOException {

        final String requestPath = HttpUtil.decodeUtf8UriParam(httpServletRequest.getRequestURI());           // RequestURI  -> /yes-shop/imagevault/product/image.png
        final String contextPath = httpServletRequest.getContextPath();                                       // ContextPath -> /yes-shop
        final String servletPath = requestPath.substring(contextPath.length());                               // ServletPath ->          /imagevault/product/image.png

        final MediaFileNameStrategy mediaFileNameStrategy = fileService.getFileNameStrategy(servletPath);

        String code = mediaFileNameStrategy.resolveObjectCode(servletPath);  //optional product or sku code
        String originalFileName = mediaFileNameStrategy.resolveFileName(servletPath);  //here file name with prefix

        final String fileRealPathPrefix = getFileRepositoryRoot();

        final boolean origFileExists = fileService.isFileInRepository(originalFileName, code, mediaFileNameStrategy.getUrlPath(), fileRealPathPrefix);

        if (!origFileExists) {
            httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
        } else {
            final byte[] file = fileService.fileToByteArray(originalFileName, code, mediaFileNameStrategy.getUrlPath(), fileRealPathPrefix);
            if (file != null && file.length > 0) {
                httpServletResponse.getOutputStream().write(file);
                httpServletResponse.flushBuffer();
            } else {
                httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        }
    }

    private String getFileRepositoryRoot() {

        return systemService.getFileRepositoryDirectory();

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

}
