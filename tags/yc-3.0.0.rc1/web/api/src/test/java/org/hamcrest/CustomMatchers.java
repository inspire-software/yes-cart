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

package org.hamcrest;

/**
 * User: denispavlov
 * Date: 02/04/2015
 * Time: 11:30
 */
public class CustomMatchers {

    private CustomMatchers() {
    }

    private static Matcher<String> IS_NOT_BLANK = new StringIsNotBlank();


    /**
     * Matcher for non blank strings.
     *
     * @return matcher
     */
    public static Matcher<String> isNotBlank() {
        return IS_NOT_BLANK;
    }

}
