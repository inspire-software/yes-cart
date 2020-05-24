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

package org.yes.cart.report;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Writer;

/**
 * User: denispavlov
 * Date: 12/04/2019
 * Time: 18:08
 */
public interface ReportObjectStreamFactory {

    /**
     * Get configured object output stream.
     *
     * @param writer given writer
     *
     * @return {@link ObjectOutputStream}
     */
    ObjectOutputStream getObjectOutputStream(final Writer writer) throws IOException;

}
