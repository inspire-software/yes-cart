/*
 * Copyright 2009 Inspire-Software.com
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

package org.yes.cart.utils;

/**
 * User: denispavlov
 * Date: 18/05/2019
 * Time: 15:51
 */
public final class MessageFormatUtils {

    private static final String PLACEHOLDER = "{}";

    public static String format(final String template, final Object... args) {

        if (template == null) {
            return "";
        }

        if (args == null || args.length == 0) {
            return template; // nothing to replace with
        }

        int index = 0;
        int next = template.indexOf(PLACEHOLDER);
        if (next == -1) {
            return template;
        }

        String remainder = template;
        final StringBuilder out = new StringBuilder();
        while (next != -1) {
            out.append(remainder.substring(0, next));
            final Object arg = nextArg(args, index++);
            out.append(arg);
            remainder = remainder.substring(next + PLACEHOLDER.length());
            next = remainder.indexOf(PLACEHOLDER);
        }

        if (remainder.length() > 0) {
            out.append(remainder);
        }

        return out.toString();

    }

    private static Object nextArg(Object[] args, int index) {
        if (args.length > index) {
            return args[index];
        }
        return PLACEHOLDER;
    }

}
