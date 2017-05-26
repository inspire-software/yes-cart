/*
 * Copyright 2009 - 2016 Denys Pavlov, Igor Azarnyi
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

package org.yes.cart.service.vo.impl;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yes.cart.service.domain.SystemService;
import org.yes.cart.service.dto.DtoFileService;
import org.yes.cart.service.dto.DtoImageService;
import org.yes.cart.service.vo.VoIOSupport;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: denispavlov
 * Date: 07/08/2016
 * Time: 23:11
 */
public class VoIOSupportImpl implements VoIOSupport {

    private static final Logger LOG = LoggerFactory.getLogger(VoIOSupportImpl.class);

    private final DtoImageService dtoImageService;
    private final DtoFileService dtoFileService;
    private final SystemService systemService;


    public VoIOSupportImpl(final DtoImageService dtoImageService,
                           final DtoFileService dtoFileService,
                           final SystemService systemService) {
        this.dtoImageService = dtoImageService;
        this.dtoFileService = dtoFileService;
        this.systemService = systemService;
    }


    /**
     * {@inheritDoc}
     */
    public String addFileToRepository(final String fileName,
                                      final String code,
                                      final String attributeFileCode,
                                      final String base64URL,
                                      final String storagePrefix) throws IOException {

        final String path = systemService.getFileRepositoryDirectory();

        final byte[] fileBody = getByteArray(base64URL);
        if (fileBody == null || fileBody.length == 0) {
            throw new IOException("Unable to read BASE64 file");
        }

        return dtoFileService.addFileToRepository(
                ensureCorrectFileName(fileName, code, attributeFileCode),
                code, fileBody, storagePrefix, path
        );

    }


    /**
     * {@inheritDoc}
     */
    public String addSystemFileToRepository(final String fileName,
                                            final String code,
                                            final String attributeFileCode,
                                            final String base64URL,
                                            final String storagePrefix) throws IOException {

        final String path = systemService.getSystemFileRepositoryDirectory();

        final byte[] fileBody = getByteArray(base64URL);
        if (fileBody == null || fileBody.length == 0) {
            throw new IOException("Unable to read BASE64 file");
        }

        return dtoFileService.addFileToRepository(
                ensureCorrectFileName(fileName, code, attributeFileCode),
                code, fileBody, storagePrefix, path
        );

    }

    /**
     * {@inheritDoc}
     */
    public String addImageToRepository(final String fileName,
                                       final String code,
                                       final String attributeImageCode,
                                       final String base64URL,
                                       final String storagePrefix) throws IOException {

        final String path = systemService.getImageRepositoryDirectory();

        final byte[] imgBody = getByteArray(base64URL);
        if (imgBody == null || imgBody.length == 0) {
            throw new IOException("Unable to read BASE64 image");
        }

        return dtoImageService.addImageToRepository(
                ensureCorrectFileName(fileName, code, attributeImageCode),
                code, imgBody, storagePrefix, path
        );

    }

    private static final Pattern MEDIA_ATTR_PATTERN = Pattern.compile("^([^\\d]*)(\\d+)(_[a-z]{2})?$");

    static String ensureCorrectFileName(final String fileName, final String objectCode, final String attributeImageCode) {

        String formatted = fileName;
        if (StringUtils.isNotBlank(objectCode)) {

            final String codePlaceholder = "_" + objectCode + "_";


            if (fileName.contains(codePlaceholder)) {

                final int placeholderPos = fileName.lastIndexOf(codePlaceholder);
                final int extPos = fileName.lastIndexOf('.');
                String name = fileName.substring(0, extPos).toLowerCase();
                name = name.substring(0, placeholderPos);
                final String ext = fileName.substring(extPos + 1).toLowerCase();

                final String suffix = determineFileSuffix(attributeImageCode);

                formatted = name
                        + "_"
                        + objectCode
                        + "_"
                        + suffix
                        + "."
                        + ext;

            } else {

                final int extPos = fileName.lastIndexOf('.');
                final String name = fileName.substring(0, extPos).toLowerCase();
                final String ext = fileName.substring(extPos + 1).toLowerCase();

                final String suffix = determineFileSuffix(attributeImageCode);

                formatted = name
                        + "_"
                        + objectCode
                        + "_"
                        + suffix
                        + "."
                        + ext;
            }

        }
        return formatted.replace(' ', '-');
    }

    private static String determineFileSuffix(final String attributeImageCode) {
        final String suffix;
        final Matcher match = MEDIA_ATTR_PATTERN.matcher(attributeImageCode);
        if (match.matches()) {
            if (match.group(3) != null) {
                // with language
                suffix = (char)('a' + Integer.valueOf(match.group(2))) + match.group(3);
            } else {
                suffix = (char)('a' + Integer.valueOf(match.group(2))) + "";
            }
        } else {
            suffix = "a"; // default
        }
        return suffix;
    }

    static byte[] getByteArray(final String base64) {
        final String[] parts = StringUtils.splitByWholeSeparator(base64, ";base64,");
        if (parts.length == 1) {
            return Base64.decodeBase64(parts[0]);
        } else if (parts.length == 2) {
            return Base64.decodeBase64(parts[1]);
        }
        return null;
    }


    /**
     * {@inheritDoc}
     */
    public String getImageAsBase64(final String fileName,
                                   final String code,
                                   final String storagePrefix) {

        final String path = systemService.getImageRepositoryDirectory();

        try {
            final byte[] content = dtoImageService.getImageAsByteArray(fileName, code, storagePrefix, path);

            final String contentType = getMimeType(fileName);

            return getBase64String(contentType, content);
        } catch (Exception exp) {
            LOG.error("Error retrieving image data for " + code + "/" + fileName + ", caused by: " + exp.getMessage(), exp);
            return null;
        }
    }

    static String getBase64String(final String contentType, byte[] content) {
        final StringBuilder out = new StringBuilder();
        out.append("data:").append(contentType).append(";base64,").append(Base64.encodeBase64String(content));
        return out.toString();
    }


    static String getMimeType(String fileName) {

        final FileNameMap mimeTypes = URLConnection.getFileNameMap();
        final String contentType = mimeTypes.getContentTypeFor(fileName);

        // 2. nothing found -> lookup our in extension map to find types like ".doc" or ".docx"
        if (contentType == null) {
            final String extension = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());;
            return EXT_MIMETYPE_MAP.get(extension);
        }
        return contentType;
    }

    private static final Map<String, String> EXT_MIMETYPE_MAP = new HashMap<String, String>() {{
        // MS Office
        put("doc", "application/msword");
        put("dot", "application/msword");
        put("docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        put("dotx", "application/vnd.openxmlformats-officedocument.wordprocessingml.template");
        put("docm", "application/vnd.ms-word.document.macroEnabled.12");
        put("dotm", "application/vnd.ms-word.template.macroEnabled.12");
        put("xls", "application/vnd.ms-excel");
        put("xlt", "application/vnd.ms-excel");
        put("xla", "application/vnd.ms-excel");
        put("xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        put("xltx", "application/vnd.openxmlformats-officedocument.spreadsheetml.template");
        put("xlsm", "application/vnd.ms-excel.sheet.macroEnabled.12");
        put("xltm", "application/vnd.ms-excel.template.macroEnabled.12");
        put("xlam", "application/vnd.ms-excel.addin.macroEnabled.12");
        put("xlsb", "application/vnd.ms-excel.sheet.binary.macroEnabled.12");
        put("ppt", "application/vnd.ms-powerpoint");
        put("pot", "application/vnd.ms-powerpoint");
        put("pps", "application/vnd.ms-powerpoint");
        put("ppa", "application/vnd.ms-powerpoint");
        put("pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation");
        put("potx", "application/vnd.openxmlformats-officedocument.presentationml.template");
        put("ppsx", "application/vnd.openxmlformats-officedocument.presentationml.slideshow");
        put("ppam", "application/vnd.ms-powerpoint.addin.macroEnabled.12");
        put("pptm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
        put("potm", "application/vnd.ms-powerpoint.presentation.macroEnabled.12");
        put("ppsm", "application/vnd.ms-powerpoint.slideshow.macroEnabled.12");
        // Open Office
        put("odt", "application/vnd.oasis.opendocument.text");
        put("ott", "application/vnd.oasis.opendocument.text-template");
        put("oth", "application/vnd.oasis.opendocument.text-web");
        put("odm", "application/vnd.oasis.opendocument.text-master");
        put("odg", "application/vnd.oasis.opendocument.graphics");
        put("otg", "application/vnd.oasis.opendocument.graphics-template");
        put("odp", "application/vnd.oasis.opendocument.presentation");
        put("otp", "application/vnd.oasis.opendocument.presentation-template");
        put("ods", "application/vnd.oasis.opendocument.spreadsheet");
        put("ots", "application/vnd.oasis.opendocument.spreadsheet-template");
        put("odc", "application/vnd.oasis.opendocument.chart");
        put("odf", "application/vnd.oasis.opendocument.formula");
        put("odb", "application/vnd.oasis.opendocument.database");
        put("odi", "application/vnd.oasis.opendocument.image");
        put("oxt", "application/vnd.openofficeorg.extension");
        // Other
        put("txt", "text/plain");
        put("rtf", "application/rtf");
        put("pdf", "application/pdf");
    }};

}
