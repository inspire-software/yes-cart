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

/**
 * User: denispavlov
 * Date: 12/09/2016
 * Time: 15:23
 */
class VoContentServiceUtils {

    static String ensureDynamicContentIsVisible(final String content) {

        String out = content;
        out = parseCodeBlock(out, "<%", "&lt;%", "%>", "%&gt;", "<", "&lt;", ">", "&gt;");
        out = parseCodeBlock(out, "${", "${", "}", "}", "<", "&lt;", ">", "&gt;");

        return out;
    }

    static String parseCodeBlock(final String content,
                                 final String open,
                                 final String openSafe,
                                 final String close,
                                 final String closeSafe,
                                 final String lessFrom,
                                 final String lessTo,
                                 final String moreFrom,
                                 final String moreTo) {

        int posStart = content.indexOf(open);
        // possible block start
        if (posStart != -1) {

            int posEnd = content.indexOf(close, posStart);

            // block part has an end tag
            if (posEnd != -1) {

                final StringBuilder out = new StringBuilder();

                out.append(content.substring(0, posStart));

                // add this part
                out.append(openSafe);
                out.append(content.substring(posStart + open.length(), posEnd).replace(lessFrom, lessTo).replace(moreFrom, moreTo));
                out.append(closeSafe);

                out.append(parseCodeBlock(content.substring(posEnd + close.length()), open, openSafe, close, closeSafe, lessFrom, lessTo, moreFrom, moreTo));

                return out.toString();

            }

        }

        // no block
        return  content;

    }

    static String ensureDynamicContentIsValid(final String edited) {

        String out = edited;
        out = parseCodeBlock(out, "&lt;%", "<%", "%&gt;", "%>", "&lt;", "<", "&gt;", ">");
        out = parseCodeBlock(out, "${", "${", "}", "}", "&lt;", "<", "&gt;", ">");

        return out;

    }


}
