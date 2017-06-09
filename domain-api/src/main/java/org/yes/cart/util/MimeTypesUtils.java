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

package org.yes.cart.util;

import org.apache.commons.codec.binary.Base64;

import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * User: denispavlov
 * Date: 08/06/2017
 * Time: 17:49
 */
public final class MimeTypesUtils {

    private MimeTypesUtils() {
        // no instance
    }

    /**
     * Encode content into Base64 URL
     *
     * @param contentType content type
     * @param content     content as bytes
     *
     * @return Base64 URL
     */
    public static String getBase64DataURL(final String contentType, byte[] content) {
        final StringBuilder out = new StringBuilder();
        out.append("data:").append(contentType).append(";base64,").append(Base64.encodeBase64String(content));
        return out.toString();
    }

    /**
     * Detect MIME type from file name.
     *
     * @param fileName file name
     *
     * @return MIME type
     */
    public static String getMimeType(String fileName) {

        final FileNameMap mimeTypes = URLConnection.getFileNameMap();
        final String contentType = mimeTypes.getContentTypeFor(fileName);

        // nothing found -> look up our in extension map to find manually mapped types
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
