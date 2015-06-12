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

import org.apache.commons.lang.StringUtils;

/**
 * User: denispavlov
 * Date: 02/04/2015
 * Time: 11:26
 */
public class StringIsNotBlank extends BaseMatcher<String> {

    @Override
    public boolean matches(final Object object) {
        return object instanceof String && StringUtils.isNotBlank((String) object);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("value was blank");
    }
}
