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

package org.yes.cart.service.theme.templates;

import java.io.InputStream;

/**
 * Date: 19/09/2020
 * Time: 12:53
 */
public interface ThemeRepositoryService {

    /**
     * File template provider.
     *
     * @param path relative path in repository
     *
     * @return input stream for template source or null if does not exist
     */
    InputStream getSource(String path);

}
